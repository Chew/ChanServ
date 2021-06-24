package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.PropertiesManager;

import java.io.IOException;
import java.util.List;

public class MessageHandler extends ListenerAdapter {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
            JSONObject json = new JSONObject()
                .put("rory", url);
            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                .url("https://chew.pw/rory/new")
                .post(body)
                .addHeader("Authorization", PropertiesManager.getRoryKey())
                .addHeader("User-Agent", "ChanServ-2619/1.0 (JDA; +https://discord.chew.pro) Rory/271750088383135745")
                .build();

            try (Response response = event.getJDA().getHttpClient().newCall(request).execute()) {
                String responseString;
                ResponseBody responseBody = response.body();
                if(responseBody == null) {
                    responseString = null;
                } else {
                    responseString = responseBody.string();
                }
                LoggerFactory.getLogger(MessageHandler.class).debug("Response is " + responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO: Swear code here.
    }
}
