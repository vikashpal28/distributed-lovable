package com.distributed_lovable_clone.workspace_service.security;


import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.common_lib.type.ProjectPermission;
import com.distributed_lovable_clone.workspace_service.repository.ProjectMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class SecurityExpressions {
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    public boolean hasPermission(Long projectId , ProjectPermission projectPermission){
        Long userId = authUtil.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId , userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
    }

    public boolean canViewProject(Long projectId){
     return hasPermission(projectId , ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId){
     return hasPermission(projectId , ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId){
        return hasPermission(projectId , ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId){
        return hasPermission(projectId , ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId){
        return hasPermission(projectId , ProjectPermission.MANAGE_MEMBERS);
    }

}
