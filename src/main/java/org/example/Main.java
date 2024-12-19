// In case of future problems use poi library directly from project structure instead of editing the maven .xml file
package org.example;

import java.util.Scanner;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        //CSVToArrayConverter.main(null);

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

            List<Double> epsilonValues = new ArrayList<>();
            for (List<Object> row : SortedData) {
                epsilonValues.add((Double) row.get(4));
            }
            double[] epsilonArray = epsilonValues.stream().mapToDouble(Double::doubleValue).toArray();
            double[] metrics = Equation.CalculatePartitionFunction(epsilonArray);
            for (int i = 0; i < SortedData.size(); i++) {
                SortedData.get(i).add(metrics[i]);
            }

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
*/
