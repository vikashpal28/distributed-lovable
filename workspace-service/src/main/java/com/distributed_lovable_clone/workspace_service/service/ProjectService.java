package com.distributed_lovable_clone.workspace_service.service;



import com.distributed_lovable_clone.common_lib.type.ProjectPermission;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectRequest;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectResponse;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request) throws Exception;

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);

    boolean hasPermission(Long projectId, ProjectPermission permission);
}
