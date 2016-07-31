package engineer.carrot.warren.kuroda.listener;

import engineer.carrot.warren.kuroda.KurodaPlugin;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.tasks.SimpleBuildWrapper;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;

@SuppressWarnings("unused")
public class BuildWrapperListener extends SimpleBuildWrapper {

    @DataBoundConstructor
    public BuildWrapperListener() {
        super();
    }

    @Override
    public Descriptor getDescriptor() {
        return (Descriptor) super.getDescriptor();
    }

    protected boolean runPreCheckout() {
        return true;
    }

    private static class Disposer extends SimpleBuildWrapper.Disposer {

        @Override
        public void tearDown(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
            Result result = build.getResult();
            EnvVars vars = build.getEnvironment(listener);

            KurodaPlugin.INSTANCE.onBuildTearDown(vars, result, listener);
        }

    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
        Disposer disposer = new Disposer();
        context.setDisposer(disposer);

        EnvVars vars = build.getEnvironment(listener);

        KurodaPlugin.INSTANCE.onBuildSetUp(vars, listener);
    }

    @Extension
    public static final class Descriptor extends BuildWrapperDescriptor {

        public Descriptor() {
            super(BuildWrapperListener.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Kuroda IRC notifier";
        }

        @Override
        public BuildWrapper newInstance(@CheckForNull StaplerRequest req, @Nonnull JSONObject formData) throws FormException {
            return new BuildWrapperListener();
        }
    }

}
