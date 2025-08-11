package com.ezlevup.smilechat.controller;

import com.ezlevup.smilechat.model.ChatMessage;
import com.ezlevup.smilechat.service.ChatService;
import com.ezlevup.smilechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Set;

/**
 * 개인 메시지(1:1 상담) 전용 컨트롤러
 */
@Controller
public class PrivateMessageController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PrivateMessageController(ChatService chatService, UserService userService,
                                    SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 1:1 개인 메시지 전송
     */
    @MessageMapping("/private.sendMessage")
    public void sendPrivateMessage(@Payload PrivateMessage privateMessage, Principal principal) {
        String senderUsername = principal.getName();

        // 수신자가 온라인인지 확인
        if (!userService.isUserOnline(privateMessage.receiverUsername())) {
            // 오프라인 메시지 처리
            sendOfflineNotification(senderUsername);
            return;
        }

        // 메시지 생성
        ChatMessage chatMessage = new ChatMessage(
                privateMessage.content(),
                senderUsername,
                ChatMessage.MessageType.CHAT,
                null,
                privateMessage.receiverUsername()
        );

        // 메시지 처리 (가상 스레드 사용)
        Thread.ofVirtual().start(() -> {
            ChatMessage processedMessage = chatService.processPrivateMessage(chatMessage);

            // 수신자에게 전송
            messagingTemplate.convertAndSendToUser(
                    privateMessage.receiverUsername(),
                    "/queue/private",
                    processedMessage.withCurrentTimestamp()
            );

            // 발신자에게도 확인 메시지 전송
            messagingTemplate.convertAndSendToUser(
                    senderUsername,
                    "/queue/private",
                    processedMessage.withCurrentTimestamp()
            );

            // 로깅
            chatService.logPrivateMessage(processedMessage);
        });
    }

    /**
     * 상담 요청 (환자 → 직원)
     */
    @MessageMapping("/consultation.request")
    public void requestConsultation(@Payload ConsultationRequest request, Principal principal) {
        String patientUsername = principal.getName();

        // 온라인 직원들에게 상담 요청 알림
        Set<String> onlineStaff = userService.getOnlineStaff();

        ConsultationNotification notification = new ConsultationNotification(
                patientUsername,
                request.message(),
                System.currentTimeMillis()
        );

        // 가상 스레드로 모든 직원에게 알림 전송
        Thread.ofVirtual().start(() -> {
            onlineStaff.forEach(staffUsername -> {
                messagingTemplate.convertAndSendToUser(
                        staffUsername,
                        "/queue/consultation-request",
                        notification
                );
            });
        });
    }

    /**
     * 상담 수락 (직원 → 환자)
     */
    @MessageMapping("/consultation.accept")
    public void acceptConsultation(@Payload ConsultationAccept accept, Principal principal) {
        String staffUsername = principal.getName();

        // 환자에게 상담 수락 알림
        ConsultationResponse response = new ConsultationResponse(
                staffUsername,
                "상담을 시작합니다. 궁금한 점을 말씀해 주세요.",
                "ACCEPTED"
        );

        messagingTemplate.convertAndSendToUser(
                accept.patientUsername(),
                "/queue/consultation-response",
                response
        );
    }

    /**
     * 온라인 직원 목록 조회 (REST API)
     */
    @GetMapping("/api/online-staff")
    @ResponseBody
    public Set<String> getOnlineStaff() {
        return userService.getOnlineStaff();
    }

    /**
     * 오프라인 사용자에게 메시지 전송 시 알림
     */
    private void sendOfflineNotification(String senderUsername) {
        ChatMessage offlineMessage = ChatMessage.systemMessage(
                "상대방이 현재 오프라인 상태입니다. 메시지가 전달되지 않았습니다.",
                senderUsername
        );

        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/private",
                offlineMessage
        );
    }

    /**
     * 개인 메시지 데이터 클래스들
     */
    public record PrivateMessage(String content, String receiverUsername) {}
    public record ConsultationRequest(String message) {}
    public record ConsultationAccept(String patientUsername) {}
    public record ConsultationNotification(String patientUsername, String message, long timestamp) {}
    public record ConsultationResponse(String staffUsername, String message, String status) {}
}
