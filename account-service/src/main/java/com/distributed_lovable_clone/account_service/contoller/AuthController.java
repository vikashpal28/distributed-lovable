package com.distributed_lovable_clone.account_service.contoller;


import com.distributed_lovable_clone.account_service.dto.auth.AuthResponse;
import com.distributed_lovable_clone.account_service.dto.auth.LoginRequest;
import com.distributed_lovable_clone.account_service.dto.auth.SignUpRequest;
import com.distributed_lovable_clone.account_service.dto.auth.UserProfileResponse;
import com.distributed_lovable_clone.account_service.service.AuthService;
import com.distributed_lovable_clone.account_service.service.impl.UserService;
import com.distributed_lovable_clone.common_lib.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
   public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request){
       return ResponseEntity.ok(authService.signup(request));
   }

   @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
   }
   @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal JwtUserPrincipal user){
        if(user == null){
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.getProfile(user.userId()));
   }
}
