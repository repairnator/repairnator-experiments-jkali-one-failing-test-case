package fr.inria.spirals.repairnator.process.step.paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.inria.spirals.repairnator.process.inspectors.StepStatus;
import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.states.PipelineState;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.maven.MavenHelper;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by urli on 08/02/2017.
 */
public class ComputeSourceDir extends AbstractStep {
    private static final String DEFAULT_SRC_DIR = "/src/main/java";
    private static final String COMPUTE_TOTAL_CLOC = "cloc --json --vcs=git .";

    private boolean allModules;
    private Set<File> visitedFiles = new HashSet<>();

    public ComputeSourceDir(ProjectInspector inspector, boolean blockingStep, boolean allModules) {
        super(inspector, blockingStep);
        this.allModules = allModules;
    }

    public ComputeSourceDir(ProjectInspector inspector, boolean blockingStep, String name, boolean allModules) {
        super(inspector, blockingStep, name);
        this.allModules = allModules;
    }

    private File[] searchForSourcesDirectory(String incriminatedModulePath, boolean rootCall) {
        List<File> result = new ArrayList<File>();
        File defaultSourceDir = new File(incriminatedModulePath + DEFAULT_SRC_DIR);

        boolean wasDefaultSourceDirFound = false;
        if (defaultSourceDir.exists()) {
            wasDefaultSourceDirFound = true;
            result.add(defaultSourceDir);
            if (!this.allModules) {
                return result.toArray(new File[result.size()]);
            }
        } else {
            this.getLogger().debug("The default source directory (" + defaultSourceDir.getPath()
                    + ") does not exist. Try to read pom.xml to get information.");
        }
        File pomIncriminatedModule = new File(incriminatedModulePath + "/pom.xml");

        if (!pomIncriminatedModule.exists()) {
            pomIncriminatedModule = new File(this.getPom());
        }

        if (this.visitedFiles.contains(pomIncriminatedModule)) {
            this.getLogger().info("It seems we are entering in a loop while searching the source dir. The following file has already been visited: "+pomIncriminatedModule.getAbsolutePath());
            return result.toArray(new File[0]);
        } else {
            this.visitedFiles.add(pomIncriminatedModule);
        }

        try {
            Model model = MavenHelper.readPomXml(pomIncriminatedModule, this.getInspector().getM2LocalPath());
            if (model == null) {
                this.addStepError("Error while building model: no model has been retrieved.");
                return null;
            }
            if (!wasDefaultSourceDirFound) {
                Build buildSection = model.getBuild();

                if (buildSection != null && buildSection.getSourceDirectory() != null) {
                    String pathSrcDirFromPom = buildSection.getSourceDirectory();

                    if (pathSrcDirFromPom != null) {
                        File srcDirFromPom = new File(pathSrcDirFromPom);

                        if (srcDirFromPom.exists()) {
                            result.add(srcDirFromPom);
                            return result.toArray(new File[result.size()]);
                        }

                        this.getLogger().debug("The source directory given in pom.xml (" + pathSrcDirFromPom
                                + ") does not exists. Try to get source dir from all modules if multimodule.");
                    } else {
                        this.getLogger().debug("The source directory has not been found in pom.xml. Try to get source dir from all modules.");
                    }

                } else {
                    this.getLogger().debug(
                            "Build section does not exist in this pom.xml. Try to get source dir from all modules.");
                }
            }

            for (String module : model.getModules()) {
                File[] srcDir = this.searchForSourcesDirectory(pomIncriminatedModule.getParent() + File.separator + module,
                        false);
                if (srcDir != null) {
                    result.addAll(Arrays.asList(srcDir));
                }
            }

            if (result.size() > 0) {
                return result.toArray(new File[result.size()]);
            }

            if (model.getParent() != null && rootCall) {
                String relativePath = "../pom.xml";

                if (model.getParent().getRelativePath() != null) {
                    relativePath = model.getParent().getRelativePath();
                }

                File parentPomXml = new File(incriminatedModulePath + File.separator + relativePath);

                if (parentPomXml.exists()) {
                    File[] srcDir = this.searchForSourcesDirectory(parentPomXml.getParent(),false);
                    if (srcDir != null) {
                        result.addAll(Arrays.asList(srcDir));
                    }

                    if (result.size() > 0) {
                        return result.toArray(new File[result.size()]);
                    }
                }
            }


        } catch (ModelBuildingException e) {
            this.addStepError("Error while building pom.xml model: " + e);
        }

        this.addStepError(
                "Source directory is not at default location or specified in build section and no parent can be found.");
        return null;
    }

    private void computeMetricsOnSources(File[] sources) {
        int totalAppFiles = 0;
        if (sources != null && sources.length > 0) {
            for (File f : sources) {
                int nbFile = FileUtils.listFiles(f, new String[] {"java"}, true).size();
                totalAppFiles += nbFile;
            }
            this.getInspector().getJobStatus().getMetrics().setNbFileApp(totalAppFiles);
            this.getInspector().getJobStatus().getMetrics4Bears().getProjectMetrics().setNumberSourceFiles(totalAppFiles);
        }
    }

    private void computeMetricsOnCompleteRepo() {
        this.getLogger().debug("Compute the line of code of the project");
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh","-c",COMPUTE_TOTAL_CLOC)
                .directory(new File(this.getInspector().getRepoLocalPath()));

        try {
            Process p = processBuilder.start();
            BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.waitFor();

            this.getLogger().debug("Get result from cloc process...");
            String processReturn = "";
            String line;
            while (stdin.ready() && (line = stdin.readLine()) != null) {
                processReturn += line;
            }

            Gson gson = new GsonBuilder().create();
            JsonObject json = gson.fromJson(processReturn, JsonObject.class);

            this.getInspector().getJobStatus().getMetrics().setSizeProjectLOC(json);
        } catch (IOException | InterruptedException e) {
            this.getLogger().error("Error while computing metrics on source code of the whole repo.", e);
        }
    }

    @Override
    protected StepStatus businessExecute() {
        this.getLogger().debug("Computing the source directory ...");
        String incriminatedModule = (this.allModules) ? this.getInspector().getRepoLocalPath() : this.getInspector().getJobStatus().getFailingModulePath();

        File[] sources = this.searchForSourcesDirectory(incriminatedModule, true);

        if (allModules) {
            this.computeMetricsOnCompleteRepo();
            this.computeMetricsOnSources(sources);
        }

        if (sources == null || sources.length == 0) {
            this.addStepError("Fail to find the sources directory.");
            this.getInspector().getJobStatus().setRepairSourceDir(null);
            return StepStatus.buildError(this, PipelineState.SOURCEDIRNOTCOMPUTED);
        } else {
            if (sources.length == 1) {
                this.getLogger().info(sources.length+" one source dir was found:");
            } else {
                this.getLogger().info(sources.length+" source dirs were found:");
            }
            for (File file : sources) {
                this.getLogger().info(file.getAbsolutePath());
            }

            this.getInspector().getJobStatus().setRepairSourceDir(sources);
            return StepStatus.buildSuccess(this);
        }
    }

}
