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
    }

    public class EmployeeLogin {

        // Attributes
        private String username;
        private String password;
        private String employeeNumber;
        private String userEmployeeNumber;

        // Constructor
        public EmployeeLogin(String employeeNumber, String username, String password) {
            this.employeeNumber = employeeNumber;
            this.username = username;
            this.password = password;
        }

        // contructor for saving user employee number
        public EmployeeLogin(String userEmployeeNumber) {
            this.userEmployeeNumber = userEmployeeNumber;
        }

        // Getters 
        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmployeeNumber() {
            return employeeNumber;
        }

        public String getUserEmployeeNumber() {
            return userEmployeeNumber;
        }

        // Setters
        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setEmployeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
        }

        public void setUserEmployeeNumber(String userEmployeeNumber) {
            this.userEmployeeNumber = userEmployeeNumber;
        }
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
        
       EmployeeLogin userEmployee = new EmployeeLogin(determineEmployeeNumber);

        for (int i = 0; i < employeeDetails.size(); i++) {
            EmployeeLogin employee_ = employeeDetails.get(i);
            if (employee_.getUsername().toLowerCase().equals(inputUsername) && employee_.getPassword().equals(inputPassword)) {
                determineEmployeeNumber = employeeDetails.get(i).getEmployeeNumber();
                userEmployee.setUserEmployeeNumber(determineEmployeeNumber);
                return determineEmployeeNumber;
            }
        }
        return null;
    }

    public void sendInformation() throws IOException {

        String employeeNum = matchEmployeeNumber();
        EmployeeProfileUser profileUser = new EmployeeProfileUser(employeeNum);
//        profileUser.jTextFieldEmployeeNum.setText(employeeNum);// testing
        profileUser.setVisible(true);
//        profileUser.showDetails();
    }

    private void openDashboard() throws IOException {
        String employeeNum = matchEmployeeNumber();
        if (employeeNum.equals("0")) {
            setVisible(false);
            new EmployeeProfile().setVisible(true);

        } else {
            setVisible(false);
            sendInformation(); //open EmployeeProfileUser and show the employee information

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
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUsernameActionPerformed(evt);
            }
        });
        jPanel2.add(jTextFieldUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 160, -1));

        jLabel1.setText("username");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 24, -1, -1));

        jLabel2.setText("password");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 53, -1, -1));
        jPanel2.add(jPasswordFieldInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 160, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 14, 958, -1));

        jCheckBoxShowPassword.setBackground(new java.awt.Color(204, 255, 204));
        jCheckBoxShowPassword.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jCheckBoxShowPassword.setText("Show Password");
        jCheckBoxShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowPasswordActionPerformed(evt);
            }
        });
        jPanel2.add(jCheckBoxShowPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, -1, -1));

        jButtonExit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonExit.setText("EXIT");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jButtonLogIn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonLogIn.setText("LOG IN");
        jButtonLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogInActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonLogIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 270, 160));

        jLabel5.setBackground(new java.awt.Color(14, 49, 113));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Motor PH - CP2 | Group 3: Login Page");
        jLabel5.setOpaque(true);
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 40));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogInActionPerformed
        try {
            // TODO add your handling code here:
            logIn();
            matchEmployeeNumber();
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordFieldInput;
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables
}
