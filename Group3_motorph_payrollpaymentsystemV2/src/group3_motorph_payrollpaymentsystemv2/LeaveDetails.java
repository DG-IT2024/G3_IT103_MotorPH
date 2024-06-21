package group3_motorph_payrollpaymentsystemv2;

public class LeaveDetails {

    // Attributes  
    private String entryNum;
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String submittedDate;
    private String startDate;
    private String endDate;
    private String leaveDay;
    private String leaveReason;
    private String leaveStatus;

    // Constructor
    public LeaveDetails(        
      String entryNum, String employeeNumber, String lastName, String firstName, String leaveStatus, String submittedDate,
            String leaveReason, String startDate, String endDate, String leaveDay) {
        this.entryNum = entryNum ;
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.leaveStatus = leaveStatus;
        this.submittedDate = submittedDate;
        this.leaveReason = leaveReason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveDay = leaveDay;

    }

    // Getter and Setter methods
     public String getentryNum() {
        return entryNum;
    }

    public void setEntryNum(String entryNum) {
        this.entryNum = entryNum;
    }
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLeaveDay() {
        return leaveDay;
    }

    public void setLeaveDay(String leaveDay) {
        this.leaveDay = leaveDay;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }
}
