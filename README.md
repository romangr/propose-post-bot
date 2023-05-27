# Propose post bot

![build status](https://github.com/romangr/propose-post-bot/actions/workflows/graalvm.yml/badge.svg)

## Starting the bot

The bot can be started via `docker-compose.yml` from the repo. To externalize banned users database and messages
configuration, empty database file and `application.yml` file should be located in the folder on the host machine.

1. Create a `data` folder
2. Copy `empty-local.db` file to the data folder and rename the file as `local.db`
3. Copy `src/main/resources/application.yml` file to the data folder
4. Update configuration in the `application.yml` file, including message templates
5. Start the compose

## Configuration reference

| Telegram configuration property   | Meaning                                                                                                                                                               |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| bot-username                      | Bot username, e.g. "my_bot"                                                                                                                                           |
| bot-token                         | Bot token from BotFather                                                                                                                                              |
| proposals-channel-id              | Id of the proposals channel. If the channel is public, use channel tag, e.g. "@my_proposals_channel". If the channel is private, use channel id, e.g. "-10004034004". |
| main-channel-id                   | Id of the main channel where approved posts will be posted to                                                                                                         |
| admin-ids-string                  | Telegram ids of admins who can approve and publish the posts                                                                                                          |
| enable-admins-check               | Is admin ids for publishing to the main channel enabled. If set to `false` anyone with access to the proposals channel can publish to the main channel                |
| delete-after-post                 | If set to `true` the posts in proposals channel will be removed when they are published to the main channel                                                           |
| number-of-requests-before-warning | Number of requests from a user during the last 10 minutes to trigger warning in the end of the message in the proposals channel                                       |
| warning-delimiter                 | Delimiter between the post and the number of requests warning in the proposals channel                                                                                |

## Using private proposals channel

Bot API requires a channel id to send messages to the private channels. If you know the id of your channel, you can specify it in the `telegram.proposals-channel-id` property.
If you don't know the id of your channel, follow this steps:

1. Make the proposals channel public
2. Fill the `telegram.proposals-channel-id` property with channel's tag
3. Set `BOT_LOG_LEVEL` env var or `logging.level.net.romangr.propose_post_bot` property as `DEBUG`
4. Run the bot
5. Propose a post via bot
6. Check the logs of the bot container, you should see a line `Proposals channel id: <your proposals channel id>`
7. Copy the channel id to the `telegram.proposals-channel-id` property and restart the bot
8. Make your proposals channel private