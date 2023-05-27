package net.romangr.propose_post_bot

import com.elbekd.bot.Bot
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean


@Tag("CI")
@SpringBootTest
class ProposePostBotApplicationTests {

    @MockBean
    lateinit var bot: Bot

    @Test
    fun contextLoads() {
    }

}
