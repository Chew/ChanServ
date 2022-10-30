package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.objects.FanclubMessage;
import pw.chew.chewbotcca.util.MiscUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UwUStatsCommand extends SlashCommand {
    public UwUStatsCommand() {
        this.name = "uwustats";
        this.help = "uwu stats";
        this.children = new SlashCommand[]{new TopUwUStatsSubCommand(), new UwUGraphSubCommand(), new UwUUserStatsSubCommand()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        // unused
    }

    public class TopUwUStatsSubCommand extends SlashCommand {
        public TopUwUStatsSubCommand() {
            this.name = "top";
            this.help = "top uwuers";
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.replyEmbeds(buildLeaderboardEmbed(event)).queue();
        }
    }

    public static class UwUGraphSubCommand extends SlashCommand {
        public UwUGraphSubCommand() {
            this.name = "graph";
            this.help = "uwu graph";
            this.cooldown = 60;
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "kind", "The kind of graph")
                    .addChoice("UwUs Per Day", "day")
                    .addChoice("Cumulative UwUs", "cumulative")
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            var choice = event.getOption("kind");
            String name = choice == null ? "UwUs Per Day" : choice.getName();
            String kind = choice == null ? "day" : choice.getAsString();

            XYChart chart = switch (kind) {
                case "day" -> buildUwUsChart(false);
                case "cumulative" -> buildUwUsChart(true);
            };

            // Format the chart
            chart.getStyler().setChartBackgroundColor(new Color(0x36393f));
            chart.getStyler().setYAxisTickLabelsColor(Color.WHITE);
            chart.getStyler().setXAxisTickLabelsColor(Color.WHITE);
            chart.getStyler().setChartFontColor(Color.WHITE);
            chart.getStyler().setPlotBackgroundColor(new Color(0x2f3136));
            chart.getStyler().setSeriesColors(new Color[]{new Color(0x5865F2)});
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setPlotGridLinesColor(new Color(0x8e9297));
            chart.getStyler().setChartTitleFont(new Font("Whitney", Font.BOLD, 20));
            chart.getStyler().setBaseFont(new Font("Whitney", Font.PLAIN, 14));
            chart.getStyler().setxAxisTickLabelsFormattingFunction(val -> LocalDate.of(2020, 9, 5).plusDays(val.intValue()).toString());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(BitmapEncoder.getBufferedImage(chart), "png", os);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            event.reply("Here's your graph!").addFiles(FileUpload.fromData(is, "bruh.png")).queue();
        }

        public XYChart buildUwUsChart(boolean cumulative) {
            // Gather a hash map of date to uwu count
            Map<String, Integer> uwuCounts = new TreeMap<>();
            var cache = MessageModificationHandler.getCache();

            for (FanclubMessage message : cache.values()) {
                if (!message.channelId().equals("751903362794127470")) continue;
                String date = message.getTimeCreated().toLocalDate().toString();
                // Get the amount of days since 09/05/2020
                if (uwuCounts.containsKey(date)) {
                    uwuCounts.put(date, uwuCounts.get(date) + 1);
                } else {
                    uwuCounts.put(date, 1);
                }
            }

            if (cumulative) {
                // Iterate over each entry in the map, and use the cumulative sum to get the total uwus on that day
                int total = 0;
                for (String date : uwuCounts.keySet()) {
                    total += uwuCounts.get(date);
                    uwuCounts.put(date, total);
                }
            }

            double[] x = uwuCounts.keySet().stream()
                .map(value -> LocalDate.parse(value).until(LocalDate.of(2020, 9, 5), ChronoUnit.DAYS) * -1)
                .mapToDouble(value -> value)
                .toArray();
            double[] y = uwuCounts.values().stream().mapToDouble(value -> value).toArray();

            // Build a line graph using XChart
            return QuickChart.getChart(
                (cumulative ? "Cumulative UwUs" : "UwUs Per Day") + " Graph",
                "Day", "UwUs", "uwus/day", x, y);
        }
    }

    private MessageEmbed buildLeaderboardEmbed(SlashCommandEvent event) {
        Map<String, Integer> most = new HashMap<>();

        var cache = MessageModificationHandler.getCache();

        int total = 0;

        for (FanclubMessage message : cache.values()) {
            if (!message.channelId().equals("751903362794127470")) continue;

            Integer amount = most.getOrDefault(message.authorId(), 0);
            amount++;
            most.put(message.authorId(), amount);
            total++;
        }

        most = MiscUtil.sortByValue(most);

        List<String> output = new ArrayList<>();

        boolean iAmTop10 = false;
        int i = 0;
        for (String userId : most.keySet()) {
            i++;
            User user = event.getJDA().getUserById(userId);
            String tag;
            if (user == null) {
                tag = "Unknown User";
            } else {
                tag = user.getAsTag();

                if (user.getIdLong() == event.getUser().getIdLong() && i <= 10) {
                    iAmTop10 = true;
                    output.add("**#" + i + ": " + tag + " - " + most.get(userId) + " uwus**");
                    continue;
                }
            }

            output.add("#" + i + ": " + tag + " - " + most.get(userId) + " uwus");
        }

        output = output.subList(0, 10);

        int your = most.getOrDefault(event.getUser().getId(), 0);

        if (!iAmTop10) {
            output.add("...");
            String tag = event.getUser().getAsTag();
            String userId = event.getUser().getId();
            int position = getPosition(most, userId) + 1;
            if (your == 0) {
                output.add("**#" + (i + 1) + ": " + tag + " - " + 0 + " uwus**");
            } else {
                output.add("**#" + position + ": " + tag + " - " + most.get(userId) + " uwus**");
            }
        }

        float channelAgeInDays = (float) ((System.currentTimeMillis() - event.getJDA().getTextChannelById("751903362794127470").getTimeCreated().toInstant().toEpochMilli()) / 86400000);
        float uwusPerDay = total / channelAgeInDays;

        output.add(0, "Total: " + total);
        output.add(1, "UwU/day: " + uwusPerDay);
        output.add(2, "");

        return new EmbedBuilder()
            .setTitle("Top 10 UwU Leaderboard")
            .setDescription(String.join("\n", output))
            .build();
    }

    public static class UwUUserStatsSubCommand extends SlashCommand {
        public UwUUserStatsSubCommand() {
            this.name = "user";
            this.help = "uwu stats for a user";
            this.options = Collections.singletonList(
                new OptionData(OptionType.USER, "user", "The user to get stats for")
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            User user = event.optUser("user", event.getUser());

            var cache = MessageModificationHandler.getCache();

            // Calculate uwus from the last 24 hours, and the last 7 days, and the last 30 days, and the last 365 days
            long now = System.currentTimeMillis();

            int total = 0;
            int dayTotal = 0;
            int weekTotal = 0;
            int monthTotal = 0;
            int yearTotal = 0;

            long oldest = now;
            long newest = 0;

            for (FanclubMessage message : cache.values()) {
                if (!message.authorId().equals(user.getId())) {
                    continue;
                }

                // Only #uwu channel
                if (!message.channelId().equals("751903362794127470")) {
                    continue;
                }

                long timestamp = message.getTimeCreated().toInstant().toEpochMilli();

                if (timestamp >= (now - 86400000)) dayTotal++;
                if (timestamp >= (now - 86400000 * 7)) weekTotal++;
                if (timestamp >= (now - 86400000L * 30)) monthTotal++;
                if (timestamp >= (now - 86400000L * 365)) yearTotal++;

                total++;

                if (timestamp < oldest) oldest = timestamp;
                if (timestamp > newest) newest = timestamp;
            }

            // Wrap the total in a list, so we can add it to the output
            List<String> stats = new ArrayList<>();
            stats.add("Total: " + total);
            stats.add("Last 24h: " + dayTotal);
            stats.add("Last 7d: " + weekTotal);
            stats.add("Last 1mo: " + monthTotal);
            stats.add("Last 1y: " + yearTotal);

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("UwU Stats for " + user.getAsTag())
                .setDescription(String.join("\n", stats));

            // Get average time between uwus.
            // This is simply the most recent uuw timestamp minus the oldest uwu timestamp, divided by the total uwus
            float tempo = (newest - oldest) / (float)total; // This is in milliseconds
            float tempoInMinutes = tempo / 60000; // Convert to minutes

            embed.addField("UwU Tempo", String.format("%.2f", tempoInMinutes) + " minutes", false);
            embed.addField("Last UwU", TimeFormat.RELATIVE.format(newest), false);

            event.replyEmbeds(embed.build()).queue();
        }
    }

    public int getPosition(Map<String, Integer> map, String key) {
        int i = 0;
        for (String k : map.keySet()) {
            if (k.equals(key)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
