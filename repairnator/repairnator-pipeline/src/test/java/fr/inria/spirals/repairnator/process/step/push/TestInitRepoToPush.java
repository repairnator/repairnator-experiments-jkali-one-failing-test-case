package fr.inria.spirals.repairnator.process.step.push;

import ch.qos.logback.classic.Level;
import fr.inria.jtravis.entities.Build;
import fr.inria.spirals.repairnator.BuildToBeInspected;
import fr.inria.spirals.repairnator.Utils;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.process.git.GitHelper;
import fr.inria.spirals.repairnator.process.inspectors.JobStatus;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.step.CloneRepository;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;
import fr.inria.spirals.repairnator.process.utils4tests.ProjectInspectorMocker;
import fr.inria.spirals.repairnator.states.PushState;
import fr.inria.spirals.repairnator.states.ScannedBuildStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by urli on 27/04/2017.
 */
public class TestInitRepoToPush {

    private File tmpDir;

    @Before
    public void setup() {
        Utils.setLoggersLevel(Level.INFO);
    }

    @After
    public void tearDown() throws IOException {
        RepairnatorConfig.deleteInstance();
        GitHelper.deleteFile(tmpDir);
    }

    @Test
    public void testInitRepoToPushSimpleCase() throws IOException, GitAPIException {
        long buildId = 207924136; // surli/failingProject build

        RepairnatorConfig repairnatorConfig = RepairnatorConfig.getInstance();
        repairnatorConfig.setClean(false);
        repairnatorConfig.setPush(true);

        Build build = this.checkBuildAndReturn(buildId, false);

        tmpDir = Files.createTempDirectory("test_initRepoToPush").toFile();

        BuildToBeInspected toBeInspected = new BuildToBeInspected(build, null, ScannedBuildStatus.ONLY_FAIL, "");

        JobStatus jobStatus = new JobStatus(tmpDir.getAbsolutePath()+"/repo");

        ProjectInspector inspector = ProjectInspectorMocker.mockProjectInspector(jobStatus, tmpDir, toBeInspected);

        CloneRepository cloneStep = new CloneRepository(inspector);

        cloneStep.setNextStep(new CheckoutBuggyBuild(inspector, true)).setNextStep(new InitRepoToPush(inspector));
        cloneStep.execute();

        assertThat(jobStatus.getPushStates().contains(PushState.REPO_INITIALIZED), is(true));

        Git gitDir = Git.open(new File(tmpDir, "repotopush"));
        Iterable<RevCommit> logs = gitDir.log().call();

        Iterator<RevCommit> iterator = logs.iterator();
        assertThat(iterator.hasNext(), is(true));

        RevCommit commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Bug commit"));
        assertThat(iterator.hasNext(), is(false));
    }

    // FIXME: this test should be moved from here
    @Test
    public void testInitRepoShouldRemoveNotificationInTravisYML() throws IOException {
        long buildId = 331637757;

        RepairnatorConfig repairnatorConfig = RepairnatorConfig.getInstance();
        repairnatorConfig.setClean(false);
        repairnatorConfig.setPush(true);

        Build build = this.checkBuildAndReturn(buildId, true);

        tmpDir = Files.createTempDirectory("test_initRepoToPush").toFile();

        BuildToBeInspected toBeInspected = new BuildToBeInspected(build, null, ScannedBuildStatus.ONLY_FAIL, "");

        JobStatus jobStatus = new JobStatus(tmpDir.getAbsolutePath()+"/repo");

        ProjectInspector inspector = ProjectInspectorMocker.mockProjectInspector(jobStatus, tmpDir, toBeInspected);

        CloneRepository cloneStep = new CloneRepository(inspector);

        cloneStep.setNextStep(new CheckoutBuggyBuild(inspector, true)).setNextStep(new InitRepoToPush(inspector));
        cloneStep.execute();

        assertThat(jobStatus.getPushStates().contains(PushState.REPO_INITIALIZED), is(true));
        File bak = new File(tmpDir.getAbsolutePath()+"/repotopush/bak.travis.yml");
        File travis = new File(tmpDir.getAbsolutePath()+"/repotopush/.travis.yml");

        assertTrue(bak.exists());
        assertTrue(travis.exists());

        boolean detected = false;
        List<String> lines = Files.readAllLines(travis.toPath());
        for (String l : lines) {
            if (l.contains("notification")) {
                assertTrue(l.trim().startsWith("#"));
                detected = true;
            }
            if (l.contains("script")) {
                assertFalse(l.trim().startsWith("#"));
            }
        }

        assertTrue(detected);
    }

    private Build checkBuildAndReturn(long buildId, boolean isPR) {
        Optional<Build> optionalBuild = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildId);
        assertTrue(optionalBuild.isPresent());

        Build build = optionalBuild.get();
        assertThat(build, IsNull.notNullValue());
        assertThat(buildId, Is.is(build.getId()));
        assertThat(build.isPullRequest(), Is.is(isPR));

        return build;
    }
}
