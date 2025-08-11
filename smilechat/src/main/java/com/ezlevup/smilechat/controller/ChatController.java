package com.ezlevup.smilechat.controller;

import com.ezlevup.smilechat.model.ChatMessage;
import com.ezlevup.smilechat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * WebSocket 메시지 처리를 담당하는 컨트롤러
 * 실시간 채팅 메시지 송수신을 관리
 */
@Controller
@Validated
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 공개 채팅방에 메시지 전송
     * 모든 연결된 클라이언트에게 브로드캐스트
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 메시지 검증 및 처리
        ChatMessage processedMessage = chatService.processMessage(chatMessage);

        // 로깅 (Java 21 가상 스레드 활용)
        Thread.ofVirtual().start(() -> {
            chatService.logMessage(processedMessage);
        });

        return processedMessage.withCurrentTimestamp();
    }

    /**
     * 사용자 입장 처리
     * 세션에 사용자 정보를 저장하고 입장 알림 전송
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션에 사용자명 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.sender());

        // 입장 메시지 생성
        ChatMessage joinMessage = new ChatMessage(
                chatMessage.sender() + "님이 상담실에 입장하셨습니다.",
                "System",
                ChatMessage.MessageType.JOIN,
                null,
                chatMessage.receiverRoom()
        );

        return joinMessage.withCurrentTimestamp();
    }

    /**
     * 개인 상담 메시지 전송
     * 특정 직원과 고객 간의 1:1 채팅
     */
    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // 개인 메시지 처리
        ChatMessage processedMessage = chatService.processPrivateMessage(chatMessage);

        // 수신자에게 개인 메시지 전송
        messagingTemplate.convertAndSendToUser(
                chatMessage.receiverRoom(),
                "/queue/private",
                processedMessage.withCurrentTimestamp()
        );
    }
}
