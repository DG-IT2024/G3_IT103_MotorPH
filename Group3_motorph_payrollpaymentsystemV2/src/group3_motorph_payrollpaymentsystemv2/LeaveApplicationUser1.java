/* 
Notes:
Allows late filling of leave
Doesn't allow startDate to be after endDate
User can update or cancel leave application that is still pending
Leave Status will be marked as pending, approved and rejected
 */
package group3_motorph_payrollpaymentsystemv2;

import com.opencsv.CSVWriter;
import group3_motorph_payrollpaymentsystemV2.Filehandling;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class LeaveApplicationUser1 extends javax.swing.JFrame {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String FILE_NAME = "leave_applications.csv";
    private static final int MAX_LEAVE_DAYS = 30;
    private static HashMap<String, Integer> leaveBalanceMap = new HashMap<>();
    private List<LeaveDetails> employees = new ArrayList<>();

    private String userEmployeeNumber;
    private String userLastName;
    private String userFirstName;
    private static String[] userInformation;

    /**
     * Creates new form LeaveApplicationUser
     */
    public LeaveApplicationUser1(String[] userInformation) throws FileNotFoundException, IOException {

        this.userEmployeeNumber = userInformation[0];
        this.userLastName = userInformation[1];
        this.userFirstName = userInformation[2];

        initComponents();

        showDetails();
        csvRun();

        // Initially set the text field to not editable
        jTextFieldOthers.setEditable(false);

    }

    public String getEmployeeNumber() {
        return userEmployeeNumber;
    }

    public String getLastName() {
        return userLastName;
    }

    public String getFirstName() {
        return userFirstName;
    }

    public String getuserFullName() {
        String userFullName = getLastName() + "," + getFirstName();
        return userFullName;
    }

    public void csvRun() throws FileNotFoundException, IOException {
        List<String[]> records = Filehandling.readCSV(FILE_NAME);
        List<LeaveDetails> employees_ = parseRecords(records);
        informationTable(employees_);
    }

    public List<LeaveDetails> parseRecords(List<String[]> records) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Sort list of String[] based on the first element (date)
        Collections.sort(records, (o1, o2) -> {
            Date date1;
            Date date2;
            try {
                date1 = dateFormat.parse(o1[5]);
                date2 = dateFormat.parse(o2[5]);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
            return date1.compareTo(date2);
        });

        for (String[] record : records) {
            String entryNum = record[0];
            String employeeNumber = record[1];
            String lastName = record[2];
            String firstName = record[3];
            String leaveStatus = record[4];
            String dateFiled = record[5];
            String leaveReason = record[6];
            String startDate = record[7];
            String endDate = record[8];
            String leaveDay = record[9];

            LeaveDetails leaveDetails = new LeaveDetails(entryNum, employeeNumber, lastName, firstName, leaveStatus, dateFiled,
                    leaveReason, startDate, endDate, leaveDay);
            employees.add(leaveDetails);
        }

        return employees;
    }

    private void informationTable(List<LeaveDetails> employees) {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        tableModel.setRowCount(0); // Clear existing rows
        for (LeaveDetails employee : employees) {
            if (employee.getEmployeeNumber().equals(getEmployeeNumber())) {
                tableModel.addRow(new Object[]{
                    employee.getLeaveStatus(),
                    employee.getSubmittedDate(),
                    employee.getLeaveReason(),
                    employee.getStartDate(),
                    employee.getEndDate(),
                    employee.getLeaveDay(),});
            }
        }
    }

    public void showDetails() {
        jTextFieldEmployeeNum.setText(getEmployeeNumber());
        jTextFieldEmployeeName.setText(getuserFullName());
    }

    public void openUserProfile() {

        try {
            setVisible(false);
            EmployeeProfileUser profileUser = new EmployeeProfileUser(getEmployeeNumber());
            profileUser.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(LeaveApplicationUser1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String[] sendInformation() {
        String[] userInformation = new String[3];;

        userInformation[0] = getEmployeeNumber();
        userInformation[1] = getLastName();
        userInformation[2] = getFirstName();

        return userInformation;

    }

    public int calculateLeaveDays(String startDateStr, String endDateStr) {
        try {
            long startDate = DATE_FORMAT.parse(startDateStr).getTime();
            long endDate = DATE_FORMAT.parse(endDateStr).getTime();
            return (int) ((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1;
        } catch (ParseException e) {
            return -1;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String getCurrentDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return currentDateTime.format(formatter);
    }

    public void clearTable() {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        tableModel.setRowCount(0); // This will clear all the rows
    }

    public boolean compareDates(String DateString1, String DateString2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate Date1 = LocalDate.parse(DateString1, formatter);
        LocalDate Date2 = LocalDate.parse(DateString2, formatter);

        return Date1.isAfter(Date2);
    }

    public boolean isPending() {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();
        int selectedRowIndex = jTableLeaveApplications.getSelectedRow();

        String status = tableModel.getValueAt(selectedRowIndex, 0).toString();

        if (status.equals("Pending")) {
            return true;
        }

        return false;

    }

    public void showEntrytoTextField() {
        try {
            DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

            int selectedRow = jTableLeaveApplications.getSelectedRow();
            String leaveReason = (String) tableModel.getValueAt(selectedRow, 2);

            String[] parts = leaveReason.split("_");

            String reason = parts.length > 0 ? parts[0] : "";    // true statement : false statement
            String other = parts.length > 1 ? parts[1] : "";

            Object startDateObj = tableModel.getValueAt(selectedRow, 3);
            Object endDateObj = tableModel.getValueAt(selectedRow, 4);

            Date startDate = convertToDate(startDateObj);
            Date endDate = convertToDate(endDateObj);

            jComboBoxLeaveReason.setEditable(true);
            jComboBoxLeaveReason.setSelectedItem(reason);
            jTextFieldOthers.setText(other);
            jDateChooserStartDate.setDate(startDate);
            jDateChooserEndDate.setDate(endDate);

        } catch (ParseException ex) {
            Logger.getLogger(LeaveApplicationUser1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Date convertToDate(Object dateObj) throws ParseException {
        if (dateObj instanceof Date) {
            return (Date) dateObj;
        } else if (dateObj instanceof String) {
            // Adjust the date format to match the format of your date strings
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse((String) dateObj);
        } else {
            throw new ParseException("Unparseable date: " + dateObj, 0);
        }
    }

    public List<String> createTableEntryIDList() {
        DefaultTableModel model = (DefaultTableModel) jTableLeaveApplications.getModel();
        List<String> tableEntryList = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String id = model.getValueAt(i, 0).toString();
            tableEntryList.add(id);
        }
        return tableEntryList;
    }

    public boolean isNewEntry(List<String> tableIdList) {
        String newEmployeeId = jTextFieldEmployeeNum.getText().trim();

        for (int i = 0; i < tableIdList.size(); i++) {
            if (tableIdList.get(i).equals(newEmployeeId)) {
                JOptionPane.showMessageDialog(this, "ID number already exist");
                return false;
            }
        }

        return true;
    }

    public String[] generateEntryID() {
        DefaultTableModel model = (DefaultTableModel) jTableLeaveApplications.getModel();
        int rowCount = model.getRowCount();
        String employeeNumber = getEmployeeNumber();
        String[] entryID = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            String index = employeeNumber + "-" + Integer.toString((i + 1));
            entryID[i] = index;
        }
        return entryID;
    }

    public List<LeaveDetails> filteredRows() {
        // Filter out rows matching the employee number
        String employeeNumber = getEmployeeNumber();
        List<LeaveDetails> filteredRows = new ArrayList<>();

        for (LeaveDetails employee : employees) {
            if (!employeeNumber.equals(employee.getEmployeeNumber())) {
                filteredRows.add(employee);
            }
        }
        return filteredRows;
    }

    public void updateCSV() {
        DefaultTableModel model = (DefaultTableModel) jTableLeaveApplications.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        String[] entryID = generateEntryID();
        String employeeNumber = getEmployeeNumber();
        String lastName = getLastName();
        String firstName = getFirstName();

        String csvFile = "leave_applications_2.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {

            String[] columnNames = {"entryID", "employeeNumber", "lastName", "firstName", "leaveStatus",
                "submittedDate", "leaveReason", "startDate", "endDate", "leaveDay"};
            writer.writeNext(columnNames);

            for (LeaveDetails dataList : filteredRows()) {
                String[] rowData = {
                    dataList.getentryNum(),
                    dataList.getEmployeeNumber(),
                    dataList.getLastName(),
                    dataList.getFirstName(),
                    dataList.getLeaveStatus(),
                    dataList.getSubmittedDate(),
                    dataList.getLeaveReason(),
                    dataList.getStartDate(),
                    dataList.getEndDate(),
                    dataList.getLeaveDay()
                };
                writer.writeNext(rowData);
            }

            for (int i = 0; i < rowCount; i++) {
                String[] rowData_ = new String[4 + columnCount];
                rowData_[0] = entryID[i];
                rowData_[1] = employeeNumber;
                rowData_[2] = lastName;
                rowData_[3] = firstName;
                for (int j = 0; j < columnCount; j++) {
                    int index = j + 4;
                    rowData_[index] = model.getValueAt(i, j).toString();
                }
                writer.writeNext(rowData_);
            }

            JOptionPane.showMessageDialog(null, "Record updated successfully");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to update your record.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButtonVacationLeave1 = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButtonLeaveApp = new javax.swing.JButton();
        jButtonProfile = new javax.swing.JButton();
        jButtonPayroll = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldEmployeeNum = new javax.swing.JTextField();
        jTextFieldEmployeeName = new javax.swing.JTextField();
        jButtonSaveLeave = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldOthers = new javax.swing.JTextField();
        jComboBoxLeaveReason = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jDateChooserStartDate = new com.toedter.calendar.JDateChooser();
        jDateChooserEndDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jButtonUpdate = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonSubmit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLeaveApplications = new javax.swing.JTable();

        jRadioButtonVacationLeave1.setText("Vacation Leave");
        jRadioButtonVacationLeave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVacationLeave1ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(102, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonLeaveApp.setText("Leave Application");
        jButtonLeaveApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeaveAppActionPerformed(evt);
            }
        });

        jButtonProfile.setText("Profile");
        jButtonProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProfileActionPerformed(evt);
            }
        });

        jButtonPayroll.setText("Payroll");
        jButtonPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPayrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonPayroll)
                    .addComponent(jButtonProfile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jButtonLeaveApp)
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jButtonProfile)
                .addGap(44, 44, 44)
                .addComponent(jButtonPayroll)
                .addGap(33, 33, 33)
                .addComponent(jButtonLeaveApp)
                .addContainerGap(261, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 460));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("End Date :");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, -1, -1));

        jLabel1.setText("Employee Name:");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel2.setText("Employee ID:");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, -1, -1));

        jLabel3.setText("Start Date :");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, -1, -1));

        jTextFieldEmployeeNum.setEditable(false);
        jTextFieldEmployeeNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNumActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEmployeeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 395, -1));

        jTextFieldEmployeeName.setEditable(false);
        jTextFieldEmployeeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNameActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 390, -1));

        jButtonSaveLeave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSaveLeave.setText("Save Leave Application");
        jButtonSaveLeave.setToolTipText("");
        jButtonSaveLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveLeaveActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonSaveLeave, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 400, -1));

        jLabel6.setText("Reason for Leave :");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        jTextFieldOthers.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jTextFieldOthers.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldOthers.setText("( if Others, specify reason )");
        jTextFieldOthers.setToolTipText("");
        jTextFieldOthers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldOthersActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldOthers, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 140, 150, -1));

        jComboBoxLeaveReason.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "","Sick Leave", "Vacation Leave", "Maternity Leave", "Paternity Leave", "Others" }));
        jComboBoxLeaveReason.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLeaveReasonActionPerformed(evt);
            }
        });
        jPanel3.add(jComboBoxLeaveReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, 130, -1));

        jLabel7.setText("Specify Reason :");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 136, -1, 30));

        jDateChooserStartDate.setDateFormatString("yyyy-MM-dd");
        jDateChooserStartDate.setMinSelectableDate(new java.util.Date(-62135794702000L));
        jPanel3.add(jDateChooserStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 210, -1));

        jDateChooserEndDate.setDateFormatString("yyyy-MM-dd");
        jPanel3.add(jDateChooserEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 210, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 570, 220));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Leave Application");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 580, 39));

        jButtonUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonUpdate.setText("Update");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 410, -1, -1));

        jButtonCancel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 410, -1, -1));

        jButtonSubmit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSubmit.setText("Submit");
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 410, -1, -1));

        jTableLeaveApplications.setAutoCreateRowSorter(true);
        jTableLeaveApplications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Status", "Date Filed", "Reason for Leave", "Start Date", "End Date", "Leave Days"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLeaveApplications.setColumnSelectionAllowed(true);
        jTableLeaveApplications.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableLeaveApplications.getTableHeader().setReorderingAllowed(false);
        jTableLeaveApplications.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLeaveApplicationsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLeaveApplications);
        jTableLeaveApplications.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 260, 550, 130));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmployeeNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNumActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jTextFieldEmployeeNumActionPerformed

    private void jTextFieldEmployeeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmployeeNameActionPerformed

    private void jButtonPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPayrollActionPerformed
        // TODO add your handling code here:

        try {
            // TODO add your handling code here:
            String[] employeeInformation = sendInformation();
            PayrollUser payrollUser = new PayrollUser(employeeInformation);
            setVisible(false);
            payrollUser.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(EmployeeProfileUser.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButtonPayrollActionPerformed

    private void jButtonProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProfileActionPerformed
        openUserProfile();
    }//GEN-LAST:event_jButtonProfileActionPerformed

    private void jButtonLeaveAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeaveAppActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLeaveAppActionPerformed

    private void jButtonSaveLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveLeaveActionPerformed
        // TODO add your handling code here:
        String id = jTextFieldEmployeeNum.getText();
        String leaveReason = jComboBoxLeaveReason.getSelectedItem().toString();
        String dateSubmitted = getCurrentDate();
        String startDate = formatDate(jDateChooserStartDate.getDate());
        String endDate = formatDate(jDateChooserEndDate.getDate());

        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        if (leaveReason.equals("Others")) {
            leaveReason = leaveReason + "_" + jTextFieldOthers.getText();
        }

        // Validate input fields
        if (startDate.isEmpty() || endDate.isEmpty() || leaveReason.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int leaveDays = calculateLeaveDays(startDate, endDate);
        if (leaveDays < 0) {
            JOptionPane.showMessageDialog(null, "End date must be after start date", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int remainingLeave = leaveBalanceMap.getOrDefault(id, MAX_LEAVE_DAYS) - leaveDays;
        if (remainingLeave < 0) {
            JOptionPane.showMessageDialog(null, "Insufficient leave balance", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        leaveBalanceMap.put(id, remainingLeave);

        tableModel.addRow(new Object[]{"Pending", dateSubmitted, leaveReason, startDate, endDate,
            leaveDays});

        // Clear input fields
        jDateChooserStartDate.setDate(
                null);
        jDateChooserEndDate.setDate(
                null);
        jComboBoxLeaveReason.setSelectedIndex(
                0);

    }//GEN-LAST:event_jButtonSaveLeaveActionPerformed

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        int selectedRow = jTableLeaveApplications.getSelectedRow();

        if (selectedRow != -1) {
            // Get the leave status of the selected row
            String leaveStatus = (String) jTableLeaveApplications.getValueAt(selectedRow, 0);

            if (leaveStatus.equals("Pending")) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to proceed with updating the entry?",
                        "Update Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // Check the user's response
                if (response == JOptionPane.YES_OPTION) {
                    showEntrytoTextField();
                    tableModel.removeRow(selectedRow);

                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Only entries with status 'Pending' can be updated.",
                        "update Entry Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please select an entry to update.",
                    "No Row Selected",
                    JOptionPane.WARNING_MESSAGE);
        }


    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed

        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        int selectedRow = jTableLeaveApplications.getSelectedRow();
        if (selectedRow != -1) {
            // Get the leave status of the selected row
            String leaveStatus = (String) jTableLeaveApplications.getValueAt(selectedRow, 0);

            if (leaveStatus.equals("Pending")) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to proceed with cancelling the entry?",
                        "Cancel Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // Check the user's response
                if (response == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Only entries with status 'Pending' can be cancel.",
                        "Cancel Entry Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please select an entry to cancel.",
                    "No Row Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        // TODO add your handling code here:
        updateCSV();
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    private void jRadioButtonVacationLeave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVacationLeave1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonVacationLeave1ActionPerformed

    private void jTextFieldOthersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldOthersActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTextFieldOthersActionPerformed

    private void jComboBoxLeaveReasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLeaveReasonActionPerformed
        // TODO add your handling code here:

        if ("Others".equals(jComboBoxLeaveReason.getSelectedItem().toString())) {
            jTextFieldOthers.setEditable(true);
        } else {
            jTextFieldOthers.setEditable(false);
            jTextFieldOthers.setText(""); // Clear the text field when not editable
        }
    }//GEN-LAST:event_jComboBoxLeaveReasonActionPerformed

    private void jTableLeaveApplicationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLeaveApplicationsMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jTableLeaveApplicationsMouseClicked

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
            java.util.logging.Logger.getLogger(LeaveApplicationUser1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationUser1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationUser1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationUser1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LeaveApplicationUser1(userInformation).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(LeaveApplicationUser1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonLeaveApp;
    private javax.swing.JButton jButtonPayroll;
    private javax.swing.JButton jButtonProfile;
    private javax.swing.JButton jButtonSaveLeave;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JComboBox<String> jComboBoxLeaveReason;
    private com.toedter.calendar.JDateChooser jDateChooserEndDate;
    private com.toedter.calendar.JDateChooser jDateChooserStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButtonVacationLeave1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTableLeaveApplications;
    private javax.swing.JTextField jTextFieldEmployeeName;
    private javax.swing.JTextField jTextFieldEmployeeNum;
    private javax.swing.JTextField jTextFieldOthers;
    // End of variables declaration//GEN-END:variables
}
