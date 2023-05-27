package net.romangr.propose_post_bot.client.telegram.commands

import com.elbekd.bot.types.Message

interface TelegramCommand {

    fun command(): String

    suspend fun action(input: Pair<Message, String?>)
}
