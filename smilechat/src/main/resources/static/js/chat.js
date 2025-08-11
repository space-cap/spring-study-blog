/**
 * 치과 병원 실시간 채팅 클라이언트
 * SockJS와 STOMP를 사용한 WebSocket 통신
 */

let stompClient = null;
let username = null;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    username = sessionStorage.getItem('username');

    if (!username) {
        alert('먼저 이름을 입력해주세요.');
        window.location.href = '/';
        return;
    }

    connect();
    setupEventListeners();
});

/**
 * WebSocket 연결 설정
 */
function connect() {
    const socket = new SockJS('/chat-websocket');
    stompClient = new StompJs.Client({
        webSocketFactory: () => socket,
        connectHeaders: {},
        debug: function (str) {
            console.log('STOMP Debug: ' + str);
        },
        reconnectDelay: 5000,  // 재연결 지연시간 5초
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    // 연결 성공 시
    stompClient.onConnect = function (frame) {
        console.log('Connected: ' + frame);
        updateConnectionStatus('연결됨', 'connected');

        // 공개 채팅방 구독
        stompClient.subscribe('/topic/public', function (message) {
            showMessage(JSON.parse(message.body));
        });

        // 개인 메시지 구독
        stompClient.subscribe('/user/queue/private', function (message) {
            showMessage(JSON.parse(message.body));
        });

        // 입장 메시지 전송
        sendJoinMessage();
    };

    // 연결 오류 시
    stompClient.onStompError = function (frame) {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        updateConnectionStatus('연결 오류', 'error');
    };

    // WebSocket 연결 오류 시
    stompClient.onWebSocketError = function (error) {
        console.error('WebSocket Error: ', error);
        updateConnectionStatus('연결 실패', 'error');
    };

    // 연결 해제 시
    stompClient.onDisconnect = function () {
        console.log('Disconnected');
        updateConnectionStatus('연결 해제됨', 'disconnected');
    };

    // 연결 시작
    stompClient.activate();
}

/**
 * 이벤트 리스너 설정
 */
function setupEventListeners() {
    // Enter 키로 메시지 전송
    document.getElementById('messageInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    // 페이지 언로드 시 연결 해제
    window.addEventListener('beforeunload', function() {
        if (stompClient && stompClient.connected) {
            sendLeaveMessage();
            stompClient.deactivate();
        }
    });
}

/**
 * 일반 메시지 전송
 */
function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient && stompClient.connected) {
        const chatMessage = {
            content: messageContent,
            sender: username,
            type: 'CHAT',
            timestamp: new Date().toISOString()
        };

        stompClient.publish({
            destination: '/app/chat.sendMessage',
            body: JSON.stringify(chatMessage)
        });

        messageInput.value = '';
    }
}

/**
 * 입장 메시지 전송
 */
function sendJoinMessage() {
    if (stompClient && stompClient.connected) {
        const joinMessage = {
            content: '',
            sender: username,
            type: 'JOIN'
        };

        stompClient.publish({
            destination: '/app/chat.addUser',
            body: JSON.stringify(joinMessage)
        });
    }
}

/**
 * 퇴장 메시지 전송
 */
function sendLeaveMessage() {
    if (stompClient && stompClient.connected) {
        const leaveMessage = {
            content: username + '님이 상담을 종료했습니다.',
            sender: username,
            type: 'LEAVE'
        };

        stompClient.publish({
            destination: '/app/chat.sendMessage',
            body: JSON.stringify(leaveMessage)
        });
    }
}

/**
 * 메시지 화면에 표시
 */
function showMessage(message) {
    const messageArea = document.getElementById('messageArea');
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');

    // 메시지 타입별 스타일 적용
    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('system-message');
        messageElement.innerHTML = `
            <div class="message-content">
                <i class="system-icon">ℹ️</i>
                ${message.content}
            </div>
            <div class="message-time">${formatTime(message.timestamp)}</div>
        `;
    } else {
        const isOwnMessage = message.sender === username;
        messageElement.classList.add(isOwnMessage ? 'own-message' : 'other-message');

        messageElement.innerHTML = `
            <div class="message-header">
                <span class="sender">${message.sender}</span>
                <span class="timestamp">${formatTime(message.timestamp)}</span>
            </div>
            <div class="message-content">${escapeHtml(message.content)}</div>
        `;
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

/**
 * 연결 상태 업데이트
 */
function updateConnectionStatus(status, statusClass) {
    const statusElement = document.getElementById('connectionStatus');
    statusElement.textContent = status;
    statusElement.className = 'connection-status ' + statusClass;
}

/**
 * 시간 포맷팅
 */
function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * HTML 이스케이프 처리
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
