package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.LoggerFactory;
import pw.chew.chanserv.util.Roles;
import pw.chew.jdachewtils.command.OptionHelper;

import java.io.IOException;
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
    protected void execute(SlashCommandEvent event) {
        event.reply("Pre-starting gradle daemon...").setEphemeral(true).queue(interactionHook -> {
            // Run `./gradlew help` in the command line and wait for exit code 0
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(null);
            processBuilder.command("/bin/bash", "-c", "./gradlew help");

            try {
                Process process = processBuilder.start();
                int exitVal = process.waitFor();
                if (exitVal > 0) {
                    LoggerFactory.getLogger(ShutdownCommand.class).warn("Error starting gradle daemon! " + exitVal);
                }
                LoggerFactory.getLogger(ShutdownCommand.class).debug("Gradle daemon started with exit code " + exitVal);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            // whee
            boolean shouldRemove = OptionHelper.optBoolean(event, "remove_slash_commands", false);
            interactionHook.editOriginal("Shutting down....").queue((msg) -> {
                if (shouldRemove) {
                    Checks.notNull(event.getGuild(), "Server");
                    event.getGuild().updateCommands().queue();
                }

                event.getJDA().shutdown();
            });
        });
    }
}
