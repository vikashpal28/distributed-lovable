package com.distributed_lovable_clone.common_lib.dto;

public record FileNode(
        String path
) {

    @Override
    public String toString() { return path;}
}
