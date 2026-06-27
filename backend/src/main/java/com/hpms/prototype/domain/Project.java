package com.hpms.prototype.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "project")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "department_id")
  private Long departmentId;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "target_date")
  private LocalDate targetDate;

  @Column(name = "open_date")
  private LocalDate openDate;

  @Column(name = "open_date_text", length = 50)
  private String openDateText;

  @Column(name = "progress_rate", nullable = false)
  private BigDecimal progressRate;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected Project() {}

  public Long getId() {
    return id;
  }
}
