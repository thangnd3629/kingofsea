//package com.supergroup.admin.api;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.supergroup.admin.dto.SendMessageRequest;
//import com.supergroup.auth.domain.service.LoginSessionService;
//import com.supergroup.core.constant.ErrorCode;
//import com.supergroup.core.exception.KOSException;
//import com.supergroup.kos.notification.domain.command.SendMessageCommand;
//import com.supergroup.kos.notification.domain.service.MessageService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/v1/admin/message")
//@RequiredArgsConstructor
//public class MessageRestController {
//
//    private final MessageService      messageService;
//    private final LoginSessionService loginSessionService;
//
//    @PostMapping("")
//    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request) {
//        var session = loginSessionService.findFirstByUser_IdOrderByUpdatedAtDesc(request.getUserId())
//                                         .orElseThrow(() -> KOSException.of(ErrorCode.USER_IS_LOGGED_OUT));
//        messageService.sendMessage(new SendMessageCommand(
//                request.getMessage(),
//                session.getId(),
//                null
//        ));
//        return ResponseEntity.noContent().build();
//    }
//
//}
