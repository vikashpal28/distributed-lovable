package com.distributed_lovable_clone.workspace_service.service;



import com.distributed_lovable_clone.workspace_service.dto.member.InvitedMemberRequest;
import com.distributed_lovable_clone.workspace_service.dto.member.MemberResponse;
import com.distributed_lovable_clone.workspace_service.dto.member.UpdateMemberRole;

import java.util.List;


public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InvitedMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRole request);

    void removeMember(Long projectId, Long memberId);
}
