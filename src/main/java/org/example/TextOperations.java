package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TextOperations {

    public static void WriteToTextFile(List<List<Object>> sortedData, String FileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileName))) {

            String header = String.format("%-8s%-12s%-12s%-12s%-12s%-12s", "Index", "T1", "T2", "T3", "△E", "Boltzmann");
            writer.write(header);
            writer.newLine();

            for (List<Object> row : sortedData) {
                String index = row.get(0).toString();
                String T1 = row.get(1).toString();
                String T2 = row.get(2).toString();
                String T3 = row.get(3).toString();
                String E = formatDouble((Double) row.get(4));
                String Boltzmann = formatDouble((Double) row.get(5));

                String Header = String.format("%-8s%-12s%-12s%-12s%-12s%-12s", index, T1, T2, T3, E, Boltzmann);
                writer.write(Header);
                writer.newLine();
            }

            System.out.println("Data successfully written to " + FileName);

        } catch (IOException e) {
            System.err.println("Error writing to text file: " + e.getMessage());
        }
    }

    private static String formatDouble(Double value) {
        if (value == null) {
            return "";
        }
        return String.format(Locale.US, "%.6f", value);  // Change this if you decide to assign the precision
    }

    public static void WriteToExcel(List<List<Object>> SortedData, String FileName) {
        File file = new File(FileName);

        // Check if file exists, and delete if it does
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Preexisting file detected and deleted: " + FileName);
            } else {
                System.err.println("Failed to delete the open preexisting file: " + FileName);
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Processed Data");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Index", "T1", "T2", "T3", "△E", "Boltzmann"};

            // Carefully creating header row
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
                cell.setCellStyle(CreateHeaderStyle(workbook));
            }

            int prevIndex = -1;
            for (int rowIdx = 0; rowIdx < SortedData.size(); rowIdx++) {
                Row row = sheet.createRow(rowIdx + 1);
                List<Object> DataRow = SortedData.get(rowIdx);

                int currentIndex = (int) DataRow.get(0);
                if (prevIndex != -1 && currentIndex > prevIndex + 1) {
                    Row previousRow = sheet.getRow(rowIdx);
                    BoldStyleToRow(previousRow, workbook);
                }

                for (int colIdx = 0; colIdx < DataRow.size(); colIdx++) {
                    Cell cell = row.createCell(colIdx);
                    Object value = DataRow.get(colIdx);
                    if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof String) {
                        cell.setCellValue((String) value);
                    }
                }

                prevIndex = currentIndex;
            }

            for (int col = 0; col < headers.length; col++) {
                sheet.autoSizeColumn(col);
            }

            try (FileOutputStream fileOut = new FileOutputStream(FileName)) {
                workbook.write(fileOut);
            }
            System.out.println("Data successfully written to " + FileName);

        } catch (IOException e) {
            System.err.println("Error writing to Excel file: " + e.getMessage());
        }
    }

    public static CellStyle CreateHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static void BoldStyleToRow(Row row, Workbook workbook) {
        CellStyle BoldStyle = workbook.createCellStyle();
        Font BoldFont = workbook.createFont();
        BoldFont.setBold(true);
        BoldStyle.setFont(BoldFont);

        for (Cell cell : row) {
            cell.setCellStyle(BoldStyle);
        }
    }

    public static double[][] ConvertCSVToArray(String FilePath) {
        List<double[]> Sample_List = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FilePath))) {
            String line;
            boolean Skip_First_Line = true;

            while ((line = br.readLine()) != null) {
                if (Skip_First_Line) {
                    Skip_First_Line = false;
                    continue;
                }

                String[] stringValues = line.split(",");

                double[] Double_Values = Arrays.stream(stringValues)
                        .mapToDouble(Double::parseDouble)
                        .toArray();

                Sample_List.add(Double_Values);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        // Convert List<double[]> to double[][]
        return Sample_List.toArray(new double[0][]);
    }
}
