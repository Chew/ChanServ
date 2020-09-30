package pw.chew.chanserv.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileManager {
    public static List<String> getLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(lines::add);
        } catch (IOException exception) {
            return null;
        }
        return lines;
    }

    public static void appendLine(String fileName, String line) {
        try {
            Files.write(Paths.get(fileName), line.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // As MrIvanPlays once said, there's only a 0.01% chance it'll fail so who cares
        }
    }
}
