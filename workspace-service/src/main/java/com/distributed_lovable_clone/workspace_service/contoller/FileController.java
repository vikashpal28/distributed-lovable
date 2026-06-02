package com.distributed_lovable_clone.workspace_service.contoller;


import com.distributed_lovable_clone.common_lib.dto.FileTreeDto;
import com.distributed_lovable_clone.workspace_service.dto.project.FileContentResponse;
import com.distributed_lovable_clone.workspace_service.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/files")
public class FileController {
    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<FileTreeDto> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }
    @GetMapping("/{*path}") //src/hooks/get-userId-hook.js
    public ResponseEntity<String> getFile(@PathVariable Long projectId , @PathVariable String path){
        return ResponseEntity.ok(projectFileService.getFileContent(projectId , path));

    }

}
