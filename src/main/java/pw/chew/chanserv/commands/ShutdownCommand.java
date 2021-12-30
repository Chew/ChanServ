package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.Roles;
import pw.chew.jdachewtils.command.OptionHelper;

import java.util.Collections;

public class ShutdownCommand extends SlashCommand {
    public ShutdownCommand() {
        this.name = "shutdown";
        this.help = "Shut down the bot (owner only)";
        this.enabledRoles = Roles.Rank.getRankIdsGreaterThanOrEqualTo(Roles.Rank.OWNER);
        this.defaultEnabled = false;

        this.options = Collections.singletonList(
            new OptionData(OptionType.BOOLEAN, "remove_slash_commands", "Whether or not to remove slash commands.").setRequired(false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent commandEvent) {
        // whee
        boolean shouldRemove = OptionHelper.optBoolean(commandEvent, "remove_slash_commands", false);
        commandEvent.reply("Shutting down....").setEphemeral(true).queue((msg) -> {
            if (shouldRemove) {
                commandEvent.getGuild().updateCommands().queue();
            }

            commandEvent.getJDA().shutdown();
        });
    }
}
