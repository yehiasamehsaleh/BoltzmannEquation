package org.example;

import java.util.*;

public class DataOrganizer {
    private double[][] data;
    private int N_Columns;

    public DataOrganizer(double[][] data, int numColumns) {
        this.data = data;
        this.N_Columns = numColumns;
    }

    private static final List<Range> Ranges = Arrays.asList(
            new Range("Group 1", -150, -120),
            new Range("Group 2", -120, -90),
            new Range("Group 3", -90, -60),
            new Range("Group 4", -60, -30),
            new Range("Group 5", -30, 0),
            new Range("Group 6", 0, 30),
            new Range("Group 7", 30, 60),
            new Range("Group 8", 60, 90),
            new Range("Group 9", 90, 120),
            new Range("Group 10", 120, 150),
            new Range("Group 11", -181, -150, 150, 181) // Handles two disjoint ranges
    );

    static class Range {
        String label;
        List<double[]> limits;

        public Range(String label, double... limits) {
            this.label = label;
            this.limits = new ArrayList<>();
            for (int i = 0; i < limits.length; i += 2) {
                this.limits.add(new double[]{limits[i], limits[i + 1]});
            }
        }

        public boolean contains(double number, double tolerance) {
            for (double[] limit : limits) {
                double lower = limit[0];
                double upper = limit[1];
                if (number >= lower - tolerance && number < upper + tolerance) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String SimilarityGrouping(double number, double tolerance) {
        for (Range range : Ranges) {
            if (range.contains(number, tolerance)) {
                return range.label;
            }
        }
        return null; // No matching group
    }

    public List<List<Object>> Accuracy() {
        List<List<Object>> ProcessedData = new ArrayList<>();

        for (int index = 0; index < data.length; index++) {
            double[] row = data[index];
            List<Object> processedRow = new ArrayList<>();
            processedRow.add(index);

            for (int i = 0; i < row.length; i++) {
                if (i < N_Columns) {
                    processedRow.add(SimilarityGrouping(row[i], 0.000001));
                } else {
                    processedRow.add(row[i]);
                }
            }
            ProcessedData.add(processedRow);
        }
        return ProcessedData;
    }


    public static List<List<Object>> RemoveDuplicatesWithinProcessedData(List<List<Object>> processed_data) {
        Map<List<Object>, List<Integer>> DuplicatesMap = new HashMap<>();
        Map<List<Object>, Integer> FirstOccurrenceMap = new HashMap<>();
        Set<List<Object>> seen = new HashSet<>();
        List<List<Object>> DeduplicatedData = new ArrayList<>();

        for (int i = 0; i < processed_data.size(); i++) {
            List<Object> row = processed_data.get(i);
            List<Object> key = row.subList(1, 4);

            if (!seen.contains(key)) {
                seen.add(key);
                DeduplicatedData.add(row);
                FirstOccurrenceMap.put(key, i);
            } else {
                DuplicatesMap.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }
        }

        if (DuplicatesMap.isEmpty()) {
            System.out.println("No duplicates found. Goodnight, everybody!");
        } else {
            System.out.println("+---------+-----------------------------------+");
            System.out.println("| Group   | Ranges                            |");
            System.out.println("+---------+-----------------------------------+");

            for (Range range : Ranges) {
                StringBuilder rangeValues = new StringBuilder();
                for (double[] limit : range.limits) {
                    rangeValues.append(Arrays.toString(limit)).append(" ");
                }
                System.out.printf("| %-7s | %-35s |\n", range.label, rangeValues.toString().trim());
            }

            System.out.println("+---------+-----------------------------------+");
            System.out.println("********** Duplicates Caught **********");
            int counter = 1;
            for (Map.Entry<List<Object>, List<Integer>> entry : DuplicatesMap.entrySet()) {
                List<Object> DuplicateKey = entry.getKey();
                List<Integer> indices = entry.getValue();
                int firstIndex = FirstOccurrenceMap.get(DuplicateKey);

                System.out.println("\n(" + counter + ") Duplicate key: " + DuplicateKey);
                System.out.println("This structure appears " + (indices.size() + 1) + " times in the original dataset.");
                System.out.println("The first occurrence is at line index: " + (firstIndex + 2));
                System.out.println("This structure is also found duplicated at the following line(s):");
                for (int index : indices) {
                    System.out.println("  - At index: " + (index + 2));
                }
                counter++;
            }
            System.out.println("****************************************");
        }
        return DeduplicatedData;
    }

    public static List<List<Object>> TimSort(List<List<Object>> data, int columnIndex) {
        Collections.sort(data, (row1, row2) -> {
            double value1 = (double) row1.get(columnIndex);
            double value2 = (double) row2.get(columnIndex);
            return Double.compare(value1, value2);
        });
        return data;
    }
}
