package com.distributed_lovable_clone.workspace_service.entity;


import com.distributed_lovable_clone.common_lib.type.ProjectRole;
import jakarta.persistence.*;
import lombok.*;


import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "project_members")
@Builder
public class ProjectMember {
    @EmbeddedId // composite key
    ProjectMemberId id;
    @ManyToOne
    @MapsId("projectId")
    Project project;


    @Enumerated(value = EnumType.STRING)
    @Column(name = "project_role" , nullable = false)
    ProjectRole projectRole;
    Instant invitedAt;
    Instant acceptedAt;
}
