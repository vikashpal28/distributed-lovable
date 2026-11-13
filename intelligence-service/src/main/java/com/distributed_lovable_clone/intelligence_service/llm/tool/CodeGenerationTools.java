package com.distributed_lovable_clone.intelligence_service.llm.tool;


import com.distributed_lovable_clone.intelligence_service.client.WorkspaceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CodeGenerationTools {


    private final Long projectId;
    private final WorkspaceClient workspaceClient;
    private final SecurityContext securityContext;

    @Tool(name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
            List<String> paths
    ){
        // <-- SET SECURITY CONTEXT ON THIS THREAD
        SecurityContext previousContext = SecurityContextHolder.getContext();
        try {
            SecurityContextHolder.setContext(securityContext);
            List<String> result = new ArrayList<>();

            for (String path : paths) {
                String classPath = path.startsWith("/") ? path.substring(1) : path;
                log.info("Request the file {}", classPath);
                String content = workspaceClient.getFileContent(projectId, classPath);
                result.add(String.format(
                        "--- START OF FILE: %s  ---\n%s\n--- END OF FILE ---",
                        classPath, content
                ));
            }
            return result;
        }finally {
            // <-- CLEAN UP THREAD CONTEXT
            SecurityContextHolder.setContext(previousContext);
        }
    }
}
