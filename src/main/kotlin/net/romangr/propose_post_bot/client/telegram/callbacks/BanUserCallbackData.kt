package net.romangr.propose_post_bot.client.telegram.callbacks

import com.elbekd.bot.Bot
import com.elbekd.bot.model.ChatId
import com.elbekd.bot.types.CallbackQuery
import net.romangr.propose_post_bot.UnknownUser
import net.romangr.propose_post_bot.client.telegram.config.TelegramProperties
import net.romangr.propose_post_bot.client.telegram.views.MessageProperties
import net.romangr.propose_post_bot.regulation.BannedUser
import net.romangr.propose_post_bot.regulation.BannedUsersRepository
import org.slf4j.LoggerFactory

class BanUserCallbackData(
    private val telegramUserId: Long,
    private val telegramProperties: TelegramProperties,
    private val bannedUsersRepository: BannedUsersRepository,
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
        bannedUsersRepository.save(BannedUser(telegramUserId))
        bot.answerCallbackQuery(queryId, text = messageProperties.userAddedToBanList)
        bot.deleteMessage(ChatId.IntegerId(message.chat.id), message.messageId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BanUserCallbackData::class.java)
    }

}