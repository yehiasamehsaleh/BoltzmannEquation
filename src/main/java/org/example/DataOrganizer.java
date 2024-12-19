package org.example;

import java.util.*;

public class DataOrganizer {
    private double[][] data;
    private int N_Columns;

    public DataOrganizer(double[][] data, int numColumns) {
        this.data = data;
        this.N_Columns = numColumns;
    }

    public static String SimilarityGrouping(double number, double tolerance) {
        Map<String, List<double[]>> ranges = new LinkedHashMap<String, List<double[]>>() {{
            put("Group 1", Collections.singletonList(new double[]{-30, 0}));
            put("Group 2", Collections.singletonList(new double[]{0, 30}));
            put("Group 3", Collections.singletonList(new double[]{30, 60}));
            put("Group 4", Collections.singletonList(new double[]{-60, -30}));
            put("Group 5", Collections.singletonList(new double[]{60, 90}));
            put("Group 6", Collections.singletonList(new double[]{-90, -60}));
            put("Group 7", Collections.singletonList(new double[]{-120, -90}));
            put("Group 8", Collections.singletonList(new double[]{90, 120}));
            put("Group 9", Collections.singletonList(new double[]{-150, -120}));
            put("Group 10", Collections.singletonList(new double[]{120, 150}));
            put("Group 11", Arrays.asList(new double[]{-181, -150}, new double[]{150, 181}));
        }};

        for (Map.Entry<String, List<double[]>> entry : ranges.entrySet()) {
            String label = entry.getKey();
            for (double[] range : entry.getValue()) {
                double lower = range[0];
                double upper = range[1];
                if (number >= lower - tolerance && number < upper + tolerance) {
                    return label;
                }
            }
        }
        return null;
    }

    public List<List<Object>> ProcessData() {
        List<List<Object>> ProcessedData = new ArrayList<>();

        for (int index = 0; index < data.length; index++) { // Use data.length instead of data.size()
            double[] row = data[index];
            List<Object> processedRow = new ArrayList<>();
            processedRow.add(index);

            for (int i = 0; i < row.length; i++) {
                if (i < N_Columns) {
                    processedRow.add(SimilarityGrouping(row[i], 0.000001));   // Change this if you decide to change the precision (TextFileWriter:Line 40 and DataTask line 50)
                } else {
                    processedRow.add(row[i]);
                }
            }
            ProcessedData.add(processedRow);
        }

        return ProcessedData;
    }


    public static List<List<Object>> RemoveDuplicatesWithinProcessedData(List<List<Object>> processed_data) {
        Set<List<Object>> seen = new HashSet<>();
        List<List<Object>> DeduplicatedData = new ArrayList<>();

        for (List<Object> row : processed_data) {
            // Use the first 3 columns (indices 1 to 3) for checking for duplicates
            List<Object> key = row.subList(1, 4);
            if (!seen.contains(key)) {
                seen.add(key);
                DeduplicatedData.add(row);
            }
        }
        return DeduplicatedData;
    }

    public static List<List<Object>> MergeSort(List<List<Object>> data, int columnIndex) {
        if (data.size() <= 1) {
            return data;
        }

        int mid = data.size() / 2;
        List<List<Object>> left = MergeSort(data.subList(0, mid), columnIndex);
        List<List<Object>> right = MergeSort(data.subList(mid, data.size()), columnIndex);

        return merge(left, right, columnIndex);
    }

    private static List<List<Object>> merge(List<List<Object>> left, List<List<Object>> right, int columnIndex) {
        List<List<Object>> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if ((double) left.get(i).get(columnIndex) <= (double) right.get(j).get(columnIndex)) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i++));
        }

        while (j < right.size()) {
            merged.add(right.get(j++));
        }
        return merged;
    }
}