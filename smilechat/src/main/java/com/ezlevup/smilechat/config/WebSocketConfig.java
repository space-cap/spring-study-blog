package com.ezlevup.smilechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 * STOMP 프로토콜을 사용하여 실시간 메시징 기능을 구성
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * - "/topic": 다중 사용자에게 브로드캐스트 (공개 채팅)
     * - "/queue": 개별 사용자에게 메시지 전송 (개인 상담)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 처리할 목적지 prefix 설정
        registry.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 서버로 메시지를 보낼 때 사용할 prefix
        registry.setApplicationDestinationPrefixes("/app");

        // 개인 메시지를 위한 사용자별 목적지 prefix
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * STOMP 엔드포인트 등록
     * 클라이언트가 WebSocket 연결을 수립할 때 사용할 엔드포인트
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트 설정
        registry.addEndpoint("/chat-websocket")
                .setAllowedOriginPatterns("*")  // CORS 설정 (운영 환경에서는 특정 도메인으로 제한)
                .withSockJS();  // SockJS fallback 옵션 활성화 (WebSocket 미지원 브라우저 대응)
    }
}
