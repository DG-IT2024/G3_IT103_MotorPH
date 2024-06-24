package group3_motorph_payrollpaymentsystemv2;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import group3_motorph_payrollpaymentsystemV2.Filehandling;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author danilo
 */
public class LoginManager extends javax.swing.JFrame {

    public List<EmployeeLogin> employeeDetails = new ArrayList<>();
    private static final int MAX_ATTEMPTS = 3;
    private Map<String, Integer> userAttempts = new HashMap<>(); // https://www.callicoder.com/java-hashmap/
    private static final String CSV_FILE = "login_attempts.csv";

    /**
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    public LoginManager() throws IOException, FileNotFoundException {
        initComponents();

        String csvFile = "employeeCredentials.csv";
        List<String[]> records = Filehandling.readCSV(csvFile);
        parseUserCredentials(records);
        loadAttemptsFromCSV();
        setIconImage();
    }

    public List< EmployeeLogin> parseUserCredentials(List<String[]> records) {
        for (String[] record : records) {
            String employeeNumber = record[0];
            String username = record[1];
            String password = record[2];

            EmployeeLogin employeeLogin = new EmployeeLogin(employeeNumber, username, password);
            employeeDetails.add(employeeLogin);
        }
        return employeeDetails;
    }

    // Load login attempts from CSV
    private void loadAttemptsFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE))) { //no need to add finally statement since try-with-resources was used
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String username = nextLine[0];
                int attempts = Integer.parseInt(nextLine[1]);
                userAttempts.put(username, attempts);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    // Save all login attempts to CSV
    private void saveAllAttemptsToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE))) {
            for (Map.Entry<String, Integer> entry : userAttempts.entrySet()) {
                String[] record = {entry.getKey(), String.valueOf(entry.getValue())};
                writer.writeNext(record);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public boolean authenticate() throws IOException {

        String inputUsername = jTextFieldUsername.getText().toLowerCase(); // accept any case ;
        String inputPassword = jPasswordFieldInput.getText();

        for (int i = 0; i < employeeDetails.size(); i++) {
            EmployeeLogin employee_ = employeeDetails.get(i);
            if (employee_.getUsername().toLowerCase().equals(inputUsername) && employee_.getPassword().equals(inputPassword)) {
//              JOptionPane.showMessageDialog(null, "Match found for user: " + inputUsername, "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
//        JOptionPane.showMessageDialog(null, "No match found for the given credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Method to check login
    public boolean logIn() throws IOException {
        boolean isAuthenticated = authenticate();
        String inputUsername = jTextFieldUsername.getText();

        if (!isAuthenticated) {
            int attempts = userAttempts.getOrDefault(inputUsername, 0) + 1;
            userAttempts.put(inputUsername, attempts); //data to be written in the csv file

            // Save attempt to CSV
            saveAllAttemptsToCSV();

            if (attempts >= MAX_ATTEMPTS) {

                JOptionPane.showMessageDialog(null, "User " + inputUsername + " is blocked due to too many failed attempts.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {

                JOptionPane.showMessageDialog(null, "Attempt " + attempts + " of " + MAX_ATTEMPTS + ".", "Login Failed", JOptionPane.ERROR_MESSAGE);

                return false;
            }
        }

        // Reset attempt count after successful login
        userAttempts.put(inputUsername, 0);
        // Save attempt to CSV
        saveAllAttemptsToCSV();
        return true;
    }

    public String matchEmployeeNumber() throws IOException {
        String inputUsername = jTextFieldUsername.getText().toLowerCase(); // accept any case 
        String inputPassword = jPasswordFieldInput.getText();
        String determineEmployeeNumber = "0";

        for (int i = 0; i < employeeDetails.size(); i++) {
            EmployeeLogin employee_ = employeeDetails.get(i);
            if (employee_.getUsername().toLowerCase().equals(inputUsername) && employee_.getPassword().equals(inputPassword)) {
                determineEmployeeNumber = employeeDetails.get(i).getEmployeeNumber();
                return determineEmployeeNumber;
            }
        }
        return null;
    }

    private void openDashboard() throws IOException {
        String employeeNum = matchEmployeeNumber();
        if (employeeNum.equals("0")) {
            setVisible(false);
            new EmployeeProfile().setVisible(true); // open Admin Dashboard
        } else {
            setVisible(false);
            EmployeeProfileUser profileUser = new EmployeeProfileUser(matchEmployeeNumber());
            profileUser.setVisible(true); //open Employee Dashboard
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

        jPanel2 = new javax.swing.JPanel();
        jTextFieldUsername = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPasswordFieldInput = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jCheckBoxShowPassword = new javax.swing.JCheckBox();
        jButtonExit = new javax.swing.JButton();
        jButtonLogIn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Group 3 | CP2 | A1102");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(192, 168, 137, 220));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 223, 87), new java.awt.Color(254, 44, 57)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldUsername.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jTextFieldUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUsernameActionPerformed(evt);
            }
        });
        jPanel2.add(jTextFieldUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 160, 22));

        jLabel1.setBackground(new java.awt.Color(192, 168, 137));
        jLabel1.setText("Username");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 24, -1, -1));

        jLabel2.setBackground(new java.awt.Color(192, 168, 137));
        jLabel2.setText("Password");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 53, -1, -1));

        jPasswordFieldInput.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.gray));
        jPanel2.add(jPasswordFieldInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 160, 22));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 14, 958, -1));

        jCheckBoxShowPassword.setBackground(new java.awt.Color(148, 129, 93));
        jCheckBoxShowPassword.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jCheckBoxShowPassword.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxShowPassword.setText("Show Password");
        jCheckBoxShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowPasswordActionPerformed(evt);
            }
        });
        jPanel2.add(jCheckBoxShowPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jButtonExit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonExit.setText("EXIT");
        jButtonExit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 223, 87), new java.awt.Color(255, 223, 87), new java.awt.Color(254, 44, 57), new java.awt.Color(254, 44, 57)));
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 60, 25));

        jButtonLogIn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonLogIn.setText("LOG IN");
        jButtonLogIn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 223, 87), new java.awt.Color(255, 223, 87), new java.awt.Color(254, 44, 57), new java.awt.Color(254, 44, 57)));
        jButtonLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogInActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonLogIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 60, 25));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 270, 160));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/group3_motorph_payrollpaymentsystemv2/Login Page.jpg"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 390, 690));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogInActionPerformed
        try {
            // TODO add your handling code here:
            logIn();
            openDashboard();

        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonLogInActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jCheckBoxShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxShowPasswordActionPerformed
        // TODO add your handling code here:

        if (jCheckBoxShowPassword.isSelected()) {
            jPasswordFieldInput.setEchoChar((char) 0);
        } else {
            jPasswordFieldInput.setEchoChar('*');
        }
    }//GEN-LAST:event_jCheckBoxShowPasswordActionPerformed

    private void jTextFieldUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUsernameActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
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
            java.util.logging.Logger.getLogger(LoginManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginManager().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonLogIn;
    private javax.swing.JCheckBox jCheckBoxShowPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordFieldInput;
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables

    private void setIconImage() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.jpg")));
    }
}
