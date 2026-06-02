package com.distributed_lovable_clone.workspace_service.service.impl;


import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.error.BadRequestException;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.common_lib.type.ProjectPermission;
import com.distributed_lovable_clone.common_lib.type.ProjectRole;
import com.distributed_lovable_clone.workspace_service.client.ApplicationClient;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectRequest;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectResponse;
import com.distributed_lovable_clone.workspace_service.dto.project.ProjectSummaryResponse;
import com.distributed_lovable_clone.workspace_service.entity.Project;
import com.distributed_lovable_clone.workspace_service.entity.ProjectMember;
import com.distributed_lovable_clone.workspace_service.entity.ProjectMemberId;
import com.distributed_lovable_clone.workspace_service.mapper.ProjectMapper;
import com.distributed_lovable_clone.workspace_service.repository.ProjectMemberRepository;
import com.distributed_lovable_clone.workspace_service.repository.ProjectRepository;
import com.distributed_lovable_clone.workspace_service.security.SecurityExpressions;
import com.distributed_lovable_clone.workspace_service.service.ProjectService;
import com.distributed_lovable_clone.workspace_service.service.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    ApplicationClient applicationClient;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SecurityExpressions securityExpressions;

    ProjectTemplateService projectTemplateService;
    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .collect(Collectors.toList());
        Long userId = authUtil.getCurrentUserId();
        var projectWithRoles = projectRepository.findAllAccessibleByUser(userId);
        return projectWithRoles.stream().
                map(p -> projectMapper.toProjectSummaryResponse(p.getProject() , p.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        var projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(projectId , userId).orElseThrow(
                () -> new BadRequestException("project not found")
        );
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject() , projectWithRole.getRole());
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) throws Exception{

            if (!canCreateNewProject()){
                throw new BadRequestException("userId cannot create a new Project with current Plan, Upgrade Plan now.");
            }

        Long userId = authUtil.getCurrentUserId();
        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
                project = projectRepository.save(project);
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), userId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
      Project project = projectRepository.findAccessibleProjectById(projectId , userId).orElseThrow();

      project.setName(request.name());
      project = projectRepository.save(project);

      return projectMapper.toProjectResponse(project);

    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
     Project project = projectRepository.findAccessibleProjectById(projectId , userId).orElseThrow();
     project.setDeletedAt(Instant.now());
     projectRepository.save(project);
    }

    @Override
    public boolean hasPermission(Long projectId, ProjectPermission permission) {
        return securityExpressions.hasPermission(projectId , permission);
    }

    private boolean canCreateNewProject(){
        Long userId = authUtil.getCurrentUserId();
        if(userId == null){
            return false;
        }
        PlanDto planDto = applicationClient.getCurrentSubscribedPlanByUser();

        int maxAllowed = planDto.maxProducts();
        int ownedCount = projectMemberRepository.countProjectOwnedByUser(userId);
        return  ownedCount < maxAllowed;
    }
}
