package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UwUChannelHandler extends ListenerAdapter {
    private final String UWU_REGEX = "[\\.|?|!]";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // We only care about #uwu
        if (!event.getChannel().getName().equals("uwu")) return;

        if ( !event.getMessage().getContentRaw().replaceAll(UWU_REGEX, "").equalsIgnoreCase("uwu")) {
            event.getMessage().delete().queue();
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // We only care about #uwu
        if (!event.getChannel().getName().equals("uwu")) return;

        if (!event.getMessage().getContentRaw().replaceAll(UWU_REGEX, "").equalsIgnoreCase("uwu")) {
            event.getMessage().delete().queue();
        }
    }
}
