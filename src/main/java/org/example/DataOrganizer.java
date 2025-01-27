package org.example;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {
    private static class CacheData implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        final String fileHash;
        final List<List<Object>> data;

        CacheData(String fileHash, List<List<Object>> data) {
            this.fileHash = fileHash;
            this.data = data;
        }
    }

    private static String calculateFileHash(String filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IOException("Failed to calculate file hash", e);
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the file path: ");
            String filePath = scanner.nextLine();
            Safeguard.validateUserInput(filePath);
            Safeguard.validateFilePath(filePath);

            List<List<Object>> ProcessedData;
            String cacheFileName = "ProcessedData.ser";
            File cacheFile = new File(cacheFileName);
            String currentFileHash = calculateFileHash(filePath);

            if (cacheFile.exists()) {
                try {
                    CacheData cachedData = (CacheData) TextOperations.deserialize(cacheFileName);
                    if (cachedData != null && cachedData.fileHash.equals(currentFileHash)) {
                        ProcessedData = cachedData.data;
                        System.out.println("Using cached data - file unchanged.");
                    } else {
                        System.out.println("File changed - reprocessing data.");
                        ProcessedData = ProcessAndCacheData(filePath, currentFileHash, cacheFileName);
                    }
                } catch (Exception e) {
                    System.out.println("Cache error - reprocessing data.");
                    ProcessedData = ProcessAndCacheData(filePath, currentFileHash, cacheFileName);
                }
            } else {
                ProcessedData = ProcessAndCacheData(filePath, currentFileHash, cacheFileName);
            }

            System.out.println("Processed Data in Ascending Order by △E.");
            TextOperations.WriteToExcel(ProcessedData, "Result.xlsx");
            TextOperations.WriteToTextFile(ProcessedData, "Result.txt");

        } catch (NumberFormatException e) {
            System.err.println("Error: The file contains invalid numeric data. Check the file content.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while processing the file: " + e.getMessage());
        } catch (OutOfMemoryError e) {
            System.err.println("Critical Error: Out of Memory! Unable to complete processing.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static List<List<Object>> ProcessAndCacheData(String filePath, String fileHash, String cacheFileName) {
        try {
            List<List<Object>> ProcessedData = DataOperations(filePath);
            CacheData cacheData = new CacheData(fileHash, ProcessedData);
            TextOperations.serialize(cacheData, cacheFileName);
            return ProcessedData;
        } catch (IOException e) {
            System.err.println("Error processing file or saving cache: " + e.getMessage());
            return null;
        }
    }

    private static List<List<Object>> DataOperations(String filePath) throws IOException {
        double[][] sampleData = TextOperations.ConvertCSVToArray(filePath);

        DataOrganizer processor = new DataOrganizer(sampleData, 3);
        List<List<Object>> DecimalAccuracy = processor.DecimalNumbersGrouping();
        List<List<Object>> DeduplicatedData = DataOrganizer.DeduplicateAndSort(DecimalAccuracy, 4);
        List<Double> epsilonValues = DeduplicatedData.stream()
                .map(row -> (Double) row.get(4))
                .collect(Collectors.toList());

        double[] epsilonArray = DeduplicatedData.stream()
                .mapToDouble(row -> (Double) row.get(4))
                .toArray();

        double[] values = Equation.CalculatePartitionFunction(epsilonArray);

        for (int i = 0; i < DeduplicatedData.size(); i++) {
            DeduplicatedData.get(i).add(values[i]);
        }

        return DeduplicatedData;
    }
}

/*

        CMD:
        java -cp "C:\Users\saleh\javaSamples\21\MB\libs\*;C:\Users\saleh\javaSamples\21\MB\target\classes" org.example.Main

        Download all Maven dependencies into a folder and name it libs


 */
