package com.distributed_lovable_clone.workspace_service.contoller;


import com.distributed_lovable_clone.workspace_service.dto.member.InvitedMemberRequest;
import com.distributed_lovable_clone.workspace_service.dto.member.MemberResponse;
import com.distributed_lovable_clone.workspace_service.dto.member.UpdateMemberRole;
import com.distributed_lovable_clone.workspace_service.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {
  private final ProjectMemberService projectMemberService;

  @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId){
      return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
  }
  @PostMapping
    public ResponseEntity<MemberResponse> invitedMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InvitedMemberRequest request
  ) {

      return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMember(projectId,request));
  }
  @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRole request
  ){

      return ResponseEntity.ok(projectMemberService.updateMemberRole( projectId , memberId ,request ));
  }

  @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId
  ){

      projectMemberService.removeMember( projectId , memberId );
     return   ResponseEntity.noContent().build();
  }
}
