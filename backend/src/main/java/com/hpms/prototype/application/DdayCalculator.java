package com.hpms.prototype.application;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class DdayCalculator {

  public int calculate(LocalDate dueDate, LocalDate baseDate) {
    return Math.toIntExact(ChronoUnit.DAYS.between(baseDate, dueDate) * -1);
  }

  public boolean isDelayed(LocalDate dueDate, LocalDate baseDate) {
    return dueDate != null && dueDate.isBefore(baseDate);
  }

  public boolean isDueThisWeek(LocalDate dueDate, LocalDate baseDate) {
    if (dueDate == null) {
      return false;
    }
    LocalDate monday = baseDate.minusDays(baseDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    LocalDate sunday = monday.plusDays(6);
    return !dueDate.isBefore(monday) && !dueDate.isAfter(sunday);
  }

  public String format(int dDay) {
    if (dDay == 0) {
      return "D-Day";
    }
    return dDay > 0 ? "D+" + dDay : "D" + dDay;
  }
}
