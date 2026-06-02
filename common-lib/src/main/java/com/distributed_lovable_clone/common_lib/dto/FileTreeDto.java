package com.distributed_lovable_clone.common_lib.dto;

import java.util.List;

public record FileTreeDto(
        List<FileNode>  files
) {
}
