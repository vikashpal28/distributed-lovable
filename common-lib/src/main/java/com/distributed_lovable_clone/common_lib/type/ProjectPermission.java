package com.distributed_lovable_clone.common_lib.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {
    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),

    MANAGE_MEMBERS("manage_members:manage"),
    VIEW_MEMBERS("project_members:view");


    private final String value;

}
