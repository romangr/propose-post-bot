package net.romangr.propose_post_bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication


@ConfigurationPropertiesScan
@SpringBootApplication
class ProposePostBotApplication {
}

fun main(args: Array<String>) {
    runApplication<ProposePostBotApplication>(*args)
}

data class ActionResult<V, S>(val value: V, val status: S)

data class UnknownUser(val name: String, val telegramId: Long, val telegramUsername: String? = null)