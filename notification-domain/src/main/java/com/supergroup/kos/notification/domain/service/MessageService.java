//package com.supergroup.kos.notification.domain.service;
//
//import javax.transaction.Transactional;
//
//import org.springframework.stereotype.Service;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.supergroup.core.constant.ErrorCode;
//import com.supergroup.core.exception.KOSException;
//import com.supergroup.kos.notification.domain.command.SendMessageCommand;
//import com.supergroup.notification.service.FcmTo;
//import com.supergroup.notification.service.MessageSender;
//import com.supergroup.notification.service.FcmMessage;
//
//import lombok.RequiredArgsConstructor;
//
///**
// * Messenger (FCM)
// * Send message to Firebase client
// */
//@Service
//@RequiredArgsConstructor
//public class MessageService {
//
//    private final MessageSender       messageSender;
//
//
//    /**
//     * Send message to client
//     */
//    @Transactional
//    public KosMessage sendMessage(SendMessageCommand command) {
//        try {
//            var session = loginSessionService.findById(command.getLoginSessionId())
//                                             .orElseThrow(() -> KOSException.of(ErrorCode.SERVER_ERROR));
//            var mess = new FcmMessage(new FcmTo(session.getFcmToken()), command.getDataMessage(), command.getNotificationMessage());
//            messageSender.send(mess);
//            return command.getDataMessage();
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//            // ignore
//            return null;
//        }
//    }
//}
