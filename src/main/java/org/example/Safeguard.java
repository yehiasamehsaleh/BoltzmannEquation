package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Safeguard {

    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9-_\\s/.:\\\\]+$");
    public static String ValidateFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        filePath = filePath.trim();

        if (!filePath.endsWith(".csv")) {
            throw new IllegalArgumentException("File must have a .csv extension.");
        }
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("The specified path is not a valid file: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("The file is not readable: " +
                    "\n1.The file format must be a valid CSV (comma-separated values) with the extension \".csv\".\n" +
                    "\n2.The expected format for each line is: (The first title line of the .csv will be disregarded)\n" +
                    "  T1, T2, T3, △E\n" +
                    "  NumberT1, NumberT2, NumberT3, E\n" +
                    "  NumberT1, NumberT2, NumberT3, E\n" +
                    "  ...\n" +
                    "\n3.The file could be open or inaccessible: " + filePath);
        }
        return path.normalize().toString(); // Return the sanitized path

    }
    public static void ValidateUserInput(String userInput) {
        if (!VALID_INPUT_PATTERN.matcher(userInput).matches()) {
            throw new IllegalArgumentException("Invalid user input. Please enter a valid string.");
        }
    }
}
