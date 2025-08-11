package com.ezlevup.smilechat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 데이터 모델
 * Java 21의 record 기능을 활용하여 불변 객체로 설계
 */
public record ChatMessage(
        @NotBlank(message = "메시지 내용은 필수입니다")
        String content,

        @NotBlank(message = "발신자 정보는 필수입니다")
        String sender,

        @NotNull(message = "메시지 타입은 필수입니다")
        MessageType type,

        LocalDateTime timestamp,

        String receiverRoom  // 수신자 방 정보 (개인 상담용)
) {

    /**
     * 메시지 타입 열거형
     * - CHAT: 일반 채팅 메시지
     * - JOIN: 사용자 입장
     * - LEAVE: 사용자 퇴장
     * - SYSTEM: 시스템 메시지
     */
    public enum MessageType {
        CHAT,    // 일반 채팅
        JOIN,    // 입장
        LEAVE,   // 퇴장
        SYSTEM   // 시스템 메시지
    }

    /**
     * 현재 시간이 설정된 새로운 ChatMessage 인스턴스 생성
     */
    public ChatMessage withCurrentTimestamp() {
        return new ChatMessage(content, sender, type, LocalDateTime.now(), receiverRoom);
    }

    /**
     * 시스템 메시지 생성 팩토리 메서드
     */
    public static ChatMessage systemMessage(String content, String room) {
        return new ChatMessage(content, "System", MessageType.SYSTEM,
                LocalDateTime.now(), room);
    }
}
