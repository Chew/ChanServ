package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import pw.chew.chanserv.util.Roles;
import pw.chew.chewbotcca.util.RestClient;

import java.util.ArrayList;
import java.util.List;

public class ShutdownCommand extends SlashCommand {
    public ShutdownCommand() {
        this.name = "shutdown";
        // this.ownerCommand = true;
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Shut down the bot (owner only)";
        this.enabledRoles = Roles.Rank.getRankIdsGreaterThanOrEqualTo(Roles.Rank.OWNER);
        this.defaultEnabled = false;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.BOOLEAN, "remove_slash_commands", "Whether or not to remove slash commands.").setRequired(false));

        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent commandEvent) {
        // whee
        OptionMapping data = commandEvent.getOption("remove_slash_commands");
        boolean shouldRemove = data != null && data.getAsBoolean();
        commandEvent.reply("Shutting down....").setEphemeral(true).queue((msg) -> {
            if (shouldRemove) {
                RequestBody body = RequestBody.create(new JSONArray().toString(), RestClient.JSON);

                String apiRegister = "https://discord.com/api/v8/applications/" + commandEvent.getJDA().getSelfUser().getId() + "/guilds/" + commandEvent.getGuild().getId() + "/commands";

                Request request = new Request.Builder()
                    .url(apiRegister)
                    .put(body)
                    .addHeader("Authorization", commandEvent.getJDA().getToken())
                    .addHeader("User-Agent", "ChanServ-2619/1.0 (JDA; +https://discord.chew.pro) DBots/271750088383135745") // ChanServ, Update user agent
                    .build();

                RestClient.performRequest(request);
            }

            commandEvent.getJDA().shutdown();
        });
    }
}