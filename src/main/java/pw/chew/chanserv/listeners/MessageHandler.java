package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.PropertiesManager;
import pw.chew.chewbotcca.util.RestClient;

import java.util.List;

public class MessageHandler extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        // Discord Invite
        List<String> invites = event.getMessage().getInvites();
        if (!invites.isEmpty()) {
            if (event.getMember() != null && MemberHelper.getRank(event.getMember()).getPriority() >= 3)
                return;

            for (String invite : invites) {
                Invite.resolve(event.getJDA(), invite).queue(invite1 -> {
                    if (invite1.getGuild() == null) return;

                    if (!invite1.getGuild().getId().equals(event.getGuild().getId())) {
                        MessageModificationHandler.uncacheMessage(event.getMessageId());
                        event.getMessage().delete().queue();
                        event.getChannel().sendMessage(event.getAuthor().getName() + ", discord link postings are disabled!").queue();
                    }
                }, throwable -> {
                });
            }
        }

        // New Rory Image
        if (event.getChannel().getId().equals("752063016425619487")) {
            Message message = event.getMessage();
            String url;
            if (message.getAttachments().isEmpty()) {
                url = message.getContentRaw();
            } else {
                url = message.getAttachments().get(0).getUrl();
            }

            // post to rory.cat and get a response
            JSONObject response = new JSONObject(
                RestClient.post("https://rory.cat/new",
                    PropertiesManager.getRoryKey(),
                    new JSONObject().put("rory", url)
                )
            );

            // Add checkmark to indicate successful upload
            if (response.optBoolean("success", false)) {
                event.getMessage().addReaction(Emoji.fromUnicode("☑️")).queue();
            }
        }

        // TODO: Swear code here.
    }
}
