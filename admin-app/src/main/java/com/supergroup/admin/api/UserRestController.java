package com.supergroup.admin.api;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.UpdateStatusCommand;
import com.supergroup.admin.domain.service.AdminManageUserService;
import com.supergroup.admin.dto.UserDTO;
import com.supergroup.admin.dto.InitUser;
import com.supergroup.admin.mapper.UserMapper;
import com.supergroup.auth.domain.constant.UserStatus;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class UserRestController {
    private final AdminManageUserService adminManageUserService;
    private final UserMapper             userMapper;
    private final KosProfileService      kosProfileService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(name = "status", required = false) String status, Pageable pageable) {
        return ResponseEntity.ok(userMapper.toDTOs(adminManageUserService.getAllUser(status, pageable).getContent()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateStatusUser(@PathVariable Long userId, @RequestParam(value = "status", required = true) String status) {
        adminManageUserService.updateStatusUser(new UpdateStatusCommand().setUserId(userId).setStatus(UserStatus.of(status)));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/init")
    public ResponseEntity<?> initUser(@RequestBody @Valid InitUser initUser) {
        KosProfile kosProfile = kosProfileService.createNewProfile(new UserCommand().setUserId(initUser.getUserId()));
        return ResponseEntity.ok(initUser.setNewKosProfileId(kosProfile.getId()));
    }
}
