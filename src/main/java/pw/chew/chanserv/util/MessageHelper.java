package pw.chew.chanserv.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.atomic.AtomicReference;

public class MessageHelper {
    public static void sendTemporaryMessage(TextChannel channel, String message, int duration) {
        AtomicReference<Message> sent = new AtomicReference<>();
        channel.sendMessage(message).queue(sent::set);
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    sent.get().delete().queue();
                }
            },
            duration
        );
    }
}
