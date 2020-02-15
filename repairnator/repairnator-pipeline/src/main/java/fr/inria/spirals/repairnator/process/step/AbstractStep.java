package fr.inria.spirals.repairnator.process.step;

import fr.inria.spirals.repairnator.Utils;
import fr.inria.spirals.repairnator.process.inspectors.JobStatus;
import fr.inria.spirals.repairnator.process.inspectors.Metrics;
import fr.inria.spirals.repairnator.process.inspectors.StepStatus;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.notifier.AbstractNotifier;
import fr.inria.spirals.repairnator.process.inspectors.*;
import fr.inria.spirals.repairnator.process.inspectors.metrics4bears.reproductionBuggyBuild.ReproductionBuggyBuild;
import fr.inria.spirals.repairnator.serializer.AbstractDataSerializer;
import fr.inria.spirals.repairnator.states.LauncherMode;
import fr.inria.spirals.repairnator.states.PushState;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by urli on 03/01/2017.
 */
public abstract class AbstractStep {
    /**
     * The name of the step, by default it's the class name
     * We can use a custom name to distinguish two different instances.
     */
    private String name;

    private ProjectInspector inspector;

    private boolean shouldStop;
    private AbstractStep nextStep;
    private Date dateBegin;
    private Date dateEnd;
    private long timeBegin;
    private long timeEnd;
    private boolean pomLocationTested;
    private List<AbstractDataSerializer> serializers;
    private List<AbstractNotifier> notifiers;
    private RepairnatorConfig config;

    private StepStatus stepStatus;
    private PushState pushState;

    /**
     * If set to true, the failure of the step means a stop of the entire pipeline.
     */
    private boolean blockingStep;

    public AbstractStep(ProjectInspector inspector, boolean blockingStep) {
        this(inspector, blockingStep, "");
        this.name = this.getClass().getSimpleName();
    }

    public AbstractStep(ProjectInspector inspector, boolean blockingStep, String name) {
        this.name = name;
        this.shouldStop = false;
        this.pomLocationTested = false;
        this.serializers = new ArrayList<>();
        this.config = RepairnatorConfig.getInstance();
        this.blockingStep = blockingStep;
        this.setProjectInspector(inspector);
    }

    public void setBlockingStep(boolean blockingStep) {
        this.blockingStep = blockingStep;
    }

    public boolean isBlockingStep() {
        return blockingStep;
    }

    protected void initStates() {
        if (this.inspector != null) {
            this.setPushState(PushState.NONE);
        }
    }

