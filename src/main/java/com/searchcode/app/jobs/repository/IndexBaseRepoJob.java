/*
 * Copyright (c) 2016 Boyter Online Services
 *
 * Use of this software is governed by the Fair Source License included
 * in the LICENSE.TXT file, but will be eventually open under GNU General Public License Version 3
 * see the README.md for when this clause will take effect
 *
 * Version 1.3.13
 */

package com.searchcode.app.jobs.repository;


import au.com.bytecode.opencsv.CSVWriter;
import com.searchcode.app.config.Values;
import com.searchcode.app.dto.BinaryFinding;
import com.searchcode.app.dto.RepositoryChanged;
import com.searchcode.app.dto.RunningIndexJob;
import com.searchcode.app.model.RepoResult;
import com.searchcode.app.service.IIndexService;
import com.searchcode.app.service.IndexService;
import com.searchcode.app.service.Singleton;
import com.searchcode.app.util.Properties;
import com.searchcode.app.util.SearchCodeLib;
import com.searchcode.app.util.UniqueRepoQueue;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

public abstract class IndexBaseRepoJob implements Job {

    public boolean LOWMEMORY = true;
    public int SLEEPTIME = 5000;
    public int MAXFILELINEDEPTH = Singleton.getHelpers().tryParseInt(Properties.getProperties().getProperty(Values.MAXFILELINEDEPTH, Values.DEFAULTMAXFILELINEDEPTH), Values.DEFAULTMAXFILELINEDEPTH);
    public boolean LOGINDEXED = Boolean.parseBoolean(Properties.getProperties().getProperty(Values.LOG_INDEXED, "false"));
    public boolean FOLLOWLINKS = Boolean.parseBoolean(Properties.getProperties().getProperty(Values.FOLLOW_LINKS, Values.DEFAULT_FOLLOW_LINKS));
    public boolean DELETEREPO = Boolean.parseBoolean(Properties.getProperties().getProperty(Values.DELETE_REPO_AFTER_PROCESS, Values.DEFAULT_DELETE_REPO_AFTER_PROCESS));
    public boolean haveRepoResult = false;
    public IndexService indexService = Singleton.getIndexService();

    public RepositoryChanged updateExistingRepository(RepoResult repoResult, String repoLocations, boolean useCredentials) {
        return null;
    }

    /**
     * This method to be implemented by the extending class
     */
    public RepositoryChanged getNewRepository(RepoResult repoResult, String repoLocations, boolean useCredentials) {
        return null;
    }

    /**
     * This method to be implemented by the extending class
     */
    public UniqueRepoQueue getNextQueuedRepo() {
        return null;
    }

    /**
     * This method to be implemented by the extending class
     */
    public String getCodeOwner(List<String> codeLines, String newString, String repoName, String fileRepoLocations, SearchCodeLib scl) {
        return null;
    }

    /**
     * This method to be implemented by the extending class
     */
    public boolean isEnabled() {
        return false;
    }

    public boolean ignoreFile(String fileParent) {
        return true;
    }


    /**
     * Check the file time against
     * @return
     */
    public boolean isUpdated(String fileLocation, Instant lastRunTime) {
        if (this.indexService.getReindexingAll()) {
            return true;
        }

        File file = new File(fileLocation);
        long lastModified = file.lastModified();
        long truncatedNow = lastRunTime.minusSeconds(3600).toEpochMilli();

        if (lastModified <= truncatedNow ) {
            // Skip the file because we in theory already indexed it
            return false;
        }

        return true;
    }

    /**
     * This method can be implemented by extending class but is not required
     * as it requires project name at the front
     */
    public String getFileLocationFilename(String fileToString, String fileRepoLocations) {
        return fileToString.replace(fileRepoLocations, Values.EMPTYSTRING);
    }

