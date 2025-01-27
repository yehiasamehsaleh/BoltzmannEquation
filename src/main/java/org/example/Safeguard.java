package org.example;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Safeguard {

    private static final List<Pattern> DangerousPatterns = Arrays.asList(
            Pattern.compile("\\.\\."),  // Path traversal
            Pattern.compile("//"),  // Double slash
            Pattern.compile("\\\\\\\\"),  // Backslash
            Pattern.compile("\\$\\("),  // Command substitution
            Pattern.compile("`"),  // Backticks (command execution)
            Pattern.compile("\\|"),  // Pipe
            Pattern.compile(";"),  // Semicolon
            Pattern.compile("&&"),  // Logical AND
            Pattern.compile("\\|\\|"),  // Logical OR
            Pattern.compile("&>"),  // Output redirection
            Pattern.compile("<&"),  // Input redirection
            Pattern.compile(">"),  // Output redirection
            Pattern.compile("<"),  // Input redirection
            Pattern.compile("&1"),  // File descriptor
            Pattern.compile("&2"),  // File descriptor
            Pattern.compile("&"),  // Background execution
            Pattern.compile("\\$\\{?[a-zA-Z0-9_]+\\}?"),  // Environment variable expansion
            Pattern.compile("\\(.*\\)"),  // Subshell execution
            Pattern.compile("\\n"),  // Newline character
            Pattern.compile("\u0000"),  // Null byte injection
            Pattern.compile("exec"),  // Command execution function
            Pattern.compile("2>&1"),  // Merge output and error
            Pattern.compile("\\$\\(.*\\)")  // Nested command substitution
    );

    private static final Pattern SAFE_PATH = Pattern.compile("^[a-zA-Z0-9._/\\-]+$");
    private static final Pattern SAFE_INPUT = Pattern.compile("^[a-zA-Z0-9._/\\s-]+$");

    public static void validateFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new SecurityException("File path cannot be null or empty.");
        }

        filePath = filePath.trim();

        for (Pattern pattern : DangerousPatterns) {
            Matcher matcher = pattern.matcher(filePath);
            if (matcher.find()) {
                throw new SecurityException("File path contains potentially dangerous patterns.");
            }
        }

        if (!SAFE_PATH.matcher(filePath).matches()) {
            throw new SecurityException("File path contains invalid characters.");
        }


        try {
            Path path = Paths.get(filePath).normalize().toAbsolutePath();
            if (!Files.exists(path)) {
                throw new SecurityException("File does not exist: " + filePath);
            }
            if (!Files.isRegularFile(path)) {
                throw new SecurityException("The specified path is not a valid file: " + filePath);
            }
            if (!Files.isReadable(path)) {
                throw new SecurityException("The file is not readable: " + filePath);
            }

        } catch (InvalidPathException e) {
            throw new SecurityException("Invalid file path: " + e.getMessage());
        }
    }

    public static void validateUserInput(String userInput) {
        if (userInput == null || userInput.isEmpty()) {
            throw new SecurityException("Input cannot be null or empty.");
        }

        for (Pattern pattern : DangerousPatterns) {
            Matcher matcher = pattern.matcher(userInput);
            if (matcher.find()) {
                throw new SecurityException("User input contains potentially dangerous patterns.");
            }
        }

        if (!SAFE_INPUT.matcher(userInput).matches()) {
            throw new SecurityException("User input contains invalid characters.");
        }
    }
}
