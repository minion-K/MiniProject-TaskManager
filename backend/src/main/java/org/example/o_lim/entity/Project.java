package org.example.o_lim.entity;

import lombok.*;
import org.example.o_lim.entity.base.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table( name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project extends BaseTimeEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "admin_id",
    nullable = false, 
    foreignKey = @ForeignKey(name = "fk_projects_admin_id")
    )
  private User admin;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(nullable = false, length = 255)
  private String description;

  private Project(User admin, String title, String description) {
    this.admin = admin;
    this.title = title;
    this.description = description;
  }

  public static Project create(User admin, String title, String description) {
    return new Project(admin, title, description);
  }

  public void update(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