    /**
     * The main method used for finding jobs to index and actually doing the work
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        this.LOWMEMORY = Boolean.parseBoolean(jobDataMap.get("LOWMEMORY").toString());
        String repoLocations = jobDataMap.get("REPOLOCATIONS").toString();

        if (!isEnabled() || this.indexService.shouldPause(IIndexService.JobType.REPO_PARSER)) {
            Singleton.getLogger().info("Pausing parser.");
            return;
        }

        // Pull the next repo to index from the queue
        RepoResult repoResult = this.getNextQueuedRepo().poll();

        if (repoResult != null && !Singleton.getRunningIndexRepoJobs().containsKey(repoResult.getName())) {
            this.haveRepoResult = true;
            Singleton.getLogger().info("Indexing " + repoResult.getName());
            repoResult.getData().indexStatus = "indexing";
            repoResult.getData().indexError = Values.EMPTYSTRING;
            Singleton.getRepo().saveRepo(repoResult);

            try {
                Singleton.getRunningIndexRepoJobs().put(repoResult.getName(),
                        new RunningIndexJob("Indexing", Singleton.getHelpers().getCurrentTimeSeconds()));

                this.checkCloneSuccess(repoResult.getDirectoryName(), repoLocations);

                String repoGitLocation = repoLocations + "/" + repoResult.getDirectoryName() + "/.git/";

                File file = new File(repoGitLocation);
                boolean existingRepo = file.exists(); // TODO this assumes git every time? Correct????
                boolean useCredentials = repoResult.getUsername() != null && !repoResult.getUsername().isEmpty();
                RepositoryChanged repositoryChanged;

                if (existingRepo) {
                    repositoryChanged = this.updateExistingRepository(repoResult, repoLocations, useCredentials);
                } else {
                    repositoryChanged = this.getNewRepository(repoResult, repoLocations, useCredentials);
                }

                // Write file indicating we have successfully cloned
                this.createCloneUpdateSuccess(repoLocations + "/" + repoResult.getDirectoryName());
                this.triggerIndex(repoResult, repoResult.getName(), repoResult.getUrl(), repoLocations, repoGitLocation, existingRepo, repositoryChanged);

                if (this.DELETEREPO) {
                    Singleton.getHelpers().tryDelete(repoLocations + "/" + repoResult.getDirectoryName());
                }
            }
            catch (Exception ex) {
                Singleton.getLogger().severe("ERROR - caught a " + ex.getClass() + " in " + this.getClass() + ".execute with message: " + ex.getMessage());
            }
            finally {
                // Clean up the job
                // Mark that this job is finished
                // TODO ensure that this line is covered by tests
                this.indexService.decrementRepoJobsCount();
                Singleton.getRunningIndexRepoJobs().remove(repoResult.getName());
            }
        }
    }

    public void triggerIndex(RepoResult repoResult, String repoName, String repoRemoteLocation, String repoLocations, String repoGitLocation, boolean existingRepo, RepositoryChanged repositoryChanged) {
        Instant jobStartTime = Instant.now();
        Singleton.getLogger().info("Update found indexing " + repoRemoteLocation);
        this.updateIndex(repoResult, repoLocations, repoRemoteLocation, existingRepo, repositoryChanged);

        int runningTime = Singleton.getHelpers().getCurrentTimeSeconds() - Singleton.getRunningIndexRepoJobs().get(repoResult.getName()).startTime;
        repoResult.getData().averageIndexTimeSeconds = (repoResult.getData().averageIndexTimeSeconds + runningTime) / 2;
        repoResult.getData().indexStatus = "success";
        repoResult.getData().jobRunTime = jobStartTime;
        Singleton.getRepo().saveRepo(repoResult);
    }

    /**
     * Checks if a clone operation of a repository was successful. If not
     * then it will delete the folder to start again
     */
    public boolean checkCloneSuccess(String repoName, String repoLocations) {
        if (Singleton.getHelpers().isNullEmptyOrWhitespace(repoName) && Singleton.getHelpers().isNullEmptyOrWhitespace(repoLocations)) {
            Singleton.getLogger().warning("Repository Location is set to nothing, this can cause searchcode to modify the root file system!");
            return false;
        }

        // Check if sucessfully cloned, and if not delete and restart
        boolean cloneSucess = this.checkCloneUpdateSucess(repoLocations + repoName);
        if (!cloneSucess) {
            // Delete the folder and delete from the index
            try {
                File filePath = new File(repoLocations + "/" + repoName + "/");
                if (!filePath.getAbsolutePath().equals("/")) { // Lets really be sure....
                    FileUtils.deleteDirectory(filePath);
                }

                Optional<RepoResult> repoByName = Singleton.getRepo().getRepoByName(repoName);

                repoByName.ifPresent(x -> {
                    try {
                        this.indexService.deleteByRepo(x);
                    } catch (IOException ex) {
                        Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() + "\n with message: " + ex.getMessage());
                    }
                });

            } catch (IOException ex) {
                Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() + "\n with message: " + ex.getMessage());
            }
        }
        // TODO is this correct?!
        this.deleteCloneUpdateSuccess(repoLocations + "/" + repoName);

        return true;
    }

    /**
     * Checks if the repository has changed, or if the last index failed for some reason
     * and if either condition is true triggers a full index otherwise triggers a delta
     * index of the files.
     */
    public void updateIndex(RepoResult repoResult, String repoLocations, String repoRemoteLocation, boolean existingRepo, RepositoryChanged repositoryChanged) {
        String repoGitLocation = repoLocations + "/" + repoResult.getDirectoryName();
        Path docDir = Paths.get(repoGitLocation);

        Singleton.getLogger().info("Doing full index of files for " + repoResult.getName());
        this.indexDocsByPath(docDir, repoResult, repoLocations, repoRemoteLocation, existingRepo);

        // Write file indicating that the index was sucessful
        Singleton.getLogger().info("Successfully processed writing index success for " + repoResult.getName());
    }

    /**
     * Indexes all the documents in the path provided. Will also remove anything from the index if not on disk
     * Generally this is a slow update used only for the initial clone of a repository
     * NB this can be used for updates but it will be much slower as it needs to to walk the contents of the disk
     */
    public void indexDocsByPath(Path path, RepoResult repoResult, String repoLocations, String repoRemoteLocation, boolean existingRepo) {

        String fileRepoLocations = FilenameUtils.separatorsToUnix(repoLocations);
        SearchcodeFileVisitor<Path> searchcodeFileVisitor = new SearchcodeFileVisitor<>(this, repoResult, fileRepoLocations, repoRemoteLocation);

        try {
            if (this.FOLLOWLINKS) {
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, searchcodeFileVisitor);
            }
            else {
                Files.walkFileTree(path, searchcodeFileVisitor);
            }

        } catch (IOException ex) {
            Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() +  " indexDocsByPath walkFileTree\n with message: " + ex.getMessage());
        }

        if (this.LOGINDEXED) {
            logIndexed(repoResult.getName(), searchcodeFileVisitor.reportList);
        }

        if (existingRepo) {
            this.cleanMissingPathFiles(repoResult.getName(), searchcodeFileVisitor.fileLocationsMap);
        }
    }

    /**
     * Method to remove from the index files that are no longer required
     */
    public void cleanMissingPathFiles(String repoName, Map<String, String> fileLocations) {
        int page = 0;
        boolean doClean = true;

        while (doClean) {
            List<String> indexLocations = this.indexService.getRepoDocuments(repoName, page);
            Singleton.getLogger().info("cleanMissingPathFiles doClean " + page + " " + indexLocations.size());

            if (indexLocations.isEmpty()) {
                doClean = false;
            }

            for (String file: indexLocations) {
                if (!fileLocations.containsKey(file)) {
                    Singleton.getLogger().info("Missing from disk, removing from index " + file);
                    try {
                        this.indexService.deleteByCodeId(DigestUtils.sha1Hex(file));
                    } catch (IOException ex) {
                        Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() + " indexDocsByPath deleteByFileLocationFilename for " + repoName + " " + file + "\n with message: " + ex.getMessage());
                    }
                }
            }

            page++;
        }
    }


    /**
     * Returns a string the represents where the file lives inside the repository
     */
    public String getRelativeToProjectPath(String projectPath, String filePath) {
        if (projectPath.charAt(projectPath.length() - 1) == '/') {
            projectPath = projectPath.substring(0, projectPath.length() - 1);
        }
        projectPath = projectPath.replace("//", "/");
        filePath = filePath.replace("//", "/");

        return filePath.replace(projectPath, Values.EMPTYSTRING);
    }


    /**
     * Attempts to get MD5 for file on disk
     */
    public String getFileMd5(String fileName) {
        String md5Hash = Values.EMPTYSTRING;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(new File(fileName));
            md5Hash = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        } catch (IOException ex) {
            Singleton.getLogger().warning("Unable to generate MD5 for " + fileName);
        }
        finally {
            IOUtils.closeQuietly(fis);
        }

        return md5Hash;
    }

    /**
     * Checks if the job should pause and if so loop endlessly sleeping. Returns
     * true if the job should be terminated and false if it should continue to run
     */
    public boolean shouldJobPauseOrTerminate() {
        if (this.indexService.shouldExit(IIndexService.JobType.REPO_PARSER)) {
            return true;
        }

        while (this.indexService.shouldPause(IIndexService.JobType.REPO_PARSER)) {
            Singleton.getLogger().info("Pausing parser.");
            try {
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException ignored) {}
        }

        return false;
    }

    /**
     * Logs to the logs directory a formatted CSV of the supplied list strings
     */
    private void logIndexed(String repoName, List<String[]> reportList) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(Singleton.getHelpers().getLogPath() + repoName + ".csv.tmp"));
            writer.writeAll(reportList);
            writer.flush();
            writer.close();

            Path source = Paths.get(Singleton.getHelpers().getLogPath() + repoName + ".csv.tmp");
            Files.move(source, source.resolveSibling(repoName + ".csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() + " logIndexed for " + repoName + "\n with message: " + ex.getMessage());
        }
    }

    /*
     * The below are shared between the delta updates and path walk
     */

    public CodeLinesReturn getCodeLines(String changedFile, List<String[]> reportList) {
        List<String> codeLines = new ArrayList<>();
        boolean error = false;

        try {
            codeLines = Singleton.getHelpers().readFileLinesGuessEncoding(changedFile, this.MAXFILELINEDEPTH);
        } catch (IOException ex) {
            error = true;
            Singleton.getLogger().warning("ERROR - caught a " + ex.getClass() + " in " + this.getClass() +  "\n with message: " + ex.getMessage());
            if (this.LOGINDEXED) {
                reportList.add(new String[]{changedFile, "excluded", "unable to guess guess file encoding"});
            }
        }

        return new CodeLinesReturn(codeLines, reportList, error);
    }

    public IsMinifiedReturn getIsMinified(List<String> codeLines, String fileName, List<String[]> reportList) {
        boolean isMinified = false;

        if (Singleton.getSearchCodeLib().isMinified(codeLines, fileName)) {
            isMinified = true;
            Singleton.getLogger().info("Appears to be minified will not index  " + fileName);
            if (this.LOGINDEXED) {
                reportList.add(new String[]{fileName, "excluded", "appears to be minified"});
            }
        }

        return new IsMinifiedReturn(isMinified, reportList);
    }

    public boolean determineBinary(String fileLocation, String fileName, List<String> codeLines, List<String[]> reportList) {
        BinaryFinding binaryFinding = Singleton.getSearchCodeLib().isBinary(codeLines, fileName);

        if (binaryFinding.isBinary()) {
            Singleton.getLogger().info("Appears to be binary will not index " + binaryFinding.getReason() + " " + fileLocation);
            if (this.LOGINDEXED) {
                reportList.add(new String[]{fileLocation, "excluded", binaryFinding.getReason()});
            }
            return true;
        }

        return false;
    }

    public boolean checkIfEmpty(List<String> codeLines, String filename, List<String[]> reportList) {
        if (codeLines.isEmpty()) {
            Singleton.getLogger().info("Unable to guess encoding type or file is empty " + filename);
            if (this.LOGINDEXED) {
                reportList.add(new String[]{filename, "excluded", "empty file"});
            }
            return true;
        }

        return false;
    }


    /*
     * The below are shared among all extending classes
     */
    public String getBlameFilePath(String fileLocationFilename) {
        String[] split = fileLocationFilename.split("/");
        return String.join("/", Arrays.asList(split).subList(1, split.length));
    }

    public void createCloneUpdateSuccess(String repoLocation) {

        try {
            this.createFile(repoLocation, "cloneupdate");
        } catch (IOException e) {
            Singleton.getLogger().warning("Unable to write clone success file. This will cause this repository to re-indexed fresh every time.");
        }
    }

    public void deleteCloneUpdateSuccess(String repoLocation) {
        try {
            this.deleteFile(repoLocation, "cloneupdate");
        } catch (IOException e) {
            Singleton.getLogger().warning("Unable to delete clone update sucess file.");
        }
    }

    public boolean checkCloneUpdateSucess(String repoLocation) {
        return checkFile(repoLocation, "cloneupdate");
    }

    public void createIndexSuccess(String repoLocation) {
        try {
            this.createFile(repoLocation, "index");
        } catch (IOException e) {
            Singleton.getLogger().warning("Unable to create index success file.");
        }
    }

    public void deleteIndexSuccess(String repoLocation) {
        try {
            this.deleteFile(repoLocation, "index");
        } catch (IOException e) {
            Singleton.getLogger().warning("Unable to delete index success file.");
        }
    }

    public boolean checkIndexSucess(String repoLocation) {
        return checkFile(repoLocation, "index");
    }

    private boolean checkFile(String repoLocation, String type) {
        File f = new File(repoLocation + "/searchcode." + type + ".success");
        return f.exists();
    }

    private void createFile(String repoLocation, String type) throws IOException {
        File file = new File(repoLocation, "/searchcode." + type + ".success");
        boolean success = file.mkdir();

        if (!success) {
            throw new IOException("Unable to create directory " + file.getAbsolutePath());
        }
    }

    private void deleteFile(String repoLocation, String type) throws IOException {
        File file = new File(repoLocation, "/searchcode." + type + ".success");
        boolean success = file.delete();

        if (!success) {
            throw new IOException("Unable to delete directory " + file.getAbsolutePath());
        }
    }

    public class CodeLinesReturn {
        private final List<String> codeLines;
        private final boolean error;
        private final List<String[]> reportList;

        public CodeLinesReturn(List<String> codeLines, List<String[]> reportList, boolean error) {
            this.codeLines = codeLines;
            this.reportList = reportList;
            this.error = error;
        }

        public List<String> getCodeLines() {
            return codeLines;
        }

        public List<String[]> getReportList() {
            return reportList;
        }

        public boolean isError() {
            return error;
        }
    }

    public class IsMinifiedReturn {
        private final boolean isMinified;
        private final List<String[]> reportList;

        public IsMinifiedReturn(boolean isMinified, List<String[]> reportList) {
            this.isMinified = isMinified;
            this.reportList = reportList;
        }

        public boolean isMinified() {
            return isMinified;
        }

        public List<String[]> getReportList() {
            return reportList;
        }
    }
}