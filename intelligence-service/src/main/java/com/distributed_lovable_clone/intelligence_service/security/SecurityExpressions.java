package com.distributed_lovable_clone.intelligence_service.security;

import com.distributed_lovable_clone.common_lib.type.ProjectPermission;
import com.distributed_lovable_clone.intelligence_service.client.WorkspaceClient;
import com.openai.errors.UnauthorizedException;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

@Slf4j
@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class SecurityExpressions {
    WorkspaceClient workspaceClient;

    private boolean hasPermission(Long projectId , ProjectPermission projectPermission){
        try {
            return workspaceClient.checkProjectPermission(projectId, projectPermission);
        }
        catch (FeignException.FeignClientException.Unauthorized e){
            log.warn("User does not have permission to perform this operation");
            throw new CredentialsExpiredException("User does not have permission to perform this operation");
        }
        catch (FeignException e){
            log.warn(e.getMessage());
            return false;
        }
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
