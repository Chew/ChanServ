package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEditHandler extends ListenerAdapter {
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getChannel().getId().equals("751903362794127470") && !event.getMessage().getContentRaw().replaceAll("[\\.|?|!]", "").equalsIgnoreCase("uwu")) {
            event.getMessage().delete().queue();
        }
    }
}
