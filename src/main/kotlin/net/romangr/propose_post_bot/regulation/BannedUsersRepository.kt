package net.romangr.propose_post_bot.regulation

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BannedUsersRepository(private val jdbcTemplate: JdbcTemplate) {

    @EventListener(ApplicationReadyEvent::class)
    fun ensureTableExists() {
        logger.info("Creating banned_user table")
        jdbcTemplate.execute(
            """
            create table if not exists banned_user(
                telegram_user_id integer primary key
            );
        """.trimIndent()
        )
        logger.info("banned_user table created")
    }

    fun save(user: BannedUser) {
        jdbcTemplate.execute(
            """
                insert into banned_user values (${user.telegramUserId})
                on conflict(telegram_user_id) do nothing;
        """.trimIndent()
        )
    }

    fun existsById(id: Long): Boolean {
        val count = jdbcTemplate.query(
            """
            select count(*) as count from banned_user where telegram_user_id = $id;
        """.trimIndent()
        ) { r, _ -> r.getLong("count") }.first()
        return count > 0
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BannedUsersRepository::class.java)
    }
}

data class BannedUser(val telegramUserId: Long)