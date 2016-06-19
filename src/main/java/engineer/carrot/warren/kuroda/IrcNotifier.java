package engineer.carrot.warren.kuroda;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

class IrcNotifier extends Notifier {

    @Override
    public Descriptor getDescriptor() {
        return (Descriptor) super.getDescriptor();
    }

    @DataBoundConstructor
    public IrcNotifier() {
        super();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        Map<hudson.model.Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof IrcNotifier) {
                listener.getLogger().println("hello, kuroda!");
                listener.getLogger().println("pipeline message: build started");
            }
        }

        return super.prebuild(build, listener);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Publisher> {

        public Descriptor() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "IRC notifications";
        }

        @Override
        public Publisher newInstance(StaplerRequest request, JSONObject formData) {
            return new IrcNotifier();
        }

        @Override
        public boolean configure(StaplerRequest request, JSONObject formData) throws FormException {
            return super.configure(request, formData);
        }
    }

}