package net.romangr.propose_post_bot.client.telegram.callbacks

import com.elbekd.bot.Bot
import com.elbekd.bot.types.CallbackQuery
import net.romangr.propose_post_bot.UnknownUser
import net.romangr.propose_post_bot.client.telegram.config.TelegramProperties
import net.romangr.propose_post_bot.client.telegram.views.MessageProperties
import net.romangr.propose_post_bot.regulation.BannedUsersRepository
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CallbackDataFactory(
    private val telegramProperties: TelegramProperties,
    private val bannedUsersRepository: BannedUsersRepository,
    private val messageProperties: MessageProperties
) {

    private val callbackDataCreators = mapOf<CallbackType, (List<String>) -> CallbackData>(
        Pair(CallbackType.POST_TO_MAIN_CHANNEL) { elements: List<String> ->
            PostToMainChannelCallbackData(telegramProperties, messageProperties)
        },
        Pair(CallbackType.BAN_USER) { elements: List<String> ->
            BanUserCallbackData(
                elements[1].toLongOrNull() ?: throw RuntimeException("Can't convert user id to Long: ${elements[1]}"),
                telegramProperties,
                bannedUsersRepository,
                messageProperties
            )
        },
    )

    fun parse(callbackString: String): CallbackData {
        if (callbackString.isBlank()) {
            throw CallbackDataParsingException("Callback string is blank")
        }
        val splitCallback: List<String> = callbackString.split(delimiter)
        val callbackType = CallbackType.parse(splitCallback[0])
            ?: throw CallbackDataParsingException("Unknown callback type '${splitCallback[0]}'")
        return callbackDataCreators[callbackType]?.invoke(splitCallback)
            ?: throw CallbackDataParsingException("No creator for callback type '$callbackType'")
    }

    companion object {
        const val delimiter = ":"

        private fun randomString(): String = Random.Default.nextInt(0, 1000000).toString()

        fun postToMainChannelCallbackData(): String =
            arrayListOf(CallbackType.POST_TO_MAIN_CHANNEL.prefix).joinToString(delimiter)

        fun banUserCallbackData(telegramUserId: Long): String =
            arrayListOf(CallbackType.BAN_USER.prefix, telegramUserId).joinToString(delimiter)
    }

}

interface CallbackData {

    suspend fun execute(bot: Bot, user: UnknownUser, query: CallbackQuery)

}

enum class CallbackType(val prefix: String) {
    POST_TO_MAIN_CHANNEL("POST"),
    BAN_USER("BAN");

    companion object Parser {
        private val typeByPrefix: Map<String, CallbackType> = values().associateBy { it.prefix }

        fun parse(prefix: String): CallbackType? = typeByPrefix[prefix]
    }
}

class CallbackDataParsingException(message: String) : RuntimeException(message)
