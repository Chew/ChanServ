package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.objects.FanclubMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CacheChannelCommand extends SlashCommand {
    public CacheChannelCommand() {
        this.name = "cache";
        this.help = "Caches the last x messages from a channel.";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "The channel to cache messages from")
                .setChannelTypes(ChannelType.TEXT).setRequired(true),
            new OptionData(OptionType.INTEGER, "amount", "The amount of messages to cache")
                .setMinValue(1).setRequired(true),
            new OptionData(OptionType.BOOLEAN, "wipe", "Whether to wipe the cache before caching")
                .setRequired(false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        GuildChannelUnion channel = event.getOption("channel", OptionMapping::getAsChannel);
        if (channel == null) {
            event.reply("You must specify a channel!").setEphemeral(true).queue();
            return;
        }

        boolean wipe = event.optBoolean("wipe", false);

        event.deferReply(true).queue(interactionHook -> {

            // Get message cache
            var cache = MessageModificationHandler.getCache();

            if (wipe) {
                for (FanclubMessage message : cache.values()) {
                    if (message.channelId().equals(channel.getId())) {
                        cache.remove(message.id());
                    }
                }
            }

            channel.asGuildMessageChannel().getIterableHistory()
                .takeAsync(event.getOption("amount", 0, OptionMapping::getAsInt)) // Collect 1000 messages
                .thenApply(list ->
                    list.stream()
                        .map(FanclubMessage::new)
                        .collect(Collectors.toList())
                ).thenAccept(messages -> {
                    // Add messages to cache
                    for (FanclubMessage message : messages) {
                        cache.put(message.id(), message);
                    }

                    interactionHook.editOriginal("Cached " + messages.size() + " messages from " + channel.getName() + "!").queue();
                });
        });
    }
}
