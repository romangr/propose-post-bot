package net.romangr.propose_post_bot.client.telegram.callbacks

import com.elbekd.bot.Bot
import com.elbekd.bot.model.ChatId
import com.elbekd.bot.types.CallbackQuery
import com.elbekd.bot.util.SendingString
import net.romangr.propose_post_bot.UnknownUser
import net.romangr.propose_post_bot.client.telegram.config.TelegramProperties
import net.romangr.propose_post_bot.client.telegram.views.MessageProperties
import org.slf4j.LoggerFactory

class PostToMainChannelCallbackData(
    private val telegramProperties: TelegramProperties,
    private val messageProperties: MessageProperties
) : CallbackData {

    override suspend fun execute(bot: Bot, user: UnknownUser, query: CallbackQuery) {
        val (queryId, from, message) = query
        if (message == null) {
            logger.warn("No message for query {}", queryId);
            return
        }
        if (telegramProperties.enableAdminsCheck && !telegramProperties.getParsedAdminIds().contains(from.id)) {
            logger.warn("Non admin user ({}) attempted to post to main channel from propose channel", from.id)
            return
        }
        val postText = postTextWithRemovedWarning(message.caption ?: message.text)
        bot.sendPhoto(
            ChatId.StringId(telegramProperties.mainChannelId),
            SendingString(message.photo.first().fileId),
            caption = postText
        )
        bot.answerCallbackQuery(queryId, text = messageProperties.messagePublishedToMainChannel)
        if (telegramProperties.deleteAfterPost) {
            bot.deleteMessage(ChatId.IntegerId(message.chat.id), message.messageId)
        }
    }

    private fun postTextWithRemovedWarning(postText: String?): String? {
        if (postText.isNullOrBlank()) {
            return null
        }
        return postText.substringBeforeLast(telegramProperties.warningDelimiter)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostToMainChannelCallbackData::class.java)
    }

}