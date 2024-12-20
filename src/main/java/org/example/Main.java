package org.example;

import java.util.Scanner;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file path: ");
        String File_Path = scanner.nextLine();

        double[][] SampleData;
        try {
            Safeguard.validateUserInput(File_Path);
            Safeguard.validateFilePath(File_Path);

            SampleData = TextOperations.ConvertCSVToArray(File_Path);
            DataOrganizer processor = new DataOrganizer(SampleData, 3);

            List<List<Object>> ProcessedData = processor.ProcessData();
            List<List<Object>> DeduplicatedData = DataOrganizer.RemoveDuplicatesWithinProcessedData(ProcessedData);
            List<List<Object>> SortedData = DataOrganizer.MergeSort(DeduplicatedData, 4);

            List<Double> EpsilonValues = new ArrayList<>();
            for (List<Object> row : SortedData) {
                EpsilonValues.add((Double) row.get(4));
            }
            double[] EpsilonArray = EpsilonValues.stream().mapToDouble(Double::doubleValue).toArray();
            double[] Values = Equation.CalculatePartitionFunction(EpsilonArray);

            int i = 0;
            do {
                if (i < SortedData.size()) {
                    SortedData.get(i).add(Values[i]);
                    i++;
                }
            } while (i < SortedData.size());

            scanner.close();
            System.out.println("Processed Data in Ascending Order by △E.");
            TextOperations.WriteToExcel(SortedData, "Result.xlsx");
            TextOperations.WriteToTextFile(SortedData, "Result.txt");

        } catch (NumberFormatException e) {
            System.err.println("Error: The file contains invalid numeric data. Please check the file content.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while processing the file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

}

/*

        CMD:
        java -cp "C:\Users\saleh\javaSamples\21\MB\libs\*;C:\Users\saleh\javaSamples\21\MB\target\classes" org.example.Main


        Compiler:
            String FilePath = "Example2.csv";
            double[][] Sample_Data = TextOperations.ConvertCSVToArray(FilePath);
            for (double[] row : Sample_Data) {
                System.out.println(Arrays.toString(row));
            }
        scanner.close();


        for (List<Object> row : SortedData) {
            System.out.println(row);
        }

        CSVToArrayConverter.main(null);



*/

/*
            for (int i = 0; i < SortedData.size(); i++) {
                SortedData.get(i).add(metrics[i]);
            }
*/