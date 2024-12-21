package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import pw.chew.chanserv.objects.FanclubMessage;

/**
 * Handles message-related events such as creation, updates, and deletions in a Discord guild.
 * 
 * This class listens to message events and manages a persistent cache of messages.
 * It generates and sends audit logs for message edits and deletions and allows 
 * messages to be manually added or removed from the cache.
 */

public class MessageModificationHandler extends ListenerAdapter {
    private final String MESSAGE_EDIT_CHANNEL = "425062504293597215";
    private final String ADMIN_EDIT_CHANNEL = "932055862556635207";

    private static final DB db = DBMaker.fileDB("messages.db").fileMmapEnable().closeOnJvmShutdown().checksumHeaderBypass().make();
    private static final HTreeMap<String, FanclubMessage> messagesMap = db
        .hashMap("messages", Serializer.STRING, new FanclubMessage.EntrySerializer())
        .createOrOpen();

    /**
    * Handles message creation events and caches messages.
    *
    * @param event The event representing a received message.
    */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore non-server
        if (!event.isFromGuild()) return;
        // Ignore bots
        if (event.getAuthor().isBot()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        // Ignore webhooks
        if (event.getMessage().isWebhookMessage()) return;

        // Cache the message
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));
    }

    /**
    * Handles message updates and logs the changes.
    *
    * @param event The event representing a message update.
    */
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        // Ignore non-server
        if (!event.isFromGuild()) return;
        // Ignore bots
        if (event.getAuthor().isBot() && !event.getMessage().isWebhookMessage()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        // Ignore webhooks
        if (event.getMessage().isWebhookMessage()) return;

        // Retrieve the old message from the cache
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // Cache the updated message
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));

        // Skip logging if the message wasn't cached
        if (message == null) return; 

        // Store messages
        String oldMessage = message.content();
        String newMessage = event.getMessage().getContentRaw();

        // Fetch the original creation date
        OffsetdateTime creationTime = TimeUtil.getTimeCreated(message.getId());
        String creationDateTime = creationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Fetch the edit date
        OffsetdateTime editTime = TimeUtil.getTimeCreated(message.getId());
        String editDateTime = creationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Someone just changed their message!")
            .addField("User", event.getAuthor().getAsMention() + "\n" + event.getAuthor().getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Message", String.format("[Jump](%s)", event.getMessage().getJumpUrl()), true)
            .addField("Old Content", oldMessage, false)
            .addField("New Content", newMessage, false)
            .addField("Creation Date", creationDateTime, false)
            .addField("Edit Date", editDateTime, false)
            .setFooter("Message ID: " + event.getMessage().getId());

        if (event.getGuild().getPublicRole().hasPermission(event.getGuildChannel(), Permission.VIEW_CHANNEL)) {
            event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getGuild().getTextChannelById(ADMIN_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        }
    }

    /**
    * Handles message deletion events and logs the details.
    *
    * @param event The event representing a deleted message.
    */
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        // ignore non-server
        if (!event.isFromGuild()) return;
        // grab message from map
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // Skip logging if the message wasn't cached
        if (message == null) return;

        // TODO: Check for PluralKit message.

        // Store Message
        String oldMessage = message.content();
        User oldAuthor = message.getAuthor();

        // Fetch the original creation date
        OffsetDateTime creationTime = TimeUtil.getTimeCreated(message.getId());
        String creationDateTime = creationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("A message was removed!")
            .addField("User", oldAuthor.getAsMention() + "\n" + oldAuthor.getId() + "\n" + oldAuthor.getName(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Message Content", oldMessage, false)
            .addField("Creation Date", creationDateTime, false)
            .setFooter("Message ID: " + event.getMessage().getId());

        if (event.getGuild().getPublicRole().hasPermission(event.getGuildChannel(), Permission.VIEW_CHANNEL)) {
            event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getGuild().getTextChannelById(ADMIN_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        }

        if (event.getChannel().getName().equals("uwu")) {
            // Remove it from the map, we don't want to track failed uwus
            messagesMap.remove(event.getMessageId());
        }
    }

    /**
     * Remove a message from the cache.
     *
     * @param id The ID of the message to remove.
     */
    public static void uncacheMessage(String id) {
        messagesMap.remove(id);
    }

    /**
     * Adds a message to the cache.
     *
     * @param msg The message to cache.
     */
    public static void cacheMessage(Message msg) {
        messagesMap.put(msg.getId(), new FanclubMessage(msg));
    }

    /**
    * Retrieves the cached messages.
    *
    * @return The map containing cached messages.
    */
    public static HTreeMap<String, FanclubMessage> getCache() {
        return messagesMap;
    }
}
