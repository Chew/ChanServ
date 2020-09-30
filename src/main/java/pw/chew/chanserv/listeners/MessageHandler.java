package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.chew.chanserv.util.MemberHelper;

public class MessageHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains("discord.gg") || event.getMessage().getContentRaw().contains("discordapp.com/invite")) {
            if (event.getMember() != null && MemberHelper.getRank(event.getMember()).getPriority() >= 3)
                return;

            event.getMessage().delete().queue();
            event.getChannel().sendMessage(event.getAuthor().getAsTag() + ", discord link postings are disabled!").queue();
            return;
        }

        // TODO: Swear code here.
    }
}
