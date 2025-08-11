package com.ezlevup.smilechat.service;

import com.ezlevup.smilechat.model.ChatMessage;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;


/**
 * 채팅 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());

    /**
     * 메시지 처리 (욕설 필터링, 스팸 방지 등)
     */
    public ChatMessage processMessage(ChatMessage message) {
        // 메시지 내용 검증 및 필터링
        String filteredContent = filterContent(message.content());

        return new ChatMessage(
                filteredContent,
                message.sender(),
                message.type(),
                message.timestamp(),
                message.receiverRoom()
        );
    }

    /**
     * 개인 메시지 처리
     */
    public ChatMessage processPrivateMessage(ChatMessage message) {
        // 개인 메시지 특별 처리 로직
        logger.info("Private message from " + message.sender() + " to " + message.receiverRoom());
        return processMessage(message);
    }

    /**
     * 메시지 로깅
     */
    public void logMessage(ChatMessage message) {
        logger.info(String.format("Message logged: [%s] %s: %s",
                message.timestamp(), message.sender(), message.content()));
    }

    /**
     * 부적절한 내용 필터링
     */
    private String filterContent(String content) {
        // 간단한 욕설 필터링 예시
        // 실제 환경에서는 더 정교한 필터링 시스템 구현 필요
        return content.replaceAll("(?i)(욕설|스팸)", "***");
    }
}