    public void setNotifiers(List<AbstractNotifier> notifiers) {
        if (notifiers != null) {
            this.notifiers = notifiers;
            if (this.nextStep != null) {
                this.nextStep.setNotifiers(notifiers);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setDataSerializer(List<AbstractDataSerializer> serializers) {
        if (serializers != null) {
            this.serializers = serializers;
            if (this.nextStep != null) {
                this.nextStep.setDataSerializer(serializers);
            }
        }
    }

    public ProjectInspector getInspector() {
        return inspector;
    }

    public AbstractStep setNextStep(AbstractStep nextStep) {
        if (this.nextStep != null) {
            this.nextStep.setNextStep(nextStep);
        } else {
            this.nextStep = nextStep;
            nextStep.setDataSerializer(this.serializers);
            nextStep.setNotifiers(this.notifiers);
        }
        return this;
    }

    protected void setPushState(PushState pushState) {
        if (pushState != null) {
            this.pushState = pushState;
            this.inspector.getJobStatus().addPushState(this.pushState);
            if (this.nextStep != null) {
                this.nextStep.setPushState(pushState);
            }
        }
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    public void addStepError(String error) {
        getLogger().error(error);
        this.inspector.getJobStatus().addStepError(this.name, error);
    }

    public void addStepError(String error, Throwable exception) {
        getLogger().error(error, exception);
        this.inspector.getJobStatus().addStepError(this.name, error + " Original msg: " + exception.getMessage());
    }

    protected void executeNextStep() {
        this.observeAndNotify();
        if (this.nextStep != null) {
            this.nextStep.execute();
        } else {
            this.terminatePipeline();
        }
    }

    private void serializeData() {
        if (serializers != null) {
            if (this.getConfig().getLauncherMode() == LauncherMode.BEARS) {
                this.getLogger().info("Serialize all data for the pair of builds " +
                        this.getInspector().getBuggyBuild().getId() + ", " + this.getInspector().getPatchedBuild().getId());
            } else {
                this.getLogger().info("Serialize all data for build: " + this.getInspector().getBuggyBuild().getId());
            }
            for (AbstractDataSerializer serializer : this.serializers) {
                serializer.serializeData(this.inspector);
            }
        }
    }

    protected void forkRepository() {
        ProjectInspector inspector = this.getInspector();
        JobStatus jobStatus = inspector.getJobStatus();

        RepairnatorConfig config = RepairnatorConfig.getInstance();

        if (jobStatus.isHasBeenPatched() && !jobStatus.isHasBeenForked() && config.isFork()) {
            String repositoryName = getInspector().getRepoSlug();
            getLogger().info("Fork the repository: "+repositoryName);
            try {
                String forkedRepoUrl = inspector.getGitHelper().forkRepository(repositoryName, this);
                if (forkedRepoUrl != null) {
                    jobStatus.setForkURL(forkedRepoUrl);
                    jobStatus.setHasBeenForked(true);
                    getLogger().info("Obtain the following fork URL: "+forkedRepoUrl);
                } else {
                    getLogger().error("Error while forking the repository");
                }
            } catch (IOException e) {
                getLogger().error("Error while forking the repository "+repositoryName, e);
            }
        } else {
            getLogger().info("The repository won't be forked.");
        }
    }


    private void observeAndNotify() {
        if (this.notifiers != null) {
            for (AbstractNotifier notifier : this.notifiers) {
                notifier.observe(this.inspector);
            }
        }
    }

    private void testPomLocation() {
        this.pomLocationTested = true;
        File defaultPomFile = new File(this.inspector.getRepoLocalPath() + File.separator + Utils.POM_FILE);

        if (defaultPomFile.exists()) {
            return;
        } else {
            this.getLogger().info("The pom.xml file is not at the root of the repository. Start to search it in folders...");

            File rootRepo = new File(this.inspector.getRepoLocalPath());

            File[] dirs = rootRepo.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            if (dirs != null) {
                Arrays.sort(dirs);
                for (File dir : dirs) {
                    File gitDir = new File(dir.getPath()+File.separator+".git");

                    if (gitDir.exists()) {
                        this.getLogger().debug("Skip folder "+dir.getPath()+": it is a git sub-module project.");
                        continue;
                    }

                    File pomFile = new File(dir.getPath()+File.separator+Utils.POM_FILE);
                  
                    if (pomFile.exists()) {
                        this.getLogger().info("A pom.xml was found in the following directory: "+dir.getPath());
                        this.inspector.getJobStatus().setPomDirPath(dir.getPath());
                        return;
                    }
                }
            }

            this.addStepError("RepairNator was unable to found a pom.xml in the repository. It will stop now.");
            this.shouldStop = true;
        }
    }

    protected String getPom() {
        if (!pomLocationTested) {
            testPomLocation();
            this.inspector.getJobStatus().getMetrics4Bears().getReproductionBuggyBuild()
                    .setProjectRootPomPath(this.inspector.getJobStatus().getPomDirPath() + File.separator + Utils.POM_FILE);
        }
        return this.inspector.getJobStatus().getPomDirPath() + File.separator + Utils.POM_FILE;
    }

    protected void cleanMavenArtifacts() {
        if (this.inspector.getM2LocalPath() != null) {
            try {
                FileUtils.deleteDirectory(this.inspector.getM2LocalPath());
            } catch (IOException e) {
                getLogger().warn(
                        "Error while deleting the M2 local directory (" + this.inspector.getM2LocalPath() + "): " + e);
            }
        }

        if (this.config.isClean()) {
            try {
                FileUtils.deleteDirectory(this.inspector.getRepoLocalPath());
            } catch (IOException e) {
                getLogger().warn("Error while deleting the workspace directory (" + this.inspector.getRepoLocalPath()
                        + "): " + e);
            }
        }
    }

    public void setProjectInspector(ProjectInspector inspector) {
        if (inspector != null) {
            this.inspector = inspector;
            this.inspector.registerStep(this);
            this.initStates();
        }
    }

    public boolean isShouldStop() {
        return this.shouldStop;
    }

    public void execute() {
        List<AbstractStep> steps = this.inspector.getSteps();
        this.getLogger().debug("----------------------------------------------------------------------");
        this.getLogger().debug("STEP "+ (steps.indexOf(this) + 1)+"/"+ steps.size() +": "+this.name);
        this.getLogger().debug("----------------------------------------------------------------------");

        this.dateBegin = new Date();
        this.timeBegin = dateBegin.getTime();
        this.stepStatus = this.businessExecute();
        this.dateEnd = new Date();
        this.timeEnd = dateEnd.getTime();

        this.getLogger().debug("STEP STATUS: "+this.stepStatus);
        this.getLogger().debug("STEP DURATION: "+getDuration()+"s");

        Metrics metric = this.inspector.getJobStatus().getMetrics();
        metric.addStepDuration(this.name, getDuration());
        metric.addFreeMemoryByStep(this.name, Runtime.getRuntime().freeMemory());

        ReproductionBuggyBuild reproductionBuggyBuild = this.inspector.getJobStatus().getMetrics4Bears().getReproductionBuggyBuild();
        reproductionBuggyBuild.addStep(this);

        this.inspector.getJobStatus().addStepStatus(this.stepStatus);

        this.shouldStop = this.shouldStop || (this.isBlockingStep() && !this.stepStatus.isSuccess());
        if (!this.shouldStop) {
            this.getLogger().debug("EXECUTE NEXT STEP");
            this.executeNextStep();
        } else {
            this.getLogger().debug("TERMINATE PIPELINE");
            this.terminatePipeline();
        }
    }

    private void terminatePipeline() {
        if (!this.inspector.isPipelineEnding()) {
            this.inspector.setPipelineEnding(true);
            this.recordMetrics();
            if (this.inspector.getFinalStep() != null) {
                if ((!(this.getInspector() instanceof ProjectInspector4Bears) && // Repairnator
                        this.getInspector().getJobStatus().isReproducedAsFail()) // A bug was reproduced
                        ||
                        (this.getInspector() instanceof ProjectInspector4Bears && // Bears
                        ((ProjectInspector4Bears) this.getInspector()).isBug())) { // A bug and its patch were reproduced
                    this.inspector.getFinalStep().execute();
                } else {
                    if (this.getInspector() instanceof ProjectInspector4Bears) {
                        this.getLogger().debug("FINAL STEPS SKIPPED: The reproduction of the bug and/or the patch failed.");
                    } else {
                        this.getLogger().debug("FINAL STEPS SKIPPED: The reproduction of the bug failed.");
                    }
                    // So the final push state is repo not pushed
                    this.setPushState(PushState.REPO_NOT_PUSHED);
                }
            }
            this.serializeData();
            this.cleanMavenArtifacts();
            this.inspector.printPipelineEnd();
        }
    }

    private void recordMetrics() {
        Metrics metric = this.inspector.getJobStatus().getMetrics();

        metric.setFreeMemory(Runtime.getRuntime().freeMemory());
        metric.setTotalMemory(Runtime.getRuntime().totalMemory());
        metric.setNbCPU(Runtime.getRuntime().availableProcessors());

        this.getInspector().getJobStatus().writeProperty("metrics", metric);
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public int getDuration() {
        if (timeEnd == 0 || timeBegin == 0) {
            return 0;
        }
        return Math.round((timeEnd - timeBegin) / 1000);
    }

    public RepairnatorConfig getConfig() {
        return config;
    }

    protected abstract StepStatus businessExecute();
}
