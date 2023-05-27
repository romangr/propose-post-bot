package net.romangr.propose_post_bot.client.telegram.config

import com.elbekd.bot.Bot
import com.elbekd.bot.types.MessageEntity
import com.elbekd.bot.types.UpdateMessage
import kotlinx.coroutines.runBlocking
import net.romangr.propose_post_bot.client.telegram.ProposePostMessageHandler
import net.romangr.propose_post_bot.client.telegram.callbacks.CallbackDataFactory
import net.romangr.propose_post_bot.client.telegram.commands.TelegramCommand
import net.romangr.propose_post_bot.client.telegram.mappers.UserMapper
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class StartupApplicationListenerBotInit(
    private val telegramBot: Bot,
    commands: List<TelegramCommand>,
    private val callbackDataFactory: CallbackDataFactory,
    private val messageHandler: ProposePostMessageHandler,
) : ApplicationListener<ContextRefreshedEvent> {

    private val commandsMap = commands.map { it.command() to it::action }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        telegramBot
            .also { bot ->
                commandsMap
                    .forEach { pair ->
                        runCatching { bot.onCommand(pair.first, pair.second) }
                            .onFailure { e ->
                                logger.warn("Error processing command {}", pair.first, e);
                            }
                    }
            }
            .also { bot -> runBlocking { bot.getMe() } }
            .also { bot ->
                bot.onCallbackQuery { query ->
                    runCatching {
                        val queryData = query.data ?: return@onCallbackQuery
                        val callback = callbackDataFactory.parse(queryData)
                        callback.execute(
                            bot,
                            UserMapper.fromTelegramUser(query.from),
                            query
                        )
                    }.onFailure { e ->
                        logger.warn("Error processing callback query with id {}", query.id, e);
                    }
                }
            }
            .also { bot ->
                bot.onAnyUpdate {
                    logger.trace("Update received: {}", it)
                    if (it !is UpdateMessage) return@onAnyUpdate
                    val m = it.message
                    val containsCommand = m.entities.any { e -> e.type == MessageEntity.Type.BOT_COMMAND }
                    if (containsCommand) {
                        return@onAnyUpdate
                    }

                    runCatching {
                        messageHandler.handle(it)
                    }.onFailure { e ->
                        logger.warn("Couldn't execute a command from message {}", m.text, e)
                    }
                }
            }
            .also { bot -> bot.start() }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(StartupApplicationListenerBotInit::class.java)
    }
}
