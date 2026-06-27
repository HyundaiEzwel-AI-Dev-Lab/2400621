package com.hpms.prototype.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_account")
public class UserAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "department_id")
  private Long departmentId;

  @Column(nullable = false, unique = true, length = 100)
  private String username;

  @Column(name = "display_name", nullable = false, length = 100)
  private String displayName;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected UserAccount() {}

  public Long getId() {
    return id;
  }

  public boolean isActive() {
    return active;
  }
}
