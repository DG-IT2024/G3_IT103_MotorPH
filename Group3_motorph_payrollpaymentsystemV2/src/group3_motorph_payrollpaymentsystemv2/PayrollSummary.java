/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package group3_motorph_payrollpaymentsystemv2;

import group3_motorph_payrollpaymentsystemV2.Filehandling;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author danilo
 */
public class PayrollSummary extends javax.swing.JFrame {

    public static List<EmployeePayroll> employees = new ArrayList<>();

    /**
     * Creates new form PayrollSummary2
     */
    public PayrollSummary() throws FileNotFoundException, IOException {
        initComponents();

        String csvFile = "PayrollRecords.csv";

        csvRun(csvFile);

        populatecomboboxCoveredPeriods();
        populateByEmployeeNum();
    }

    private void csvRun(String csvFile) throws FileNotFoundException, IOException {
        List<String[]> records = Filehandling.readCSV(csvFile);
        List<EmployeePayroll> employees = parseRecords(records);
        informationTable(employees);
    }

    public static List<EmployeePayroll> parseRecords(List<String[]> records) {

        for (String[] record : records) {
            String employeeNo = record[0];
            String lastName = record[1];
            String firstName = record[2];
            String workedHours = record[3];
            String basicSalary = record[4];
            String hourlyRate = record[5];
            String grossIncome = record[6];
            String sssDeduction = record[7];
            String philHealthDeduction = record[8];
            String pagibigDeduction = record[9];
            String withholdingTax = record[10];
            String coveredMonth = record[11];
            String coveredYear = record[12];
            String benefits = record[13];
            String totalDeductions = record[14];
            String takeHomePay = record[15];

            EmployeePayroll payroll = new EmployeePayroll(employeeNo, lastName, firstName, workedHours, basicSalary, hourlyRate, grossIncome, sssDeduction, philHealthDeduction, pagibigDeduction, withholdingTax, coveredMonth, coveredYear, benefits, totalDeductions, takeHomePay);
            employees.add(payroll);
        }

        return employees;
    }

    private void informationTable(List<EmployeePayroll> employees) {
        DefaultTableModel tableModel = (DefaultTableModel) jTablePayrollSummary.getModel();
        tableModel.setRowCount(0); // Clear existing rows
        for (EmployeePayroll payroll : employees) {
            tableModel.addRow(new Object[]{
                payroll.getEmployeeNo(),
                payroll.getLastName(),
                payroll.getFirstName(),
                payroll.getWorkedHours(),
                payroll.getBasicSalary(),
                payroll.getHourlyRate(),
                payroll.getGrossIncome(),
                payroll.getSssDeduction(),
                payroll.getPhilHealthDeduction(),
                payroll.getPagibigDeduction(),
                payroll.getWithholdingTax(),
                payroll.getCoveredMonth(),
                payroll.getCoveredYear(),
                payroll.getBenefits(),
                payroll.getTotalDeductions(),
                payroll.getTakeHomePay()
            });
        }
    }

    private String[] populateByMonth() {
        String[] months = {"",
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        return months;
    }

    private List<String> populateByYear() {
        List<String> years = new ArrayList<>();
        years.add(""); // Adding empty string as the first element
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 4; i <= currentYear; i++) {  //assuming company store data for 5 years
            years.add(String.valueOf(i));
        }
        return years;
    }

    private void populatecomboboxCoveredPeriods() {

        String[] months = populateByMonth();
        for (String month : months) {
            jComboBoxCoveredMonth.addItem(month);
        }

        List<String> years = populateByYear();
        for (int i = 0; i < years.size(); i++) {
            jComboBoxCoveredYear.addItem(years.get(i));
        }
    }

    private void populateByEmployeeNum() {
        Set<Integer> employeeSet = new HashSet<>();

        for (EmployeePayroll payroll : employees) {
            String id = payroll.getEmployeeNo();
            employeeSet.add(Integer.valueOf(id));
        }

        List<Integer> employeeList = new ArrayList<>(employeeSet);
        Collections.sort(employeeList);

        jComboBoxEmployeeNumber.addItem("");
        for (int id : employeeList) {
            jComboBoxEmployeeNumber.addItem(String.valueOf(id));
        }
    }

