package engineer.carrot.warren.kuroda

import engineer.carrot.warren.kale.irc.message.rfc1459.PrivMsgMessage
import engineer.carrot.warren.warren.*
import engineer.carrot.warren.warren.event.ConnectionLifecycleEvent
import engineer.carrot.warren.warren.event.WarrenEventDispatcher
import engineer.carrot.warren.warren.event.internal.SendSomethingEvent
import engineer.carrot.warren.warren.state.LifecycleState
import hudson.EnvVars
import hudson.model.TaskListener
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

interface IIrcWrapper {

    fun connect()
    fun send(message: String, listener: TaskListener)
    fun sendBuildStartedMessage(vars: EnvVars, listener: TaskListener)
    fun sendBuildSucceededMessage(vars: EnvVars, listener: TaskListener)
    fun sendBuildFailedMessage(vars: EnvVars, listener: TaskListener)
    fun sendBuildAbortedMessage(vars: EnvVars, listener: TaskListener)
    fun sendBuildUnstableMessage(vars: EnvVars, listener: TaskListener)

}

class IrcWrapper(private val server: String, private val port: Int, private val useTLS: Boolean, private val nick: String, private val channels: Map<String, String?>) : IIrcWrapper {

    private @Volatile var ircRunner: IrcRunner? = null
    private val queuedMessages = ConcurrentLinkedQueue<String>()

    override fun connect() {
        println("getting or creating new irc runner")
        getOrCreateCurrentRunner()
    }

    override fun send(message: String, listener: TaskListener) {
        sendIrcMessage(message, listener)
    }

    override fun sendBuildStartedMessage(vars: EnvVars, listener: TaskListener) {
        val message = listOf("Build started:", vars["JOB_NAME"], vars["BUILD_DISPLAY_NAME"], "🕑", vars["BUILD_URL"]).filterNotNull().joinToString(separator = " ")

        sendIrcMessage(message, listener)
    }

    override fun sendBuildSucceededMessage(vars: EnvVars, listener: TaskListener) {
        val message = listOf("Build succeeded:", vars["JOB_NAME"], vars["BUILD_DISPLAY_NAME"], "⭐️", vars["BUILD_URL"]).filterNotNull().joinToString(separator = " ")

        sendIrcMessage(message, listener)
    }

    override fun sendBuildFailedMessage(vars: EnvVars, listener: TaskListener) {
        val message = listOf("Build failed:", vars["JOB_NAME"], vars["BUILD_DISPLAY_NAME"], "⛈", vars["BUILD_URL"]).filterNotNull().joinToString(separator = " ")

        sendIrcMessage(message, listener)
    }

    override fun sendBuildAbortedMessage(vars: EnvVars, listener: TaskListener) {
        val message = listOf("Build aborted:", vars["JOB_NAME"], vars["BUILD_DISPLAY_NAME"], "⛔️", vars["BUILD_URL"]).filterNotNull().joinToString(separator = " ")

        sendIrcMessage(message, listener)
    }

    override fun sendBuildUnstableMessage(vars: EnvVars, listener: TaskListener) {
        val message = listOf("Build unstable:", vars["JOB_NAME"], vars["BUILD_DISPLAY_NAME"], "🌧", vars["BUILD_URL"]).filterNotNull().joinToString(separator = " ")

        sendIrcMessage(message, listener)
    }

    private fun sendIrcMessage(message: String, listener: TaskListener) {
        val irc: IrcRunner = getOrCreateCurrentRunner()

        if (irc.lastStateSnapshot?.connection?.lifecycle != LifecycleState.CONNECTED) {
            listener.logger.println("waiting to connect - added to queued messages")
            queuedMessages += message
        } else {
            val channels = (irc.lastStateSnapshot?.channels?.joining?.all?.keys ?: setOf()) + (irc.lastStateSnapshot?.channels?.joined?.all?.keys ?: setOf())
            channels.forEach {
                listener.logger.println("sending to $it: $message")

                irc.eventSink.add(SendSomethingEvent(PrivMsgMessage(target = it, message = message), irc.sink))
            }
        }
    }

    private fun getOrCreateCurrentRunner(): IrcRunner {
        var currentRunner = ircRunner

        if (currentRunner?.lastStateSnapshot?.connection?.lifecycle == LifecycleState.DISCONNECTED) {
            currentRunner?.sink?.tearDown()
            currentRunner = null
        }

        return if (currentRunner != null) { currentRunner } else {
            val events = WarrenEventDispatcher()
            events.onAnything { println("irc event: $it") }

            val factory = WarrenFactory(ServerConfiguration(server, port, useTLS = useTLS),
                    UserConfiguration(nick),
                    ChannelsConfiguration(channels),
                    EventConfiguration(events, fireIncomingLineEvent = false))

            val connection = factory.create()

            events.on(ConnectionLifecycleEvent::class) {
                val lifecycle = it.lifecycle

                if (lifecycle == LifecycleState.CONNECTED) {
                    for (queuedMessage in queuedMessages) {
                        val channels = (connection.lastStateSnapshot?.channels?.joining?.all?.keys ?: setOf()) + (connection.lastStateSnapshot?.channels?.joined?.all?.keys ?: setOf())
                        channels.forEach {
                            println("sending queued messages to $it: $queuedMessages")

                            connection.eventSink.add(SendSomethingEvent(PrivMsgMessage(target = it, message = queuedMessage), connection.sink))
                        }
                    }
                } else if (lifecycle == LifecycleState.DISCONNECTED) {
                    queuedMessages.clear()
                }
            }

            ircRunner = connection

            thread {
                println("irc connection started")
                connection.run()
                ircRunner = null
                println("irc connection ended")
            }

            connection
        }
    }

}