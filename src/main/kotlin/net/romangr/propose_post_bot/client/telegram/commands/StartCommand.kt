package net.romangr.propose_post_bot.client.telegram.commands

import com.elbekd.bot.Bot
import com.elbekd.bot.model.toChatId
import com.elbekd.bot.types.Message
import net.romangr.propose_post_bot.client.telegram.mappers.UserMapper
import net.romangr.propose_post_bot.client.telegram.views.MessageProperties
import org.springframework.stereotype.Component


@Component
class StartCommand(
    private val bot: Bot,
    private val messageProperties: MessageProperties
) : TelegramCommand {

    override fun command(): String = "/start"

    override suspend fun action(input: Pair<Message, String?>) {
        val (message) = input
        val from = message.from
        val senderId = from?.id
        if (senderId === null) {
            return
        }
        val user = UserMapper.fromTelegramUser(from)
        val welcomeMessage = messageProperties.welcomeMessage.format(user.name)
        bot.sendMessage(message.chat.id.toChatId(), welcomeMessage)
    }

}
