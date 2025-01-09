package com.supergroup.kos.api.notification;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.notification.CountNotificationUnseen;
import com.supergroup.kos.dto.notification.NotificationDTO;
import com.supergroup.kos.mapper.NotificationUserMapper;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;
import com.supergroup.kos.notification.domain.service.UserNotificationService;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/notifications")
@RequiredArgsConstructor
public class NotificationUserRestController {
    private final NotificationUserMapper  notificationUserMapper;
    private final UserNotificationService userNotificationService;
    private final UserService             userService;

    @GetMapping("")
    @Transactional
    public ResponseEntity<PageResponse<NotificationDTO>> getNotification(@RequestParam(name = "statuses", required = false) List<String> statuses,
                                                                         @RequestParam(name = "types", required = false) List<String> types,
                                                                         Pageable pageable) {
        User user = userService.findUserByEmail(AuthUtil.getCurrentUserDetails().getUsername());
        pageable = PageRequest.of(0,Integer.MAX_VALUE);
        var rs = userNotificationService.getNotifications(user.getId(), statuses, types, pageable);
        var data = notificationUserMapper.toDTOs(rs.getContent());
        return ResponseEntity.ok(new PageResponse<NotificationDTO>().setTotal(rs.getTotalElements()).setData(data));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateStatusNotification(@PathVariable Long id, @RequestParam(name = "status") String status) {
        User user = userService.findUserByEmail(AuthUtil.getCurrentUserDetails().getUsername());
        userNotificationService.updateStatusNotification(id, user.getId(), NotificationStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    @Transactional
    public ResponseEntity<?> updateStatusAllNotification(@RequestParam(name = "status") String status) {
        User user = userService.findUserByEmail(AuthUtil.getCurrentUserDetails().getUsername());
        userNotificationService.updateStatusAllNotification(user.getId(), NotificationStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count-unseen")
    @Transactional
    public ResponseEntity<CountNotificationUnseen> getCountNotificationUnseen() {
        var userId = userService.findUserByEmail(AuthUtil.getCurrentUserDetails().getUsername()).getId();
        var response = new CountNotificationUnseen().setCount(userNotificationService.countNotificationUnseen(userId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
