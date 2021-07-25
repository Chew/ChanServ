package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.PropertiesManager;
import pw.chew.chewbotcca.util.RestClient;

import java.util.List;

public class MessageHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Discord Invite
        List<String> invites = event.getMessage().getInvites();
        if (!invites.isEmpty()) {
            if (event.getMember() != null && MemberHelper.getRank(event.getMember()).getPriority() >= 3)
                return;

            for (String invite : invites) {
                Invite.resolve(event.getJDA(), invite).queue(invite1 -> {
                    if (invite1.getGuild() == null) return;

                    if (!invite1.getGuild().getId().equals(event.getGuild().getId())) {
                        event.getMessage().delete().queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsTag() + ", discord link postings are disabled!").queue();
                    }
                }, throwable -> {
                });
            }
        }

        // #uwu
        if (event.getChannel().getId().equals("751903362794127470") && !event.getMessage().getContentRaw().replaceAll("[\\.|?|!]", "").equalsIgnoreCase("uwu")) {
            event.getMessage().delete().queue();
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

            RestClient.post("https://chew.pw/rory/new", PropertiesManager.getRoryKey(), new JSONObject().put("rory", url));
        }

        // TODO: Swear code here.
    }
}
