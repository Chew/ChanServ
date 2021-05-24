package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class ShutdownCommand extends SlashCommand {
    public ShutdownCommand() {
        this.name = "shutdown";
        this.ownerCommand = true;
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Shut down the bot (owner only)";

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.BOOLEAN, "remove_slash_commands", "Whether or not to remove slash commands.").setRequired(false));

        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent commandEvent) {
        // whee
        SlashCommandEvent.OptionData data = commandEvent.getOption("remove_slash_commands");
        boolean shouldRemove = data != null && data.getAsBoolean();
        List<Command> commands = new ArrayList<>();
        if (shouldRemove)
            commands = commandEvent.getGuild().retrieveCommands().complete();
        List<Command> finalCommands = commands;
        commandEvent.reply("Shutting down....").setEphemeral(true).queue((msg) -> {
            if (shouldRemove)
                for (Command command : finalCommands)
                    commandEvent.getGuild().deleteCommandById(command.getId()).queue();

            commandEvent.getJDA().shutdown();
        });
    }
}