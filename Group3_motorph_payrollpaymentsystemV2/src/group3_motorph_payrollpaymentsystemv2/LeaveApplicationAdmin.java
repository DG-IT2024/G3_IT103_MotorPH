package group3_motorph_payrollpaymentsystemv2;

import com.opencsv.CSVWriter;
import group3_motorph_payrollpaymentsystemV2.Filehandling;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class LeaveApplicationAdmin extends javax.swing.JFrame {

    private static final String FILE_NAME = "leave_applications.csv";
    public List<LeaveDetails> employees = new ArrayList<>();

    /**
     * Creates new form LeaveApplicationUser
     */
    public LeaveApplicationAdmin() throws FileNotFoundException, IOException {
        initComponents();
        csvRun();

    }

    private void csvRun() throws FileNotFoundException, IOException {
        List<String[]> records = Filehandling.readCSV(FILE_NAME);
        List<LeaveDetails> employees = parseRecords(records);
        informationTable(employees);
    }

    public List<LeaveDetails> parseRecords(List<String[]> records) {

        for (String[] record : records) {
            String entryNum = record[0];
            String employeeNumber = record[1];
            String lastName = record[2];
            String firstName = record[3];
            String leaveStatus = record[4];
            String submittedDate = record[5];
            String leaveReason = record[6];
            String startDate = record[7];
            String endDate = record[8];
            String leaveDay = record[9];

            LeaveDetails employee = new LeaveDetails(
                    entryNum, employeeNumber, lastName, firstName, leaveStatus, submittedDate,
                    leaveReason, startDate, endDate, leaveDay);
            employees.add(employee);
        }
        return employees;
    }

    private void informationTable(List<LeaveDetails> employees) {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (LeaveDetails employee : employees) {
            tableModel.addRow(new Object[]{
                employee.getentryNum(),
                employee.getEmployeeNumber(),
                employee.getLastName(),
                employee.getFirstName(),
                employee.getLeaveStatus(),
                employee.getSubmittedDate(),
                employee.getLeaveReason(),
                employee.getStartDate(),
                employee.getEndDate(),
                employee.getLeaveDay(),});
        }

    }

    public void showPendingLeaves() {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (LeaveDetails employee : employees) {
            if ("Pending".equalsIgnoreCase(employee.getLeaveStatus())) {
                tableModel.addRow(new Object[]{
                    employee.getentryNum(),
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getLeaveStatus(),
                    employee.getSubmittedDate(),
                    employee.getLeaveReason(),
                    employee.getStartDate(),
                    employee.getEndDate(),
                    employee.getLeaveDay()});
            }
        }
    }

    public void previousLeaves() {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (LeaveDetails employee : employees) {
            if (!"Pending".equalsIgnoreCase(employee.getLeaveStatus())) {
                tableModel.addRow(new Object[]{
                    employee.getentryNum(),
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getLeaveStatus(),
                    employee.getSubmittedDate(),
                    employee.getLeaveReason(),
                    employee.getStartDate(),
                    employee.getEndDate(),
                    employee.getLeaveDay()});
            }
        }
    }

    public int[] computeLeaveTotals() {

        String employeeNumber = jTextFieldEmployeeNum.getText();

        int sickLeaveTotal = 0;
        int vacationLeaveTotal = 0;
        int paternityLeaveTotal = 0;
        int maternityLeaveTotal = 0;
        int otherLeaveTotal = 0;

        for (LeaveDetails employee : employees) {
            if (employee.getEmployeeNumber().equals(employeeNumber) && employee.getLeaveStatus().equals("Approved")) {
                switch (employee.getLeaveReason()) {
                    case "Sick Leave":
                        sickLeaveTotal += Integer.parseInt(employee.getLeaveDay());
                        break;
                    case "Vacation Leave":
                        vacationLeaveTotal += Integer.parseInt(employee.getLeaveDay());
                        break;
                    case "Paternity Leave":
                        paternityLeaveTotal += Integer.parseInt(employee.getLeaveDay());
                        break;
                    case "Maternity Leave":
                        maternityLeaveTotal += Integer.parseInt(employee.getLeaveDay());
                        break;
                    default:
                        otherLeaveTotal += Integer.parseInt(employee.getLeaveDay());
                        break;
                }
            }
        }

        return new int[]{sickLeaveTotal, vacationLeaveTotal, paternityLeaveTotal, maternityLeaveTotal, otherLeaveTotal};
    }

    public void showSummary() {
        int[] summary = computeLeaveTotals();
        String sickLeaveTotal = Integer.toString(summary[0]);
        String vacationLeaveTotal = Integer.toString(summary[1]);
        String paternityLeaveTotal = Integer.toString(summary[2]);
        String maternityLeaveTotal = Integer.toString(summary[3]);
        String otherLeaveTotal = Integer.toString(summary[4]);

        jTextFieldSickLeave.setText(sickLeaveTotal);
        jTextFieldVacationLeave.setText(vacationLeaveTotal);
        jTextFieldPaternity.setText(paternityLeaveTotal);
        jTextFieldMaternity.setText(maternityLeaveTotal);
        jTextFieldOthers.setText(otherLeaveTotal);
    }

    public void updateLeaveStatus(String Status) {
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();
        int selectedRow = jTableLeaveApplications.getSelectedRow();

        if (selectedRow != -1) {
            String selectedRowStatus = tableModel.getValueAt(selectedRow, 4).toString(); // Leave Status is in the 5th column (index 4)

            if (selectedRowStatus.equals("Pending")) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to proceed with changing the leave status",
                        "Update Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    tableModel.setValueAt(Status, selectedRow, 4); // Update the status in the table model

                    // Optionally update the employees list if you have it available
                    String employeeNumber = tableModel.getValueAt(selectedRow, 1).toString(); // Employee Number is in the 2nd column (index 1)
                    for (LeaveDetails employee : employees) {
                        if (employee.getEmployeeNumber().equals(employeeNumber) && "Pending".equalsIgnoreCase(employee.getLeaveStatus())) {
                            employee.setLeaveStatus(Status);
                            break; // Assuming each employee number is unique, we can break after the first match
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Only entries with 'Pending' leave status can be updated.",
                        "update Entry Error",
                        JOptionPane.ERROR_MESSAGE);

            }

        }
    }
/*
    public void updateCSV() {
        String csvFile = "leave_applications_2.csv";
        DefaultTableModel tableModel = (DefaultTableModel) jTableLeaveApplications.getModel();

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            // Write header
            String[] header = {"Entry ID", "Employee Number", "Last Name", "First Name", "Leave Status",
                "Date Filed", "Reason for Leave", "Start Date", "End Date", "Leave Days"};
            writer.writeNext(header);

            // Write rows
            int rowCount = tableModel.getRowCount();
            int columnCount = tableModel.getColumnCount();
            for (int i = 0; i < rowCount; i++) {
                String[] row = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    row[j] = tableModel.getValueAt(i, j).toString();
                }
                writer.writeNext(row);
            }

            JOptionPane.showMessageDialog(null, "Record updated successfully");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to update your record.");
        }
    }

  */
     public void updateCSV() {
    String csvFile = "leave_applications_2.csv";
    
    try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
        // Write header
        String[] header = {"Entry ID", "Employee Number", "Last Name", "First Name", "Leave Status", 
                           "Date Filed", "Reason for Leave", "Start Date", "End Date", "Leave Days"};
        writer.writeNext(header);
        
        // Write rows
        for (LeaveDetails employee : employees) {
            String[] row = {
                employee.getentryNum(),
                employee.getEmployeeNumber(),
                employee.getLastName(),
                employee.getFirstName(),
                employee.getLeaveStatus(),
                employee.getSubmittedDate(),
                employee.getLeaveReason(),
                employee.getStartDate(),
                employee.getEndDate(),
                employee.getLeaveDay()
            };
            writer.writeNext(row);
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
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldEmployeeNum = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldEmployeeName = new javax.swing.JTextField();
        jTextFieldDateFiled = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldLeaveReason = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldStartDate = new javax.swing.JTextField();
        jTextFieldEndDate = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldEmployeeNum6 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldLeaveDays = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldSickLeave = new javax.swing.JTextField();
        jTextFieldVacationLeave = new javax.swing.JTextField();
        jTextFieldPaternity = new javax.swing.JTextField();
        jTextFieldMaternity = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldOthers = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jButtonReject = new javax.swing.JButton();
        jButtonApprove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLeaveApplications = new javax.swing.JTable();
        jRadioButtonPending = new javax.swing.JRadioButton();
        jButtonSave = new javax.swing.JButton();

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

        jLabel16.setText("days");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Employee ID:");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        jTextFieldEmployeeNum.setEditable(false);
        jTextFieldEmployeeNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNumActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEmployeeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 180, -1));

        jLabel8.setText("Employee Name");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 90, -1));

        jTextFieldEmployeeName.setEditable(false);
        jTextFieldEmployeeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNameActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 180, -1));

        jTextFieldDateFiled.setEditable(false);
        jTextFieldDateFiled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDateFiledActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldDateFiled, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 180, -1));

        jLabel3.setText("Date Filed");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jTextFieldLeaveReason.setEditable(false);
        jTextFieldLeaveReason.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLeaveReasonActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldLeaveReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 180, -1));

        jLabel10.setText("Reason for Leave");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 100, -1));

        jTextFieldStartDate.setEditable(false);
        jTextFieldStartDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldStartDateActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 180, -1));

        jTextFieldEndDate.setEditable(false);
        jTextFieldEndDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEndDateActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 180, -1));

        jLabel11.setText("End Date");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel12.setText("Start Date");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        jTextFieldEmployeeNum6.setEditable(false);
        jTextFieldEmployeeNum6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEmployeeNum6ActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldEmployeeNum6, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 180, -1));

        jLabel13.setText("End Date");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel14.setText("Leave Days");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, -1, -1));

        jTextFieldLeaveDays.setEditable(false);
        jTextFieldLeaveDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLeaveDaysActionPerformed(evt);
            }
        });
        jPanel3.add(jTextFieldLeaveDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, 180, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 340, 230));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Leave Application");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 39));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel1.setText("Employee Leave Summary");

        jLabel4.setText("Sick Leave");

        jLabel6.setText("Vacation Leave");

        jLabel7.setText("Maternity  Leave");

        jLabel9.setText("Paternity Leave");

        jTextFieldSickLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSickLeaveActionPerformed(evt);
            }
        });

        jTextFieldMaternity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMaternityActionPerformed(evt);
            }
        });

        jLabel15.setText("days");

        jLabel17.setText("days");

        jLabel18.setText("days");

        jLabel19.setText("days");

        jLabel20.setText("Others");

        jLabel21.setText("days");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldOthers, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextFieldSickLeave, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextFieldVacationLeave, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextFieldPaternity, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextFieldMaternity, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19)))))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldSickLeave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldVacationLeave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldPaternity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldMaternity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextFieldOthers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 280, 220));

        jButtonReject.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonReject.setText("REJECT");
        jButtonReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRejectActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReject, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 440, -1, -1));

        jButtonApprove.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonApprove.setText("APPROVE");
        jButtonApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApproveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonApprove, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, -1, -1));

        jTableLeaveApplications.setAutoCreateRowSorter(true);
        jTableLeaveApplications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Entry ID", "Employee Number", "Last Name", "First Name", "Leave Status", "Date Filed", "Reason for Leave", "Start Date", "End Date", "Leave Days"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLeaveApplications.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableLeaveApplications.getTableHeader().setReorderingAllowed(false);
        jTableLeaveApplications.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLeaveApplicationsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLeaveApplications);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 670, 140));

        jRadioButtonPending.setText("Show Pending Only");
        jRadioButtonPending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPendingActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonPending, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, -1, -1));

        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("SAVE");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 440, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonVacationLeave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVacationLeave1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonVacationLeave1ActionPerformed

    private void jTextFieldEmployeeNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNumActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTextFieldEmployeeNumActionPerformed

    private void jTextFieldEmployeeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmployeeNameActionPerformed

    private void jTextFieldDateFiledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDateFiledActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDateFiledActionPerformed

    private void jTextFieldLeaveReasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLeaveReasonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLeaveReasonActionPerformed

    private void jTextFieldStartDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldStartDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldStartDateActionPerformed

    private void jTextFieldEndDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEndDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEndDateActionPerformed

    private void jTextFieldEmployeeNum6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmployeeNum6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmployeeNum6ActionPerformed

    private void jTextFieldLeaveDaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLeaveDaysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLeaveDaysActionPerformed

    private void jButtonRejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRejectActionPerformed
        // TODO add your handling code here:
        updateLeaveStatus("Rejected");
        showSummary();
    }//GEN-LAST:event_jButtonRejectActionPerformed

    private void jButtonApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApproveActionPerformed
        // TODO add your handling code here:
        updateLeaveStatus("Approved");
        showSummary();
    }//GEN-LAST:event_jButtonApproveActionPerformed

    private void jTextFieldSickLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSickLeaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSickLeaveActionPerformed

    private void jTextFieldMaternityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMaternityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMaternityActionPerformed

    private void jTableLeaveApplicationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLeaveApplicationsMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jTableLeaveApplications.getModel();
        int selectedRowIndex = jTableLeaveApplications.getSelectedRow();
        String lastName = model.getValueAt(selectedRowIndex, 2).toString();
        String firstName = model.getValueAt(selectedRowIndex, 3).toString();
        String fullName = lastName + ", " + firstName;

        jTextFieldEmployeeNum.setText(model.getValueAt(selectedRowIndex, 1).toString());
        jTextFieldEmployeeName.setText(fullName);
        jTextFieldDateFiled.setText(model.getValueAt(selectedRowIndex, 5).toString());
        jTextFieldLeaveReason.setText(model.getValueAt(selectedRowIndex, 6).toString());
        jTextFieldStartDate.setText(model.getValueAt(selectedRowIndex, 7).toString());
        jTextFieldEndDate.setText(model.getValueAt(selectedRowIndex, 8).toString());
        jTextFieldLeaveDays.setText(model.getValueAt(selectedRowIndex, 9).toString());
        showSummary();

    }//GEN-LAST:event_jTableLeaveApplicationsMouseClicked

    private void jRadioButtonPendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPendingActionPerformed
        // TODO add your handling code here:

        if (jRadioButtonPending.isSelected()) {
            showPendingLeaves();
        } else {
            informationTable(employees);
        }
        

    }//GEN-LAST:event_jRadioButtonPendingActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        // TODO add your handling code here:
        
        updateCSV();
    }//GEN-LAST:event_jButtonSaveActionPerformed

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
            java.util.logging.Logger.getLogger(LeaveApplicationAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveApplicationAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LeaveApplicationAdmin().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(LeaveApplicationAdmin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonApprove;
    private javax.swing.JButton jButtonReject;
    private javax.swing.JButton jButtonSave;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButtonPending;
    private javax.swing.JRadioButton jRadioButtonVacationLeave1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTableLeaveApplications;
    private javax.swing.JTextField jTextFieldDateFiled;
    private javax.swing.JTextField jTextFieldEmployeeName;
    private javax.swing.JTextField jTextFieldEmployeeNum;
    private javax.swing.JTextField jTextFieldEmployeeNum6;
    private javax.swing.JTextField jTextFieldEndDate;
    private javax.swing.JTextField jTextFieldLeaveDays;
    private javax.swing.JTextField jTextFieldLeaveReason;
    private javax.swing.JTextField jTextFieldMaternity;
    private javax.swing.JTextField jTextFieldOthers;
    private javax.swing.JTextField jTextFieldPaternity;
    private javax.swing.JTextField jTextFieldSickLeave;
    private javax.swing.JTextField jTextFieldStartDate;
    private javax.swing.JTextField jTextFieldVacationLeave;
    // End of variables declaration//GEN-END:variables
}
