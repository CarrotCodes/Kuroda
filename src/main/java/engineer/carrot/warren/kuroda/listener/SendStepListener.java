package engineer.carrot.warren.kuroda.listener;

import engineer.carrot.warren.kuroda.KurodaPlugin;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SendStepListener extends AbstractStepImpl {
    private final @Nonnull String message;

    @Nonnull
    public String getMessage() {
        return message;
    }

    @DataBoundConstructor
    public SendStepListener(@Nonnull String message) {
        this.message = message;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(IrcSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "ircSend";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Sends an IRC message";
        }

    }

    private static class IrcSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        final transient SendStepListener step;

        @StepContextParameter
        transient TaskListener listener;

        @Inject
        public IrcSendStepExecution(SendStepListener step) {
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            KurodaPlugin.wrapper.send(step.message, listener);

            return null;
        }
    }
}