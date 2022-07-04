/*
 * Copyright (C) 2020 Chew
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pw.chew.chanserv;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import pw.chew.chanserv.listeners.BanHandler;
import pw.chew.chanserv.listeners.MemberJoinHandler;
import pw.chew.chanserv.listeners.MemberLeaveHandler;
import pw.chew.chanserv.listeners.MessageHandler;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.listeners.ReadyHandler;
import pw.chew.chanserv.listeners.RoryListener;
import pw.chew.chanserv.listeners.UnbanHandler;
import pw.chew.chanserv.listeners.UwUChannelHandler;
import pw.chew.chanserv.util.PropertiesManager;
import pw.chew.chewbotcca.commands.owner.EvalCommand;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ChanServ {
    private static JDA jda;
    public static EventWaiter waiter = new EventWaiter();

    public static void main(String[] args) throws IOException, LoginException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Load properties into the PropertiesManager
        Properties prop = new Properties();
        prop.load(new FileInputStream("bot.properties"));
        PropertiesManager.loadProperties(prop);

        // Initialize the waiter and client
        CommandClientBuilder client = new CommandClientBuilder();

        // Set the client settings
        client.setOwnerId(PropertiesManager.getOwnerId());
        client.setPrefix(PropertiesManager.getPrefix());
        client.setActivity(Activity.watching("rory!!"));

        client.useHelpBuilder(false);

        client.addCommand(new EvalCommand());
        client.addSlashCommands(getSlashCommands());

        client.forceGuildOnly("134445052805120001");

        // Register JDA
        jda = JDABuilder.createDefault(PropertiesManager.getToken())
            .setChunkingFilter(ChunkingFilter.ALL)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .enableIntents(GatewayIntent.GUILD_PRESENCES)
            .enableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI)
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.playing("Booting..."))
            .addEventListeners(waiter,
                client.build(),
                new BanHandler(),
                new MemberJoinHandler(),
                new MemberLeaveHandler(),
                new MessageHandler(),
                new MessageModificationHandler(),
                new ReadyHandler(),
                new RoryListener(),
                new UnbanHandler(),
                new UwUChannelHandler()
            )
            .build();
    }

    public static JDA getJDA() {
        return jda;
    }

    private static SlashCommand[] getSlashCommands() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Reflections reflections = new Reflections("pw.chew.chanserv.commands");
        Set<Class<? extends SlashCommand>> subTypes = reflections.getSubTypesOf(SlashCommand.class);
        List<SlashCommand> commands = new ArrayList<>();

        for (Class<? extends SlashCommand> theClass : subTypes) {
            // Ignore if "SubCommand" is in the name
            if (theClass.getSimpleName().contains("SubCommand")) continue;

            commands.add(theClass.getDeclaredConstructor().newInstance());
            LoggerFactory.getLogger(theClass).debug("Loaded SlashCommand Successfully!");
        }

        return commands.toArray(new SlashCommand[0]);
    }
}
