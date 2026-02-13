package userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import userservice.dto.SignUpRequestDto;
import userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  @PostMapping
  public ResponseEntity<String> signUp(
      @RequestBody SignUpRequestDto signUpRequestDto
  ) {
    userService.signUp(signUpRequestDto);
    return ResponseEntity.ok("회원가입 성공");
  }
}
