document.addEventListener('DOMContentLoaded', function() {
    // 챗봇 UI 요소 가져오기
    const chatbotToggler = document.getElementById('chatbotToggler');
    const heroChatbotBtn = document.getElementById('heroChatbotBtn');
    const chatbotPopup = document.getElementById('chatbotPopup');
    const closeChatbotBtn = document.getElementById('closeChatbotBtn');
    const chatForm = document.getElementById('chatForm');
    const chatInput = document.getElementById('chatInput');
    const chatMessages = document.getElementById('chatMessages');
    const loadingSpinner = document.getElementById('loadingSpinner'); // 스피너 요소 추가

    // API 서버 주소
    const API_ENDPOINT = 'http://127.0.0.1:8000/chat';

    // 챗봇 창 열기/닫기 함수
    const toggleChatbot = () => {
        chatbotPopup.classList.toggle('show');
        chatbotToggler.classList.toggle('d-none');
    };

    if (chatbotToggler) chatbotToggler.addEventListener('click', toggleChatbot);
    if (heroChatbotBtn) heroChatbotBtn.addEventListener('click', toggleChatbot);
    if (closeChatbotBtn) closeChatbotBtn.addEventListener('click', toggleChatbot);

    // 메시지를 화면에 추가하는 함수
    const addMessage = (message, sender) => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('chat-message', `chat-message-${sender}`);
        messageElement.textContent = message;
        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    };

    // 폼 제출(메시지 전송) 이벤트
    if (chatForm) {
        chatForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const userMessage = chatInput.value.trim();
            if (!userMessage) return;

            addMessage(userMessage, 'user');
            chatInput.value = '';

            // [수정] 로딩 스피너 표시
            loadingSpinner.classList.remove('d-none');
            chatMessages.scrollTop = chatMessages.scrollHeight;

            const sessionId = sessionStorage.getItem('chatbot_session_id');

            try {
                const response = await fetch(API_ENDPOINT, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        session_id: sessionId,
                        message: userMessage
                    })
                });

                if (!response.ok) {
                    throw new Error('서버 응답에 문제가 있습니다.');
                }

                const data = await response.json();
                sessionStorage.setItem('chatbot_session_id', data.session_id);
                addMessage(data.response, 'bot');

                if (data.is_complete) {
                    chatInput.disabled = true;
                    chatInput.placeholder = '상담 접수가 완료되었습니다.';
                }

            } catch (error) {
                console.error('Error:', error);
                addMessage('죄송합니다. 서버와 통신 중 오류가 발생했습니다.', 'bot');
            } finally {
                // [수정] 응답이 오면 로딩 스피너 숨기기
                loadingSpinner.classList.add('d-none');
            }
        });
    }

    addMessage('안녕하세요! 스마일 치과 챗봇입니다. 무엇을 도와드릴까요?', 'bot');
});
