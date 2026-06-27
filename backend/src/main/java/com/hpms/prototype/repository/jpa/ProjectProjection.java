package com.hpms.prototype.repository.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProjectProjection {
  Long getProjectId();

  String getProjectName();

  String getStatus();

  LocalDate getTargetDate();

  LocalDate getOpenDate();

  String getOpenDateText();

  BigDecimal getProgressRate();

  String getDepartmentName();
}
