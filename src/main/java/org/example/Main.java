package org.example;


import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file path: ");
        String filePath = scanner.nextLine();

        try {
            Safeguard.validateUserInput(filePath);
            Safeguard.validateFilePath(filePath);

            List<List<Object>> processedData = processData(filePath);

            System.out.println("Processed Data in Ascending Order by △E.");
            TextOperations.WriteToExcel(processedData, "Result.xlsx");
            TextOperations.WriteToTextFile(processedData, "Result.txt");

        } catch (NumberFormatException e) {
            System.err.println("Error: The file contains invalid numeric data. Check the file content.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while processing the file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static List<List<Object>> processData(String filePath) throws IOException {
        double[][] sampleData = TextOperations.ConvertCSVToArray(filePath);
        
        DataOrganizer processor = new DataOrganizer(sampleData, 3);
        List<List<Object>> decimalAccuracyGuarantor = processor.DecimalNumbersGrouping();
        List<List<Object>> deduplicatedData = DataOrganizer.DeduplicateAndSort(decimalAccuracyGuarantor, 4);
        List<Double> epsilonValues = deduplicatedData.stream()
                .map(row -> (Double) row.get(4))
                .collect(Collectors.toList());
        
        double[] epsilonArray = epsilonValues.stream().mapToDouble(Double::doubleValue).toArray();
        double[] values = Equation.CalculatePartitionFunction(epsilonArray);

        for (int i = 0; i < deduplicatedData.size(); i++) {
            deduplicatedData.get(i).add(values[i]);
        }

        return deduplicatedData;
    }
}

/*
        CMD:
        java -cp "C:\Users\saleh\javaSamples\21\MB\libs\*;C:\Users\saleh\javaSamples\21\MB\target\classes" org.example.Main

 */
