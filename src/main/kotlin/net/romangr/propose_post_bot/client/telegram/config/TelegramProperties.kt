package net.romangr.propose_post_bot.client.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "telegram")
class TelegramProperties {

    lateinit var botUsername: String
    lateinit var token: String
    lateinit var proposalsChannelId: String
    lateinit var mainChannelId: String
    lateinit var adminIdsString: String
    lateinit var warningDelimiter: String
    var deleteAfterPost: Boolean = true
    var enableAdminsCheck: Boolean = true
    var numberOfRequestsBeforeWarning: Int = 0

    fun getParsedAdminIds(): List<Long> = adminIdsString.takeIf { adminIdsString.isNotBlank() }?.split(",")
        ?.map { it.toLongOrNull() ?: throw Exception("Can't parse admin id to Long: $it") } ?: emptyList()
}
