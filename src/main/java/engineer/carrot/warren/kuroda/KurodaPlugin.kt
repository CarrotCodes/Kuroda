package engineer.carrot.warren.kuroda

import hudson.EnvVars
import hudson.model.Result
import hudson.model.TaskListener

object KurodaPlugin {

    lateinit var wrapper: IIrcWrapper

    fun init() {
        wrapper = IrcWrapper(server = "your.server", port = 6697, useTLS = true, nick = "your-nickname", channels = mapOf("#your-channel" to "your-pass"))
        wrapper.connect()
    }

    fun onBuildTearDown(vars: EnvVars, result: Result?, listener: TaskListener) = when (result) {
        null, Result.SUCCESS -> wrapper.sendBuildSucceededMessage(vars, listener)
        Result.FAILURE -> wrapper.sendBuildFailedMessage(vars, listener)
        Result.ABORTED -> wrapper.sendBuildAbortedMessage(vars, listener)
        Result.UNSTABLE -> wrapper.sendBuildUnstableMessage(vars, listener)
        else -> Unit
    }

    fun onBuildSetUp(vars: EnvVars, listener: TaskListener) {
        wrapper.sendBuildStartedMessage(vars, listener)
    }

}