package group3_motorph_payrollpaymentsystemV2;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Filehandling {

    // Method to read CSV file and return records
    public static List<String[]> readCSV(String csvFile) throws IOException {

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> records = reader.readAll();
            // Assuming the first row is the header
            records.remove(0);

            return records;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: " + csvFile, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            throw e; // rethrow the exception to indicate failure

        }
    }

    public static void exportTableToCSV(JTable table) {
        String csvFile = "MotorPHEmployeeData.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // Write column headers
            int columnCount = model.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = model.getColumnName(i);
            }
            writer.writeNext(columnNames);

            // Write rows
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String[] rowData = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    rowData[j] = model.getValueAt(i, j).toString();
                }
                writer.writeNext(rowData);
            }

            JOptionPane.showMessageDialog(null, "Database updated successfully");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to export data to CSV file.");
        }
    }
}
