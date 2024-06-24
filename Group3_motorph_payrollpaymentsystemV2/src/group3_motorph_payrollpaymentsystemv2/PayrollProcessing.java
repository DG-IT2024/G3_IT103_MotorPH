/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package group3_motorph_payrollpaymentsystemv2;

import com.opencsv.CSVReader;
import static group3_motorph_payrollpaymentsystemv2.StatutoryDeductions.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import group3_motorph_payrollpaymentsystemV2.Filehandling;
import java.awt.Toolkit;

/**
 *
 * @author danilo
 */
public class PayrollProcessing extends javax.swing.JFrame {

    public List<EmployeeHoursWorked> employeeData = new ArrayList<>();

    /**
     * Creates new form PayrollProcessing
     *
     * @throws java.io.FileNotFoundException
     */
    public PayrollProcessing() throws FileNotFoundException, IOException {

        initComponents();
        String csvWorkedHoursFile = "Employee_Hours_Worked.csv";
        List<String[]> records = Filehandling.readCSV(csvWorkedHoursFile);
        parseRecordsHoursWorked(records);
        populatecomboboxCoveredPeriods();
        setIconImage();

    }

    public void parseRecordsHoursWorked(List<String[]> records) {

        for (String[] record : records) {
            String employeeNumber = record[0];
            String lastName = record[1];
            String firstName = record[2];
            String coveredPeriod = record[3];
            String noOfHoursWorked = record[4];

            EmployeeHoursWorked employeehoursWorked = new EmployeeHoursWorked(employeeNumber, lastName, firstName, coveredPeriod, noOfHoursWorked);
            employeeData.add(employeehoursWorked);

        }

    }

    private String[] populateByMonth() {
        String[] months = {"",
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        return months;
    }

    private List<Integer> populateByYear() {
        List<Integer> years = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 4; i <= currentYear; i++) {  //assuming company store data for 5years
            years.add(i);
        }
        return years;
    }

    private void populatecomboboxCoveredPeriods() {

        String[] months = populateByMonth();
        for (String month : months) {
            jComboBoxCoveredMonth.addItem(month);
        }

        List<Integer> years = populateByYear();
        for (int i = 0; i < years.size(); i++) {
            jComboBoxCoveredYear.addItem(years.get(i).toString());
        }

    }

    public Integer matchWorkedHours() {
        String searchId = jTextFieldEmployeeNum.getText();
        String searchPeriod = jComboBoxCoveredMonth.getSelectedItem().toString() + " " + jComboBoxCoveredYear.getSelectedItem().toString();

        for (int i = 0; i < employeeData.size(); i++) {
            EmployeeHoursWorked employeehoursWorked = employeeData.get(i);
            if (employeehoursWorked.getEmployeeNumber().equals(searchId) && employeehoursWorked.getCoveredPeriod().equals(searchPeriod)) {
                return i;

            }
        }
        JOptionPane.showMessageDialog(null, "No record for the selected covered period");

        return -1; // Return -1 if no match is found
    }

    public String[] createHeadertoRecords() {

        String[] headers = {
            "Employee No.",
            "Last Name",
            "First Name",
            "Worked Hours",
            "Basic Salary",
            "Hourly Rate",
            "Gross Income",
            "SSS Deduction ",
            "Philhealth Deduction ",
            "Pagibig Deduction",
            "Withholding Tax",
            "Covered Month",
            "Covered Year",
            "Benefits",
            "Total Deductions",
            "Take-Home pay"};

        return headers;
    }

