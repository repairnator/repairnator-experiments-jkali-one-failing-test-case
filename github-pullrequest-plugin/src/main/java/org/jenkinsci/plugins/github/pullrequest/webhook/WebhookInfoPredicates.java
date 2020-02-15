package org.jenkinsci.plugins.github.pullrequest.webhook;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import hudson.model.Job;
import org.jenkinsci.plugins.github.pullrequest.GitHubPRTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.jenkinsci.plugins.github.pullrequest.GitHubPRTriggerMode.HEAVY_HOOKS;
import static org.jenkinsci.plugins.github.pullrequest.GitHubPRTriggerMode.HEAVY_HOOKS_CRON;
import static org.jenkinsci.plugins.github.pullrequest.GitHubPRTriggerMode.LIGHT_HOOKS;
import static org.jenkinsci.plugins.github.pullrequest.utils.JobHelper.ghPRTriggerFromJob;
import static org.jenkinsci.plugins.github.pullrequest.utils.PRHelperFunctions.asFullRepoName;
import static org.jenkinsci.plugins.github.util.JobInfoHelpers.withTrigger;

/**
 * @author lanwen (Merkushev Kirill)
 */
public final class WebhookInfoPredicates {
    private WebhookInfoPredicates() {
    }

    public static Predicate<Job> withPRTrigger() {
        return Predicates.and(
                withTrigger(GitHubPRTrigger.class),
                withHookTriggerMode()
        );
    }

    public static Predicate<Job> withHookTriggerMode() {
        return new HookTriggerMode();
    }

    public static Predicate<Job> withRepo(final String repo) {
        return new WithRepo(repo);
    }

    private static class HookTriggerMode implements Predicate<Job> {
        @Override
        public boolean apply(Job job) {
            return Predicates.in(asList(HEAVY_HOOKS, HEAVY_HOOKS_CRON, LIGHT_HOOKS))
                    .apply(checkNotNull(
                            ghPRTriggerFromJob(job),
                            "This predicate can be applied only for job with GitHubPRTrigger"
                    ).getTriggerMode());
        }
    }

    private static class WithRepo implements Predicate<Job> {
        public static final Logger LOG = LoggerFactory.getLogger(WithRepo.class);

        private final String repo;

        WithRepo(String repo) {
            this.repo = repo;
        }

        @Override
        public boolean apply(Job job) {
            try {
                return equalsIgnoreCase(
                        repo,
                        asFullRepoName(
                                ghPRTriggerFromJob(job).getRepoFullName(job)
                        )
                );
            } catch (Exception ex) {
                LOG.warn("Can't get GitHub repository name for {}. Have you set correct 'GitHub Project Property'?",
                        job.getFullName(), ex);
                return false;
            }
        }
    }
}