    public void filterByCategory(String month, String year, String employeeNumber) {
        DefaultTableModel tableModel = (DefaultTableModel) jTablePayrollSummary.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        List<EmployeePayroll> filteredList = new ArrayList<>();
        for (EmployeePayroll payroll : employees) {
            boolean matches = true;
            if (employeeNumber != null && !employeeNumber.trim().isEmpty()) {
                matches = matches && payroll.getEmployeeNo().equalsIgnoreCase(employeeNumber);
            }
            if (month != null && !month.trim().isEmpty()) {
                matches = matches && payroll.getCoveredMonth().equalsIgnoreCase(month);
            }
            if (year != null && !year.trim().isEmpty()) {
                matches = matches && payroll.getCoveredYear().equalsIgnoreCase(year);
            }
            if (matches) {
                filteredList.add(payroll);
            }
        }

        // Update the table with the filtered list
        for (EmployeePayroll payroll : filteredList) {
            tableModel.addRow(new Object[]{
                payroll.getEmployeeNo(),
                payroll.getLastName(),
                payroll.getFirstName(),
                payroll.getWorkedHours(),
                payroll.getBasicSalary(),
                payroll.getHourlyRate(),
                payroll.getGrossIncome(),
                payroll.getSssDeduction(),
                payroll.getPhilHealthDeduction(),
                payroll.getPagibigDeduction(),
                payroll.getWithholdingTax(),
                payroll.getCoveredMonth(),
                payroll.getCoveredYear(),
                payroll.getBenefits(),
                payroll.getTotalDeductions(),
                payroll.getTakeHomePay()
            });
        }
    }

    public void onFilterAction() {
        String month = jComboBoxCoveredMonth.getSelectedItem() != null ? jComboBoxCoveredMonth.getSelectedItem().toString() : "";
        String year = jComboBoxCoveredYear.getSelectedItem() != null ? jComboBoxCoveredYear.getSelectedItem().toString() : "";
        String employeeNumber = jComboBoxEmployeeNumber.getSelectedItem() != null ? jComboBoxEmployeeNumber.getSelectedItem().toString() : "";

        filterByCategory(month, year, employeeNumber);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTablePayrollSummary = new javax.swing.JTable();
        jComboBoxCoveredMonth = new javax.swing.JComboBox<>();
        jComboBoxCoveredYear = new javax.swing.JComboBox<>();
        jComboBoxEmployeeNumber = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTablePayrollSummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Employee No", "Last Name", "First Name", "Worked Hours", "Basic Salary", "Hourly Rate", "Gross Income", "SSS Deduction", "Philthealth Deduction", "Pagibig Deduction", "Withholding Tax", "Covered Month", "Covered Year", "Benefits", "Total Deductions", "TakeHome-Pay"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePayrollSummary.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(jTablePayrollSummary);

        jComboBoxCoveredMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCoveredMonthActionPerformed(evt);
            }
        });

        jComboBoxCoveredYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCoveredYearActionPerformed(evt);
            }
        });

        jComboBoxEmployeeNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEmployeeNumberActionPerformed(evt);
            }
        });

        jLabel1.setText("Month");

        jLabel2.setText("Year");

        jLabel3.setText("Employee Number");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxEmployeeNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxCoveredYear, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxCoveredMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCoveredMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxCoveredYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxEmployeeNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCoveredMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCoveredMonthActionPerformed
        // TODO add your handling code here:
        onFilterAction();
    }//GEN-LAST:event_jComboBoxCoveredMonthActionPerformed

    private void jComboBoxCoveredYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCoveredYearActionPerformed
        // TODO add your handling code here:
        onFilterAction();
    }//GEN-LAST:event_jComboBoxCoveredYearActionPerformed

    private void jComboBoxEmployeeNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxEmployeeNumberActionPerformed
        // TODO add your handling code here:
        onFilterAction();
    }//GEN-LAST:event_jComboBoxEmployeeNumberActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PayrollSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new PayrollSummary().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(PayrollSummary.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBoxCoveredMonth;
    private javax.swing.JComboBox<String> jComboBoxCoveredYear;
    private javax.swing.JComboBox<String> jComboBoxEmployeeNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTablePayrollSummary;
    // End of variables declaration//GEN-END:variables
}
