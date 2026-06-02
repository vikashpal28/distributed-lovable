package com.distributed_lovable_clone.workspace_service.service.impl;


import com.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.distributed_lovable_clone.workspace_service.entity.Project;
import com.distributed_lovable_clone.workspace_service.entity.ProjectFile;
import com.distributed_lovable_clone.workspace_service.repository.ProjectFileRepository;
import com.distributed_lovable_clone.workspace_service.repository.ProjectRepository;
import com.distributed_lovable_clone.workspace_service.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;

    private static final String TEMPLATE_BUCKET = "project-starters";
    private static final String TARGET_BUCKET = "project";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";

    @Override
    public void initializeProjectFromTemplate(Long projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("project", projectId.toString()));

        log.info("start storing file on minio {}",projectId);

        try {
            log.info("try block {}", projectId);
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();
               log.info("inside for loop {}" ,projectId);
                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                log.info("complete the process of string at minio {}", projectId);
                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
            }

            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            log.error("not stored the file {}", projectId);
            throw new RuntimeException("Failed to initialize project from template", e);
        }



    }
}
