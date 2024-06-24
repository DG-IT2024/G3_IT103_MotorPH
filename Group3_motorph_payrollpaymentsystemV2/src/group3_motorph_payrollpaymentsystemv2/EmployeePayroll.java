/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group3_motorph_payrollpaymentsystemv2;

/**
 *
 * @author danilo
 */
public class EmployeePayroll {
    private String employeeNo;
    private String lastName;
    private String firstName;
    private String workedHours;
    private String basicSalary;
    private String hourlyRate;
    private String grossIncome;
    private String sssDeduction;
    private String philHealthDeduction;
    private String pagibigDeduction;
    private String withholdingTax;
    private String coveredMonth;
    private String coveredYear;
    private String benefits;
    private String totalDeductions;
    private String takeHomePay;

    // Constructor
    public EmployeePayroll(String employeeNo, String lastName, String firstName, String workedHours, String basicSalary, String hourlyRate, String grossIncome, String sssDeduction, String philHealthDeduction, String pagibigDeduction, String withholdingTax, String coveredMonth, String coveredYear, String benefits, String totalDeductions, String takeHomePay) {
        this.employeeNo = employeeNo;
        this.lastName = lastName;
        this.firstName = firstName;
        this.workedHours = workedHours;
        this.basicSalary = basicSalary;
        this.hourlyRate = hourlyRate;
        this.grossIncome = grossIncome;
        this.sssDeduction = sssDeduction;
        this.philHealthDeduction = philHealthDeduction;
        this.pagibigDeduction = pagibigDeduction;
        this.withholdingTax = withholdingTax;
        this.coveredMonth = coveredMonth;
        this.coveredYear = coveredYear;
        this.benefits = benefits;
        this.totalDeductions = totalDeductions;
        this.takeHomePay = takeHomePay;
    }

    // Getters and Setters
    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
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

    public String getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(String workedHours) {
        this.workedHours = workedHours;
    }

    public String getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(String basicSalary) {
        this.basicSalary = basicSalary;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(String grossIncome) {
        this.grossIncome = grossIncome;
    }

    public String getSssDeduction() {
        return sssDeduction;
    }

    public void setSssDeduction(String sssDeduction) {
        this.sssDeduction = sssDeduction;
    }

    public String getPhilHealthDeduction() {
        return philHealthDeduction;
    }

    public void setPhilHealthDeduction(String philHealthDeduction) {
        this.philHealthDeduction = philHealthDeduction;
    }

    public String getPagibigDeduction() {
        return pagibigDeduction;
    }

    public void setPagibigDeduction(String pagibigDeduction) {
        this.pagibigDeduction = pagibigDeduction;
    }

    public String getWithholdingTax() {
        return withholdingTax;
    }

    public void setWithholdingTax(String withholdingTax) {
        this.withholdingTax = withholdingTax;
    }

    public String getCoveredMonth() {
        return coveredMonth;
    }

    public void setCoveredMonth(String coveredMonth) {
        this.coveredMonth = coveredMonth;
    }

    public String getCoveredYear() {
        return coveredYear;
    }

    public void setCoveredYear(String coveredYear) {
        this.coveredYear = coveredYear;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(String totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public String getTakeHomePay() {
        return takeHomePay;
    }

    public void setTakeHomePay(String takeHomePay) {
        this.takeHomePay = takeHomePay;
    }
}
