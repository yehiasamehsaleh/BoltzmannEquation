package org.example;

import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the file path: ");
            String filePath = scanner.nextLine();
            Safeguard.validateUserInput(filePath);
            Safeguard.validateFilePath(filePath);

            List<List<Object>> ProcessedData = DataOperations(filePath);

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
            System.err.println("An unexpected error occurred: " + e.getMessage()); // More descriptive message
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
