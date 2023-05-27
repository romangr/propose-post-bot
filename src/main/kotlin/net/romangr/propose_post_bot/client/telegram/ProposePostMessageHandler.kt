package net.romangr.propose_post_bot.client.telegram

import com.elbekd.bot.Bot
import com.elbekd.bot.model.ChatId
import com.elbekd.bot.types.UpdateMessage
import com.elbekd.bot.util.SendingString
import net.romangr.propose_post_bot.client.telegram.config.TelegramProperties
import net.romangr.propose_post_bot.client.telegram.views.MessageProperties
import net.romangr.propose_post_bot.client.telegram.views.ProposeChannelView
import net.romangr.propose_post_bot.regulation.BannedUsersRepository
import net.romangr.propose_post_bot.regulation.RequestCounter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProposePostMessageHandler(
    private val bot: Bot,
    private val requestCounter: RequestCounter,
    private val bannedUsersRepository: BannedUsersRepository,
    private val telegramProperties: TelegramProperties,
    private val messageProperties: MessageProperties,
    private val proposeChannelView: ProposeChannelView
) {
    suspend fun handle(update: UpdateMessage) {
        val message = update.message
        val photos = message.photo
        val userChatId = ChatId.IntegerId(message.chat.id)
        val telegramUserId = message.from?.id
        if (telegramUserId == null) {
            logger.warn(
                "Received message without telegram user id in chat {} {}",
                message.chat.id,
                message.chat.title ?: message.chat.username
            )
            return
        }
        if (bannedUsersRepository.existsById(telegramUserId)) {
            return
        }
        if (photos.isEmpty()) {
            bot.sendMessage(userChatId, messageProperties.noPhotosInTheIncomingMessageResponse)
            return
        }
        val channelId = resolveChannelId(telegramProperties.proposalsChannelId)
        var postText = message.caption ?: message.text ?: ""
        val numberOfRequestsByUser = requestCounter.incrementAndGet(telegramUserId)
        if (numberOfRequestsByUser > telegramProperties.numberOfRequestsBeforeWarning) {
            postText += proposeChannelView.wrapWarningDelimiter(telegramProperties.warningDelimiter) +
                    messageProperties.numberOfMessagesFromUserAboveThresholdWarning.format(numberOfRequestsByUser)
        }
        val sendingResult = bot.sendPhoto(
            channelId,
            SendingString(message.photo.last().fileId),
            caption = postText,
            replyMarkup = proposeChannelView.buildKeyboard(telegramUserId)
        )
        logger.debug("Proposals channel id: {}", sendingResult.chat.id)
        bot.sendMessage(
            userChatId,
            messageProperties.postIsAddedToProposedPostsListResponse
        )
    }

    fun resolveChannelId(id: String): ChatId {
        val numberId = id.toLongOrNull()
        if (numberId !== null) {
            return ChatId.IntegerId(numberId)
        }
        return ChatId.StringId(id)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ProposePostMessageHandler::class.java)
    }

}
