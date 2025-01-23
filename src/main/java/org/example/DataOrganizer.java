package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class DataOrganizer {
    private final double[][] data;
    private final int N_Columns;
    private static final double TOLERANCE = 0.000001; // Used for proper grouping. For Example, 29.999999 would be placed in Group 5 (-30 to 0). While 30.000001 would be placed in Group 6 (0 to 30)


    private enum Range {
        GROUP_1("Group 1", -150, -120),
        GROUP_2("Group 2", -120, -90),
        GROUP_3("Group 3", -90, -60),
        GROUP_4("Group 4", -60, -30),
        GROUP_5("Group 5", -30, 0),
        GROUP_6("Group 6", 0, 30),
        GROUP_7("Group 7", 30, 60),
        GROUP_8("Group 8", 60, 90),
        GROUP_9("Group 9", 90, 120),
        GROUP_10("Group 10", 120, 150),
        GROUP_11("Group 11", -181, -150, 150, 181);

        private final String label;
        private final List<double[]> limits;

        Range(String label, double... limits) {
            this.label = label;
            this.limits = new ArrayList<>();
            for (int i = 0; i < limits.length; i += 2) {
                this.limits.add(new double[]{limits[i], limits[i + 1]});
            }
        }

        public boolean contains(double number, double tolerance) {
            return limits.stream().anyMatch(limit -> number >= limit[0] - tolerance && number < limit[1] + tolerance);
        }

        public String getLabel() {
            return label;
        }
    }

    public DataOrganizer(double[][] data, int numColumns) {
        if (data == null || data.length == 0 || numColumns <= 0) {
            throw new IllegalArgumentException("Invalid data or number of columns.");
        }

        this.data = data;
        this.N_Columns = numColumns;

        int rowLength = data[0].length;
        for (int i = 1; i < data.length; i++) {
            if (data[i].length != rowLength) {
                throw new IllegalArgumentException("Improper row lengths in input data.");
            }
        }
    }

    public static String SimilarityGrouping(Double number, double tolerance) {
        if (number == null) {
            return null;
        }

        return Arrays.stream(Range.values())
                .filter(group -> group.contains(number, tolerance))
                .findFirst()
                .map(Range::getLabel)
                .orElse(null);
    }

    public List<List<Object>> DecimalNumbersGrouping() {
        List<List<Object>> groups = new ArrayList<>(data.length);

        for (int index = 0; index < data.length; index++) {
            double[] row = data[index];
            List<Object> processedRow = new ArrayList<>();
            processedRow.add(index);

            for (int i = 0; i < row.length; i++) {
                processedRow.add(i < N_Columns ? SimilarityGrouping(row[i], TOLERANCE) : row[i]);
            }
            groups.add(processedRow);
        }

        return groups;
    }

    public static List<List<Object>> DeduplicateAndSort(List<List<Object>> rawData, int columnIndexForSorting) {
        if (rawData == null || rawData.isEmpty()) {
            return new ArrayList<>(); // Return empty list
        }

        TreeSet<List<Object>> deduplicatedData = new TreeSet<>((row1, row2) -> {
            for (int i = 1; i <= 3; i++) {
                int comparison = CompareObjects(row1.get(i), row2.get(i));
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        });
        deduplicatedData.addAll(rawData);

        List<List<Object>> sortedData = new ArrayList<>(deduplicatedData);

        sortedData.sort((row1, row2) -> {
            Object val1 = row1.get(columnIndexForSorting);
            Object val2 = row2.get(columnIndexForSorting);

            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return 1;  // null values are last
            if (val2 == null) return -1;

            return CompareObjects(val1, val2);
        });

        deduplicatedData.clear(); // Limit Data Retention.To ensure intermediate data is cleared or dereferenced when it is no longer needed.
        rawData.clear();
        return sortedData;
    }

    private static int CompareObjects(Object o1, Object o2) {  // default Timsort
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        if (!(o1 instanceof Comparable<?> && o2 instanceof Comparable<?>)) {
            throw new IllegalArgumentException("Non-comparable objects found: " + o1 + ", " + o2);
        }
        return ((Comparable<Object>) o1).compareTo(o2);
    }
}



