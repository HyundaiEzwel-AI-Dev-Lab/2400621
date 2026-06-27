package com.hpms.prototype.repository.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TaskProjection {
  Long getTaskId();

  String getTaskName();

  String getTaskStatus();

  Long getProjectId();

  String getProjectName();

  BigDecimal getProgressRate();

  LocalDate getStartDate();

  LocalDate getDueDate();

  Integer getSortOrder();
}
