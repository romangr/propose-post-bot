package net.romangr.propose_post_bot.client.telegram.views

import com.elbekd.bot.types.InlineKeyboardButton
import com.elbekd.bot.types.InlineKeyboardMarkup
import net.romangr.propose_post_bot.client.telegram.callbacks.CallbackDataFactory
import org.springframework.stereotype.Component

@Component
class ProposeChannelView(private val messageProperties: MessageProperties) {

    fun buildKeyboard(telegramUserId: Long) = InlineKeyboardMarkup(
        listOf(
            listOf(
                InlineKeyboardButton(
                    text = messageProperties.publishButton,
                    callbackData = CallbackDataFactory.postToMainChannelCallbackData()
                ),
                InlineKeyboardButton(
                    text = messageProperties.banButton,
                    callbackData = CallbackDataFactory.banUserCallbackData(telegramUserId)
                )
            )
        )
    )

    fun wrapWarningDelimiter(warningDelimiter: String) = "\n\n$warningDelimiter\n"

}

fun String.escapeMarkdownCharacters(): String = this
    .replace("*", """\*""")
    .replace("`", """\`""")
    .replace("[", """\[""")