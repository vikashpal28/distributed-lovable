package com.distributed_lovable_clone.workspace_service.service.impl;


import com.distributed_lovable_clone.common_lib.dto.UserDto;
import com.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.workspace_service.client.ApplicationClient;
import com.distributed_lovable_clone.workspace_service.dto.member.InvitedMemberRequest;
import com.distributed_lovable_clone.workspace_service.dto.member.MemberResponse;
import com.distributed_lovable_clone.workspace_service.dto.member.UpdateMemberRole;
import com.distributed_lovable_clone.workspace_service.entity.Project;
import com.distributed_lovable_clone.workspace_service.entity.ProjectMember;
import com.distributed_lovable_clone.workspace_service.entity.ProjectMemberId;
import com.distributed_lovable_clone.workspace_service.mapper.ProjectMemberMapper;
import com.distributed_lovable_clone.workspace_service.repository.ProjectMemberRepository;
import com.distributed_lovable_clone.workspace_service.repository.ProjectRepository;
import com.distributed_lovable_clone.workspace_service.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {
    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    AuthUtil authUtil;
    ApplicationClient applicationClient;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        return   projectMemberRepository.findByIdProjectId(projectId)
                        .stream()
                        .map(projectMemberMapper::toProjectMemberResponseFromMember)
                        .toList();

//        return memberResponsesList;
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InvitedMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId , userId);

//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("not Allowed");
//        }
        UserDto invitee = applicationClient.getUserByEmail(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("not found" , request.username()));


        if(invitee.id().equals(userId)){
            throw new RuntimeException("you are try yourSelf");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId , invitee.id());
        if(projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("cannot invited once again");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRole request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId , userId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("not allowed");
//        }

        ProjectMemberId memberId1 = new ProjectMemberId(projectId , memberId);
        ProjectMember member = projectMemberRepository.findById(memberId1).orElseThrow();
        member.setProjectRole(request.role());
        projectMemberRepository.save(member);
        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId , userId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw  new RuntimeException("Not allowed");
//        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId , memberId);
        if(!projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("cannot invite once again");
        }

        projectMemberRepository.deleteById(projectMemberId);

    }

    public Project getAccessibleProjectById(Long id, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }



}
