package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class ShutdownCommand extends SlashCommand {
    public ShutdownCommand() {
        this.name = "shutdown";
        this.ownerCommand = true;
        this.guildOnly = true;
        this.guildId = "134445052805120001";
    }

    @Override
    protected void execute(SlashCommandEvent commandEvent) {
        // whee
        List<Command> commands = commandEvent.getGuild().retrieveCommands().complete();
        commandEvent.reply("Shutting down....").setEphemeral(true).queue((msg) -> {
            for (Command command : commands) {
                commandEvent.getGuild().deleteCommandById(command.getId()).queue();
            }

            commandEvent.getJDA().shutdown();
        });
    }
}