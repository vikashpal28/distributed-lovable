package com.distributed_lovable_clone.workspace_service.mapper;


import com.distributed_lovable_clone.common_lib.type.ProjectRole;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectResponse;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectSummaryResponse;
import com.distributed_lovable_clone.workspace_service.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);
    ProjectSummaryResponse toProjectSummaryResponse(Project project , ProjectRole role);
    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);
}
