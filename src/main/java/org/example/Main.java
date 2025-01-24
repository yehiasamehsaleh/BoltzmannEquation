package org.example;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the file path: ");
            String filePath = scanner.nextLine();
            Safeguard.validateUserInput(filePath);
            Safeguard.validateFilePath(filePath);

            List<List<Object>> ProcessedData = null;
            String cacheFileName = "ProcessedData.ser";
            File cacheFile = new File(cacheFileName);
            if (cacheFile.exists()) {
                try {
                    ProcessedData = (List<List<Object>>) TextOperations.Serialization.deserialize(cacheFileName);
                } catch (IOException | ClassNotFoundException e) {
                    ProcessedData = DataOperations(filePath);
                    TextOperations.Serialization.serialize(ProcessedData, cacheFileName);
                }
            } else {
                ProcessedData = DataOperations(filePath);
                // Serializing the processed data to avoid reprocessing in the future
                TextOperations.Serialization.serialize(ProcessedData, cacheFileName);
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

    private static List<List<Object>> DataOperations (String filePath) throws IOException {
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

 */
