package engineer.carrot.warren.kuroda;

import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class IrcSendStep extends AbstractStepImpl {
    private final @Nonnull String message;

    @Nonnull
    public String getMessage() {
        return message;
    }

    @DataBoundConstructor
    public IrcSendStep(@Nonnull String message) {
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

        final transient IrcSendStep step;

        @StepContextParameter
        transient TaskListener listener;

        @Inject
        public IrcSendStepExecution(IrcSendStep step) {
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            listener.getLogger().println("hello, kuroda!");
            listener.getLogger().println("pipeline message: " + step.message);

            return null;
        }
    }
}