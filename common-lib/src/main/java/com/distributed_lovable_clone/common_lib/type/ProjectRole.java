package com.distributed_lovable_clone.common_lib.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    VIEWER(Set.of(ProjectPermission.VIEW , ProjectPermission.VIEW_MEMBERS)),
    EDITOR(Set.of(ProjectPermission.EDIT , ProjectPermission.DELETE , ProjectPermission.VIEW ,ProjectPermission.VIEW_MEMBERS)),
    OWNER(Set.of(ProjectPermission.EDIT , ProjectPermission.VIEW , ProjectPermission.DELETE , ProjectPermission.MANAGE_MEMBERS , ProjectPermission.VIEW_MEMBERS));

    private final Set<ProjectPermission> permissions;
}
