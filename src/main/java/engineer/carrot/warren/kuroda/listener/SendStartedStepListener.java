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

public class SendStartedStepListener extends AbstractStepImpl {

    @DataBoundConstructor
    public SendStartedStepListener() {
        super();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(IrcSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "ircSendStarted";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Sends build started template to IRC";
        }

    }

    private static class IrcSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        final transient SendStartedStepListener step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient EnvVars envVars;

        @Inject
        public IrcSendStepExecution(SendStartedStepListener step) {
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            KurodaPlugin.wrapper.sendBuildStartedMessage(envVars, listener);

            return null;
        }
    }
}