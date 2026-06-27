package com.hpms.prototype.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "wbs_task")
public class WbsTask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "project_id", nullable = false)
  private Long projectId;

  @Column(name = "requirement_id")
  private Long requirementId;

  @Column(name = "parent_task_id")
  private Long parentTaskId;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "progress_rate", nullable = false)
  private BigDecimal progressRate;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected WbsTask() {}

  public Long getId() {
    return id;
  }
}
