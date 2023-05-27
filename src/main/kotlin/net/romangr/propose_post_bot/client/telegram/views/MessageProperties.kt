package net.romangr.propose_post_bot.client.telegram.views

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "messages")
class MessageProperties() {
    lateinit var postIsAddedToProposedPostsListResponse: String
    lateinit var numberOfMessagesFromUserAboveThresholdWarning: String
    lateinit var noPhotosInTheIncomingMessageResponse: String
    lateinit var userAddedToBanList: String
    lateinit var messagePublishedToMainChannel: String
    lateinit var welcomeMessage: String
    lateinit var publishButton: String
    lateinit var banButton: String

}