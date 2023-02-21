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
import pw.chew.chanserv.util.PluralKitLookup;

/**
 * This class listens, stores, and checks for deleted or edited messages
 */
public class MessageModificationHandler extends ListenerAdapter {
    private final String MESSAGE_EDIT_CHANNEL = "425062504293597215";
    private final String ADMIN_EDIT_CHANNEL = "932055862556635207";

    private static final DB db = DBMaker.fileDB("messages.db").fileMmapEnable().closeOnJvmShutdown().checksumHeaderBypass().make();
    private static final HTreeMap<String, FanclubMessage> messagesMap = db
        .hashMap("messages", Serializer.STRING, new FanclubMessage.EntrySerializer())
        .createOrOpen();

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

        // Store the message
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        // Ignore non-server
        if (!event.isFromGuild()) return;
        // Ignore bots
        if (event.getAuthor().isBot() && !event.getMessage().isWebhookMessage()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        // Ignore webhooks, unless is PK
        if (event.getMessage().isWebhookMessage() && !PluralKitLookup.isMessageProxied(event.getMessageId())) return;

        // grab message from map
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // Put new message into map
        messagesMap.put(event.getMessageId(), new FanclubMessage(event.getMessage()));

        if (message == null) {
            return;
        }

        String oldMessage = message.content();
        String newMessage = event.getMessage().getContentRaw();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Someone just changed their message!")
            .addField("User", event.getAuthor().getAsMention() + "\n" + event.getAuthor().getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Message", String.format("[Jump](%s)", event.getMessage().getJumpUrl()), true)
            .addField("Old Content", oldMessage, false)
            .addField("New Content", newMessage, false)
            .setFooter("Message ID: " + event.getMessage().getId());

        if (event.getGuild().getPublicRole().hasPermission(event.getGuildChannel(), Permission.VIEW_CHANNEL)) {
            event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getGuild().getTextChannelById(ADMIN_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        // ignore non-server
        if (!event.isFromGuild()) return;
        // grab message from map
        FanclubMessage message = messagesMap.get(event.getMessageId());

        // If it's not stored, do nothing
        if (message == null) return;

        if (PluralKitLookup.isMessageProxied(message.id())) return;

        String oldMessage = message.content();
        User oldAuthor = message.getAuthor();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("A message was removed!")
            .addField("User", oldAuthor.getAsMention() + "\n" + oldAuthor.getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Content", oldMessage, false);

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
