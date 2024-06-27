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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.YearMonth;

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

    public boolean isEntryUnique() {
        boolean condition = true;

        return condition;
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
        
        String monthString= jComboBoxCoveredMonth.getSelectedItem().toString();
        Month monthEnum = Month.valueOf(monthString.toUpperCase());
        int month = monthEnum.getValue();
        int year = Integer.parseInt(jComboBoxCoveredYear.getSelectedItem().toString());

        double basisSalary = Double.parseDouble(jTextFieldBasicSalary.getText());
        double totalDaysMonth = getWorkingDays(month, year); //          
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

    public int getWorkingDays(int month, int year) {

        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth(); //determines the total days in a particular month, in a given year
        int workingDays = 0;

        List<MonthDay> holidays = getPhilippineHolidays(year); //List all declared holiday

        //determine the number of working days
        for (int day = 1; day <= totalDays; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            if (isWorkingDay(date, holidays)) { // adds one day if true
                workingDays++;
            }
        }
                       
        return workingDays;
    }

    private static boolean isWorkingDay(LocalDate date, List<MonthDay> holidays) {
        DayOfWeek dayOfWeek = date.getDayOfWeek(); //Retrieves the day of the week for the given date (e.i. Monday-Sunday)
        MonthDay monthDay = MonthDay.of(date.getMonthValue(), date.getDayOfMonth());   // Creates a MonthDay instance for the given date, ignoring the year
        return dayOfWeek != DayOfWeek.SUNDAY && !holidays.contains(monthDay);
    }

    public static List<MonthDay> getPhilippineHolidays(int year) {
        List<MonthDay> holidays = new ArrayList<>();

        // Regular holidays
        holidays.add(MonthDay.of(1, 1));   // New Year's Day
        holidays.add(MonthDay.of(4, 9));   // Araw ng Kagitingan
        holidays.add(MonthDay.of(5, 1));   // Labor Day
        holidays.add(MonthDay.of(6, 12));  // Independence Day
        holidays.add(MonthDay.of(11, 30)); // Bonifacio Day
        holidays.add(MonthDay.of(12, 25)); // Christmas Day
        holidays.add(MonthDay.of(12, 30)); // Rizal Day

        // Special holidays
        holidays.add(MonthDay.of(2, 25));  // EDSA People Power Revolution Anniversary
        holidays.add(MonthDay.of(8, 21));  // Ninoy Aquino Day
        holidays.add(MonthDay.of(11, 1));  // All Saints' Day
        holidays.add(MonthDay.of(11, 2));  // All Souls' Day
        holidays.add(MonthDay.of(12, 8));  // Feast of the Immaculate Conception
        holidays.add(MonthDay.of(12, 24)); // Christmas Eve
        holidays.add(MonthDay.of(12, 31)); // New Year's Eve

        // Moveable holidays (examples for given years, adjust manually)
        if (year == 2020) {
            holidays.add(MonthDay.of(4, 9));  // Maundy Thursday 2020
            holidays.add(MonthDay.of(4, 10)); // Good Friday 2020
            holidays.add(MonthDay.of(4, 11)); // Black Saturday 2020
            holidays.add(MonthDay.of(1, 25)); // Chinese New Year 2020
            holidays.add(MonthDay.of(5, 24)); // Eid'l Fitr 2020 (adjust manually)
            holidays.add(MonthDay.of(7, 31)); // Eid'l Adha 2020 (adjust manually)
        } else if (year == 2021) {
            holidays.add(MonthDay.of(4, 1));  // Maundy Thursday 2021
            holidays.add(MonthDay.of(4, 2));  // Good Friday 2021
            holidays.add(MonthDay.of(4, 3));  // Black Saturday 2021
            holidays.add(MonthDay.of(2, 12)); // Chinese New Year 2021
            holidays.add(MonthDay.of(5, 13)); // Eid'l Fitr 2021 (adjust manually)
            holidays.add(MonthDay.of(7, 20)); // Eid'l Adha 2021 (adjust manually)
        } else if (year == 2022) {
            holidays.add(MonthDay.of(4, 14)); // Maundy Thursday 2022
            holidays.add(MonthDay.of(4, 15)); // Good Friday 2022
            holidays.add(MonthDay.of(4, 16)); // Black Saturday 2022
            holidays.add(MonthDay.of(2, 1));  // Chinese New Year 2022
            holidays.add(MonthDay.of(5, 3));  // Eid'l Fitr 2022 (adjust manually)
            holidays.add(MonthDay.of(7, 10)); // Eid'l Adha 2022 (adjust manually)
        } else if (year == 2023) {
            holidays.add(MonthDay.of(4, 6));  // Maundy Thursday 2023
            holidays.add(MonthDay.of(4, 7));  // Good Friday 2023
            holidays.add(MonthDay.of(4, 8));  // Black Saturday 2023
            holidays.add(MonthDay.of(1, 22)); // Chinese New Year 2023
            holidays.add(MonthDay.of(4, 21)); // Eid'l Fitr 2023 (adjust manually)
            holidays.add(MonthDay.of(6, 28)); // Eid'l Adha 2023 (adjust manually)
        } else if (year == 2024) {
            holidays.add(MonthDay.of(3, 28)); // Maundy Thursday 2024
            holidays.add(MonthDay.of(3, 29)); // Good Friday 2024
            holidays.add(MonthDay.of(3, 30)); // Black Saturday 2024
            holidays.add(MonthDay.of(2, 10)); // Chinese New Year 2024
            holidays.add(MonthDay.of(4, 10)); // Eid'l Fitr 2024 (adjust manually)
            holidays.add(MonthDay.of(6, 17)); // Eid'l Adha 2024 (adjust manually)
        }

        return holidays;
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
        jButtonClose = new javax.swing.JButton();
        jButtonAddtoRecords = new javax.swing.JButton();
        jButtonCompute1 = new javax.swing.JButton();
        jButtonPayrollSummary = new javax.swing.JButton();
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
        jTextFieldEmployeeNum.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldEmployeeNum.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldEmployeeNum.setEnabled(false);
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
        jPanel1.add(jTextFieldEmployeeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 170, 22));

        jLabel4.setText("Last Name");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 43, -1, -1));

        jTextFieldLastName.setEditable(false);
        jTextFieldLastName.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldLastName.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldLastName.setEnabled(false);
        jTextFieldLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLastNameActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 170, 22));

        jLabel5.setText("First Name");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 73, -1, -1));

        jTextFieldFirstName.setEditable(false);
        jTextFieldFirstName.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldFirstName.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldFirstName.setEnabled(false);
        jTextFieldFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFirstNameActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 170, 22));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 13, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel6.setText("Covered Period");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 13, -1, -1));

        jComboBoxCoveredYear.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jComboBoxCoveredYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCoveredYearActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxCoveredYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 55, 79, -1));

        jLabel7.setText("Worked Hours");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 103, -1, -1));

        jTextFieldWorkedHours.setEditable(false);
        jTextFieldWorkedHours.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldWorkedHours.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldWorkedHours.setEnabled(false);
        jTextFieldWorkedHours.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWorkedHoursActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldWorkedHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 170, 22));

        jButtonClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonClear.setText("Clear");
        jButtonClear.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(407, 90, 83, 23));

        jLabel8.setText("Gross Income");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 193, -1, -1));

        jTextFieldGrossIncome.setEditable(false);
        jTextFieldGrossIncome.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldGrossIncome.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldGrossIncome.setEnabled(false);
        jTextFieldGrossIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGrossIncomeActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldGrossIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 170, 22));

        jLabel9.setText("SSS Deduction ");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 223, -1, -1));

        jTextSssDeduction.setEditable(false);
        jTextSssDeduction.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextSssDeduction.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextSssDeduction.setEnabled(false);
        jTextSssDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextSssDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextSssDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 170, 22));

        jTextFieldPhilHealthDeduction.setEditable(false);
        jTextFieldPhilHealthDeduction.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldPhilHealthDeduction.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldPhilHealthDeduction.setEnabled(false);
        jTextFieldPhilHealthDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPhilHealthDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldPhilHealthDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 170, 22));

        jLabel10.setText("PhilHealth Deduction ");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 253, -1, -1));

        jLabel11.setText("Pagibig Deduction ");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 283, -1, -1));

        jTextFieldPagibigDeduction.setEditable(false);
        jTextFieldPagibigDeduction.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldPagibigDeduction.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldPagibigDeduction.setEnabled(false);
        jTextFieldPagibigDeduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPagibigDeductionActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldPagibigDeduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 280, 170, 22));

        jLabel12.setText("Withholding Tax");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 313, -1, -1));

        jTextFieldWHT.setEditable(false);
        jTextFieldWHT.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldWHT.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldWHT.setEnabled(false);
        jTextFieldWHT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWHTActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldWHT, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, 170, 22));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel13.setText("Summary");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, -1, -1));

        jLabel14.setText("Gross Income");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 163, -1, -1));

        jTextFieldGrossIncome_S.setEditable(false);
        jTextFieldGrossIncome_S.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldGrossIncome_S.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldGrossIncome_S.setEnabled(false);
        jTextFieldGrossIncome_S.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGrossIncome_SActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldGrossIncome_S, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 160, 112, 22));

        jLabel15.setText("Benefits");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 193, -1, -1));

        jTextFieldBenefits.setEditable(false);
        jTextFieldBenefits.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldBenefits.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldBenefits.setEnabled(false);
        jTextFieldBenefits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBenefitsActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBenefits, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, 112, 22));

        jTextFieldTotalDeductions.setEditable(false);
        jTextFieldTotalDeductions.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldTotalDeductions.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldTotalDeductions.setEnabled(false);
        jTextFieldTotalDeductions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTotalDeductionsActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldTotalDeductions, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 220, 112, 22));

        jLabel16.setText("Total Deductions");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 223, -1, -1));

        jTextFieldTakeHomePay.setEditable(false);
        jTextFieldTakeHomePay.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldTakeHomePay.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldTakeHomePay.setEnabled(false);
        jTextFieldTakeHomePay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTakeHomePayActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldTakeHomePay, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, 112, 22));

        jLabel17.setText("TAKE-HOME PAY");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 253, -1, -1));

        jTextFieldHourlyRate.setEditable(false);
        jTextFieldHourlyRate.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldHourlyRate.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldHourlyRate.setEnabled(false);
        jTextFieldHourlyRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHourlyRateActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldHourlyRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 160, 170, 22));

        jLabel18.setText("Basic Salary");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 133, -1, -1));

        jLabel19.setText("Hourly Rate");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 163, -1, -1));

        jTextFieldBasicSalary.setEditable(false);
        jTextFieldBasicSalary.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldBasicSalary.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextFieldBasicSalary.setEnabled(false);
        jTextFieldBasicSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBasicSalaryActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBasicSalary, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 170, 22));

        jComboBoxCoveredMonth.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
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
        jPanel1.add(jComboBoxCoveredMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 55, 112, -1));

        jLabel20.setText("Month");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 36, -1, -1));

        jLabel23.setText("Year");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 36, -1, -1));

        jButtonClose.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonClose.setText("CLOSE");
        jButtonClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 280, 70, 23));

        jButtonAddtoRecords.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonAddtoRecords.setText("Add to Records");
        jButtonAddtoRecords.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jButtonAddtoRecords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddtoRecordsActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAddtoRecords, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 280, 140, 23));

        jButtonCompute1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCompute1.setText("Compute");
        jButtonCompute1.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jButtonCompute1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompute1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonCompute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 83, 23));

        jButtonPayrollSummary.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonPayrollSummary.setText("Payroll Summary");
        jButtonPayrollSummary.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        jButtonPayrollSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPayrollSummaryActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonPayrollSummary, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 310, 140, 23));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 620, 350));

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

        jComboBoxCoveredMonth.setSelectedIndex(0); // Assuming you want to deselect any selection
        jComboBoxCoveredYear.setSelectedIndex(0); // Assuming you want to deselect any selection


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

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        // TODO add your handling code here:
        setVisible(false);


    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonAddtoRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddtoRecordsActionPerformed
        // TODO add your handling code here:

        int response = JOptionPane.showConfirmDialog(null, "Do you want to add this to the payroll records?",
                "Update Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                updatePayrollRecords();
            } catch (IOException ex) {
                Logger.getLogger(PayrollProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {

        }
    }//GEN-LAST:event_jButtonAddtoRecordsActionPerformed

    private void jButtonCompute1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompute1ActionPerformed
        // TODO add your handling code here:
        int searchIndex = matchWorkedHours();
        String workedHours = employeeData.get(searchIndex).getNoOfHoursWorked();
        jTextFieldWorkedHours.setText(workedHours);

        processPayroll();

    }//GEN-LAST:event_jButtonCompute1ActionPerformed

    private void jButtonPayrollSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPayrollSummaryActionPerformed
        // TODO add your handling code here:

        try {

            setVisible(false);
            PayrollSummary payroll = new PayrollSummary();
            // Display the window
            payroll.setVisible(true);
            payroll.pack();
            payroll.setDefaultCloseOperation(PayrollProcessing.DISPOSE_ON_CLOSE); //if viewEmployeeFrame is close, main frame will not close.

        } catch (IOException ex) {
            Logger.getLogger(EmployeeProfile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonPayrollSummaryActionPerformed

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
    private javax.swing.JButton jButtonAddtoRecords;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonCompute1;
    private javax.swing.JButton jButtonPayrollSummary;
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
