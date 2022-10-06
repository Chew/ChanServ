package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.objects.FanclubMessage;

import java.util.ArrayList;
import java.util.Collections;

public class QuoteCommand extends SlashCommand {

    public QuoteCommand() {
        this.name = "quote";
        this.help = "quote";
        this.guildOnly = false;
        this.options = Collections.singletonList(
            new OptionData(OptionType.USER, "user", "The user to quote, or blank for yourself")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        var cache = MessageModificationHandler.getCache();

        String userId = event.optString("user", event.getUser().getId());

        // Only get messages from the userId
        var messages = new ArrayList<FanclubMessage>();

        for (FanclubMessage message : cache.values()) {
            if (message.authorId().equals(userId)) {
                messages.add(message);
            }
        }

        if (messages.isEmpty()) {
            event.reply("No messages found.").setEphemeral(true).queue();
            return;
        }

        // Pick a random element from the messages arraylist
        var random = (int) (Math.random() * messages.size());
        FanclubMessage message = messages.get(random);
        // Attempt to retrieve the message from Discord
        Message origin;
        Checks.notNull(event.getGuild(), "server"); // all commands are server-bound
        TextChannel originatingChannel = event.getGuild().getTextChannelById(message.channelId());
        boolean thisChannel;
        if (originatingChannel != null) {
            thisChannel = originatingChannel.getId().equals(event.getChannel().getId());
            origin = originatingChannel.retrieveMessageById(message.id()).complete();
        } else {
            event.reply("Could not retrieve original message.").setEphemeral(true).queue();
            return;
        }
        // Make sure author is cached
        if (event.getJDA().getUserById(message.authorId()) == null) {
            event.getJDA().retrieveUserById(message.authorId()).complete();
        }

        MessageEmbed embed = pw.chew.chewbotcca.commands.util.QuoteCommand.gatherData(origin, true, thisChannel, true);

        event.replyEmbeds(embed).queue();
    }
}