    public static boolean isPayrollRecordsCsvEmpty(String csvFile) {

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine = csvReader.readNext();

            // If nextLine is null, the file is empty
            return nextLine == null; // check if statement is true
        } catch (IOException e) {

            return true; // return boolean true if csvFile can't be found in the directory. 
        }
    }

    public void updatePayrollRecords() throws IOException {
        String csvPayrollFile = "PayrollRecords.csv";

        boolean isEmpty = isPayrollRecordsCsvEmpty(csvPayrollFile);

        String[] entry = {
            jTextFieldEmployeeNum.getText(),
            jTextFieldLastName.getText(),
            jTextFieldFirstName.getText(),
            jTextFieldWorkedHours.getText(),
            jTextFieldBasicSalary.getText(),
            jTextFieldHourlyRate.getText(),
            jTextFieldGrossIncome.getText(),
            jTextSssDeduction.getText(),
            jTextFieldPhilHealthDeduction.getText(),
            jTextFieldPagibigDeduction.getText(),
            jTextFieldWHT.getText(),
            jComboBoxCoveredMonth.getSelectedItem().toString(),
            jComboBoxCoveredYear.getSelectedItem().toString(),
            jTextFieldBenefits.getText(),
            jTextFieldTotalDeductions.getText(),
            jTextFieldTakeHomePay.getText()
        };

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPayrollFile, true))) {

            if (isEmpty) {
                writer.writeNext(createHeadertoRecords());
            }

            writer.writeNext(entry);
            JOptionPane.showMessageDialog(null, "Entry added successfully.");
        } catch (IOException ex) {

        }

    }

    public void processPayroll() {

        double basisSalary = Double.parseDouble(jTextFieldBasicSalary.getText());
        double totalDaysMonth = 21; // used to be consistent with the hourlyrate in the MotorPH website.         
        double maxDayHours = 8;//maximum of working hours per day

        double hourlyRate = basisSalary / totalDaysMonth / maxDayHours;
        String formattedHourlyRate = String.format("%.2f", hourlyRate); // Format to 2 decimal places
        jTextFieldHourlyRate.setText(formattedHourlyRate);

        double workedHours = Double.parseDouble(jTextFieldWorkedHours.getText());

        double grossIncome = hourlyRate * workedHours;
        String formattedGrossIncome = String.format("%.2f", grossIncome); // Format to 2 decimal places
        jTextFieldGrossIncome.setText(formattedGrossIncome);
        jTextFieldGrossIncome_S.setText(formattedGrossIncome);

        double sssDeduction = calculateSSS(grossIncome);
        String formattedSssDeduction = String.format("%.2f", sssDeduction);
        jTextSssDeduction.setText(formattedSssDeduction);

        double philhealthDeduction = calculatePhilHealth(grossIncome);
        String formattedPhilhealthDeduction = String.format("%.2f", philhealthDeduction);
        jTextFieldPhilHealthDeduction.setText(formattedPhilhealthDeduction);

        double pagibigDeduction = calculatePagIbig(grossIncome);
        String formattedPagibigDeduction = String.format("%.2f", pagibigDeduction);
        jTextFieldPagibigDeduction.setText(formattedPagibigDeduction);

        double benefitDeductions;
        benefitDeductions = sssDeduction + pagibigDeduction + philhealthDeduction;

        double taxableMonthlyPay;
        taxableMonthlyPay = grossIncome - benefitDeductions;

        double whtax = calculateWHTax(taxableMonthlyPay);
        String formattedWhtax = String.format("%.2f", whtax);
        jTextFieldWHT.setText(formattedWhtax);

        double totalDeduction;
        totalDeduction = whtax + benefitDeductions;
        String formattedTotalDeduction = String.format("%.2f", totalDeduction);
        jTextFieldTotalDeductions.setText(formattedTotalDeduction);

        double totalBenefits;
        totalBenefits = Double.parseDouble(jTextFieldBenefits.getText());

        double takeHomePay;
        takeHomePay = grossIncome - totalDeduction + totalBenefits;
        String formattedTakeHomePay = String.format("%.2f", takeHomePay);
        jTextFieldTakeHomePay.setText(formattedTakeHomePay);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldEmployeeNum = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldLastName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldFirstName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxCoveredYear = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldWorkedHours = new javax.swing.JTextField();
        jButtonClear = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldGrossIncome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextSssDeduction = new javax.swing.JTextField();
        jTextFieldPhilHealthDeduction = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldPagibigDeduction = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldWHT = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldGrossIncome_S = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldBenefits = new javax.swing.JTextField();
        jTextFieldTotalDeductions = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldTakeHomePay = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldHourlyRate = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldBasicSalary = new javax.swing.JTextField();
        jComboBoxCoveredMonth = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jButtonAddtoRecords1 = new javax.swing.JButton();
        jButtonAddtoRecords2 = new javax.swing.JButton();
        jButtonCompute1 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();

        jLabel21.setText("Month");

        jLabel22.setText("Month");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Group 3 | CP2 | A1102");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Employee No.");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        jTextFieldEmployeeNum.setEditable(false);
        jTextFieldEmployeeNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNumActionPerformed(evt);
            }
        });
        jTextFieldEmployeeNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldEmployeeNumKeyTyped(evt);
            }
        });
        jPanel1.add(jTextFieldEmployeeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 170, -1));

        jLabel4.setText("Last Name");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 43, -1, -1));

        jTextFieldLastName.setEditable(false);
        jTextFieldLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLastNameActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 170, -1));

        jLabel5.setText("First Name");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 73, -1, -1));

        jTextFieldFirstName.setEditable(false);
        jTextFieldFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFirstNameActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 170, -1));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 13, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel6.setText("Covered Period");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 33, -1, -1));

        jComboBoxCoveredYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCoveredYearActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxCoveredYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 75, 79, -1));

        jLabel7.setText("Worked Hours");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 103, -1, -1));

        jTextFieldWorkedHours.setEditable(false);
        jTextFieldWorkedHours.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWorkedHoursActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldWorkedHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 170, -1));

        jButtonClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 110, -1, 23));

        jLabel8.setText("Gross Income");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 193, -1, -1));

        jTextFieldGrossIncome.setEditable(false);
        jTextFieldGrossIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGrossIncomeActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldGrossIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 170, -1));

        jLabel9.setText("SSS Deduction ");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 223, -1, -1));

        jTextSssDeduction.setEditable(false);
        jTextSssDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextSssDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextSssDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 170, -1));

        jTextFieldPhilHealthDeduction.setEditable(false);
        jTextFieldPhilHealthDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPhilHealthDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldPhilHealthDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 170, -1));

        jLabel10.setText("PhilHealth Deduction ");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 253, -1, -1));

        jLabel11.setText("Pagibig Deduction ");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 283, -1, -1));

        jTextFieldPagibigDeduction.setEditable(false);
        jTextFieldPagibigDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPagibigDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldPagibigDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 280, 170, -1));

        jLabel12.setText("Withholding Tax");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 313, -1, -1));

        jTextFieldWHT.setEditable(false);
        jTextFieldWHT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWHTActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldWHT, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, 170, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel13.setText("Summary");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, -1, -1));

        jLabel14.setText("Gross Income");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 170, -1, -1));

        jTextFieldGrossIncome_S.setEditable(false);
        jTextFieldGrossIncome_S.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGrossIncome_SActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldGrossIncome_S, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 160, 112, -1));

        jLabel15.setText("Benefits");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 200, -1, -1));

        jTextFieldBenefits.setEditable(false);
        jTextFieldBenefits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBenefitsActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBenefits, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, 112, -1));

        jTextFieldTotalDeductions.setEditable(false);
        jTextFieldTotalDeductions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTotalDeductionsActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldTotalDeductions, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 220, 112, -1));

        jLabel16.setText("Total Deductions");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, -1, -1));

        jTextFieldTakeHomePay.setEditable(false);
        jTextFieldTakeHomePay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTakeHomePayActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldTakeHomePay, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, 112, -1));

        jLabel17.setText("TAKE-HOME PAY");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 260, -1, -1));

        jTextFieldHourlyRate.setEditable(false);
        jTextFieldHourlyRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHourlyRateActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldHourlyRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 160, 170, -1));

        jLabel18.setText("Basic Salary");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 133, -1, -1));

        jLabel19.setText("Hourly Rate");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 163, -1, -1));

        jTextFieldBasicSalary.setEditable(false);
        jTextFieldBasicSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBasicSalaryActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBasicSalary, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 170, -1));

        jComboBoxCoveredMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jComboBoxCoveredMonthMouseClicked(evt);
            }
        });
        jComboBoxCoveredMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCoveredMonthActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxCoveredMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 75, 112, -1));

        jLabel20.setText("Month");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 56, -1, -1));

        jLabel23.setText("Year");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 56, -1, -1));

        jButtonAddtoRecords1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonAddtoRecords1.setText("Payroll Summary");
        jButtonAddtoRecords1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddtoRecords1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAddtoRecords1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, 140, 23));

        jButtonAddtoRecords2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonAddtoRecords2.setText("Add to Records");
        jButtonAddtoRecords2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddtoRecords2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAddtoRecords2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 290, 140, 23));

        jButtonCompute1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCompute1.setText("Compute");
        jButtonCompute1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompute1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonCompute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 110, -1, 23));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 620, 360));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/group3_motorph_payrollpaymentsystemv2/PayrollD.jpg"))); // NOI18N
        getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmployeeNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmployeeNumActionPerformed

    private void jTextFieldEmployeeNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNumKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_jTextFieldEmployeeNumKeyTyped

    private void jTextFieldLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLastNameActionPerformed

    private void jTextFieldFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFirstNameActionPerformed

    private void jTextFieldWorkedHoursActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldWorkedHoursActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jTextFieldWorkedHoursActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        // TODO add your handling code here:
        jTextFieldBenefits.setText("");
        jTextFieldGrossIncome.setText("");
        jTextFieldGrossIncome_S.setText("");
        jTextFieldHourlyRate.setText("");
        jTextFieldPagibigDeduction.setText("");
        jTextFieldPhilHealthDeduction.setText("");
        jTextFieldTakeHomePay.setText("");
        jTextFieldTotalDeductions.setText("");
        jTextFieldWHT.setText("");
        jTextFieldWorkedHours.setText("");
        jTextSssDeduction.setText("");

        jComboBoxCoveredMonth.setSelectedIndex(-1); // Assuming you want to deselect any selection
        jComboBoxCoveredYear.setSelectedIndex(-1); // Assuming you want to deselect any selection


    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jTextFieldGrossIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGrossIncomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGrossIncomeActionPerformed

    private void jTextSssDeductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextSssDeductionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextSssDeductionActionPerformed

    private void jTextFieldPhilHealthDeductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhilHealthDeductionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhilHealthDeductionActionPerformed

    private void jTextFieldPagibigDeductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPagibigDeductionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPagibigDeductionActionPerformed

    private void jTextFieldWHTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldWHTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldWHTActionPerformed

    private void jTextFieldGrossIncome_SActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGrossIncome_SActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGrossIncome_SActionPerformed

    private void jTextFieldBenefitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBenefitsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBenefitsActionPerformed

    private void jTextFieldTotalDeductionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTotalDeductionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTotalDeductionsActionPerformed

    private void jTextFieldTakeHomePayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTakeHomePayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTakeHomePayActionPerformed

    private void jTextFieldHourlyRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHourlyRateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldHourlyRateActionPerformed

    private void jTextFieldBasicSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBasicSalaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBasicSalaryActionPerformed

    private void jComboBoxCoveredMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCoveredMonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCoveredMonthActionPerformed

    private void jComboBoxCoveredYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCoveredYearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCoveredYearActionPerformed

    private void jComboBoxCoveredMonthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxCoveredMonthMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jComboBoxCoveredMonthMouseClicked

    private void jButtonAddtoRecords1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddtoRecords1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonAddtoRecords1ActionPerformed

    private void jButtonAddtoRecords2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddtoRecords2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonAddtoRecords2ActionPerformed

    private void jButtonCompute1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompute1ActionPerformed
        // TODO add your handling code here:
        int searchIndex = matchWorkedHours();
        String workedHours = employeeData.get(searchIndex).getNoOfHoursWorked();
        jTextFieldWorkedHours.setText(workedHours);

        processPayroll();

    }//GEN-LAST:event_jButtonCompute1ActionPerformed

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
            java.util.logging.Logger.getLogger(PayrollProcessing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollProcessing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollProcessing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollProcessing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new PayrollProcessing().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(EmployeeProfile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddtoRecords1;
    private javax.swing.JButton jButtonAddtoRecords2;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonCompute1;
    private javax.swing.JComboBox<String> jComboBoxCoveredMonth;
    private javax.swing.JComboBox<String> jComboBoxCoveredYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JTextField jTextFieldBasicSalary;
    public javax.swing.JTextField jTextFieldBenefits;
    public javax.swing.JTextField jTextFieldEmployeeNum;
    public javax.swing.JTextField jTextFieldFirstName;
    public javax.swing.JTextField jTextFieldGrossIncome;
    public javax.swing.JTextField jTextFieldGrossIncome_S;
    public javax.swing.JTextField jTextFieldHourlyRate;
    public javax.swing.JTextField jTextFieldLastName;
    public javax.swing.JTextField jTextFieldPagibigDeduction;
    public javax.swing.JTextField jTextFieldPhilHealthDeduction;
    public javax.swing.JTextField jTextFieldTakeHomePay;
    public javax.swing.JTextField jTextFieldTotalDeductions;
    public javax.swing.JTextField jTextFieldWHT;
    public javax.swing.JTextField jTextFieldWorkedHours;
    public javax.swing.JTextField jTextSssDeduction;
    // End of variables declaration//GEN-END:variables

    private void setIconImage() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.jpg")));
    }
}
