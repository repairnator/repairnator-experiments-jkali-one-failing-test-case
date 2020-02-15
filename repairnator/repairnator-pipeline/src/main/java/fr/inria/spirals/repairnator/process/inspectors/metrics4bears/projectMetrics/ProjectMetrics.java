package fr.inria.spirals.repairnator.process.inspectors.metrics4bears.projectMetrics;

public class ProjectMetrics {

    /**
     * {@code numberModules} is calculated in the step
     * {@link fr.inria.spirals.repairnator.process.step.paths.ComputeModules}.
     */
    private int numberModules;
    /**
     * {@code numberSourceFiles} is calculated in the step
     * {@link fr.inria.spirals.repairnator.process.step.paths.ComputeSourceDir}.
     */
    private int numberSourceFiles;
    /**
     * {@code numberTestFiles} is calculated in the step
     * {@link fr.inria.spirals.repairnator.process.step.paths.ComputeTestDir}.
     */
    private int numberTestFiles;
    /**
     * {@code numberLibrariesFailingModule} is calculated in the step
     * {@link fr.inria.spirals.repairnator.process.step.paths.ComputeClasspath}.
     */
    private int numberLibrariesFailingModule;

    public ProjectMetrics() {}

    public int getNumberModules() {
        return numberModules;
    }

    public void setNumberModules(int numberModules) {
        this.numberModules = numberModules;
    }

    public int getNumberSourceFiles() {
        return numberSourceFiles;
    }

    public void setNumberSourceFiles(int numberSourceFiles) {
        this.numberSourceFiles = numberSourceFiles;
    }

    public int getNumberTestFiles() {
        return numberTestFiles;
    }

    public void setNumberTestFiles(int numberTestFiles) {
        this.numberTestFiles = numberTestFiles;
    }

    public int getNumberLibrariesFailingModule() {
        return numberLibrariesFailingModule;
    }

    public void setNumberLibrariesFailingModule(int numberLibrariesFailingModule) {
        this.numberLibrariesFailingModule = numberLibrariesFailingModule;
    }

}
