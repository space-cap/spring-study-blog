'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#message-form');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var sendButton = document.querySelector('#send-button');
var leaveButton = document.querySelector('#leave-chat');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.style.display = 'flex';

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    document.querySelector('#connected-user-fullname').textContent = username;
}

function onError(error) {
    connectingElement.textContent = '연결할 수 없습니다. 페이지를 새로고침하고 다시 시도해주세요.';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('div');

    if(message.type === 'JOIN') {
        messageElement.classList.add('system-message');
        messageElement.textContent = message.content;
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('system-message');
        messageElement.textContent = message.content;
    } else {
        messageElement.classList.add('message');
        
        if(message.sender === username) {
            messageElement.classList.add('own');
        } else {
            messageElement.classList.add('other');
        }

        var messageHeader = document.createElement('div');
        messageHeader.classList.add('message-header');
        
        var usernameElement = document.createElement('span');
        usernameElement.textContent = message.sender;
        usernameElement.style.fontWeight = 'bold';
        usernameElement.style.color = getAvatarColor(message.sender);
        
        var timeElement = document.createElement('span');
        timeElement.textContent = message.timestamp;
        timeElement.style.color = '#999';
        
        messageHeader.appendChild(usernameElement);
        messageHeader.appendChild(timeElement);

        var messageContent = document.createElement('div');
        messageContent.classList.add('message-content');
        messageContent.textContent = message.content;

        messageElement.appendChild(messageHeader);
        messageElement.appendChild(messageContent);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function disconnect() {
    if(stompClient !== null) {
        var chatMessage = {
            sender: username,
            type: 'LEAVE'
        };
        stompClient.send("/app/chat.addUser", {}, JSON.stringify(chatMessage));
        stompClient.disconnect();
    }
    usernamePage.style.display = 'flex';
    chatPage.style.display = 'none';
    username = null;
}

// 이벤트 리스너
usernameForm.addEventListener('submit', connect, true);

// 메시지 전송 이벤트 (폼이 없으므로 버튼과 엔터키에 직접 연결)
sendButton.addEventListener('click', sendMessage, true);

messageInput.addEventListener('keypress', function(event) {
    if(event.key === 'Enter') {
        sendMessage(event);
    }
});

leaveButton.addEventListener('click', disconnect, true);

// 페이지 로드 시 사용자명 입력 포커스
window.addEventListener('load', function() {
    document.querySelector('#name').focus();
});