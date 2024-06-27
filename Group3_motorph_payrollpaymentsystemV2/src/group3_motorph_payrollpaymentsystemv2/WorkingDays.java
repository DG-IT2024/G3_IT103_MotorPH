package group3_motorph_payrollpaymentsystemv2;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danilo
 */
public class WorkingDays {
    

    public static void main(String[] args) {
        int month = 12; // June
        int year = 2024;

        int workingDays = getWorkingDays(month, year);
        System.out.println("Number of working days: " + workingDays);
    }

    public static int getWorkingDays(int month, int year) {
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

}
