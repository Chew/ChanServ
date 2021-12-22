package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import pw.chew.chanserv.objects.FanclubMessage;

/**
 * This class listens, stores, and checks for deleted or edited messages
 */
public class MessageModificationHandler extends ListenerAdapter {
    private final String MESSAGE_EDIT_CHANNEL = "425062504293597215";

    private static final DB db = DBMaker.fileDB("messages.db").fileMmapEnable().closeOnJvmShutdown().make();
    private static final HTreeMap<String, FanclubMessage> messagesMap = db
        .hashMap("messages", Serializer.STRING, new FanclubMessage.EntrySerializer())
        .createOrOpen();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Ignore bots
        if (event.getAuthor().isBot()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        // Ignore webhooks
        if (event.getMessage().isWebhookMessage()) return;

        // Store the message
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        // Ignore bots
        if (event.getAuthor().isBot()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        // Ignore webhooks
        if (event.getMessage().isWebhookMessage()) return;

        // grab message from map
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // Put new message into map
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));

        if (message == null) {
            return;
        }

        String oldMessage = message.getContentRaw();
        String newMessage = event.getMessage().getContentRaw();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Someone just changed their message!")
            .addField("User", event.getAuthor().getAsMention() + "\n" + event.getAuthor().getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Message", String.format("[Jump](%s)", event.getMessage().getJumpUrl()), true)
            .addField("Old Content", oldMessage, false)
            .addField("New Content", newMessage, false)
            .setFooter("Message ID: " + event.getMessage().getId());

        event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        // grab message from map
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // If it's not stored, do nothing
        if (message == null) return;

        String oldMessage = message.getContentRaw();
        User oldAuthor = message.getAuthor();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("A message was removed!")
            .addField("User", oldAuthor.getAsMention() + "\n" + oldAuthor.getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Content", oldMessage, false);

        event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Remove a message from the cache
     * @param id The message
     */
    public static void uncacheMessage(String id) {
        messagesMap.remove(id);
    }

    /**
     * Add a message to the cache
     * @param msg The message
     */
    public static void cacheMessage(Message msg) {
        messagesMap.put(msg.getId(), new FanclubMessage(msg));
    }

    public static HTreeMap<String, FanclubMessage> getCache() {
        return messagesMap;
    }
}
