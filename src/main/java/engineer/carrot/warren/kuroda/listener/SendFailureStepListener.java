package engineer.carrot.warren.kuroda.listener;

import engineer.carrot.warren.kuroda.KurodaPlugin;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SendFailureStepListener extends AbstractStepImpl {

    @DataBoundConstructor
    public SendFailureStepListener() {
        super();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(IrcSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "ircSendFailure";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Sends build failure template to IRC";
        }

    }

    private static class IrcSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        final transient SendFailureStepListener step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient EnvVars envVars;

        @Inject
        public IrcSendStepExecution(SendFailureStepListener step) {
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            KurodaPlugin.wrapper.sendBuildFailedMessage(envVars, listener);

            return null;
        }
    }
}