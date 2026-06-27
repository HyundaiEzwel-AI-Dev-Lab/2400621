package com.hpms.prototype.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DdayCalculatorTest {

  private final DdayCalculator calculator = new DdayCalculator();

  @Test
  void calculatesRemainingDaysAsNegativeDday() {
    assertThat(calculator.calculate(LocalDate.of(2026, 3, 30), LocalDate.of(2026, 3, 16)))
        .isEqualTo(-14);
  }

  @Test
  void calculatesTodayAsZeroDday() {
    assertThat(calculator.calculate(LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 20)))
        .isZero();
    assertThat(calculator.format(0)).isEqualTo("D-Day");
  }

  @Test
  void calculatesOverdueDaysAsPositiveDday() {
    assertThat(calculator.calculate(LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 22)))
        .isEqualTo(2);
    assertThat(calculator.isDelayed(LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 22)))
        .isTrue();
  }

  @Test
  void detectsDueThisWeekFromMondayToSunday() {
    LocalDate baseDate = LocalDate.of(2026, 3, 18);

    assertThat(calculator.isDueThisWeek(LocalDate.of(2026, 3, 16), baseDate)).isTrue();
    assertThat(calculator.isDueThisWeek(LocalDate.of(2026, 3, 22), baseDate)).isTrue();
    assertThat(calculator.isDueThisWeek(LocalDate.of(2026, 3, 23), baseDate)).isFalse();
  }
}
