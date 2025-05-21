<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>운세 정보</title>
    <style>
        body {
            font-family: 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
            color: #333;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }
        
        .menu-container {
            display: flex;
            justify-content: space-between;
            padding: 20px 0;
            border-bottom: 1px solid #eee;
            overflow-x: auto;
            white-space: nowrap;
        }
        
        .menu-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 0 10px;
            text-decoration: none;
            color: #333;
            min-width: 60px;
        }
        
        .menu-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background-color: #f0f0f0;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 8px;
        }
        
        .menu-icon img {
            width: 32px;
            height: 32px;
        }
        
        .menu-text {
            font-size: 14px;
            color: #666;
        }
        
        .menu-item.active .menu-text {
            color: #ff6b6b;
            font-weight: bold;
        }
        
        .content {
            padding: 20px 0;
        }
        
        .title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .date {
            font-size: 14px;
            color: #999;
            margin-bottom: 20px;
        }
        
        .description {
            font-size: 16px;
            line-height: 1.6;
            color: #444;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="menu-container">
            <a href="#" class="menu-item active">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <circle cx="12" cy="12" r="10" fill="none" stroke="#ff6b6b" stroke-width="2" stroke-dasharray="40 60" />
                        <circle cx="12" cy="12" r="10" fill="none" stroke="#4dabf7" stroke-width="2" stroke-dasharray="30 70" stroke-dashoffset="40" />
                        <circle cx="12" cy="12" r="10" fill="none" stroke="#f783ac" stroke-width="2" stroke-dasharray="20 80" stroke-dashoffset="70" />
                    </svg>
                </div>
                <span class="menu-text">총운</span>
            </a>
            <a href="#" class="menu-item">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="#ff8787"/>
                    </svg>
                </div>
                <span class="menu-text">애정운</span>
            </a>
            <a href="#" class="menu-item">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <path d="M12 2L2 22h20L12 2zm0 5l6.5 12h-13L12 7z" fill="#20c997"/>
                    </svg>
                </div>
                <span class="menu-text">금전운</span>
            </a>
            <a href="#" class="menu-item">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <path d="M20,7h-4V4c0-1.1-0.9-2-2-2h-4C8.9,2,8,2.9,8,4v3H4C2.9,7,2,7.9,2,9v11c0,1.1,0.9,2,2,2h16c1.1,0,2-0.9,2-2V9 C22,7.9,21.1,7,20,7z M10,4h4v3h-4V4z M20,20H4V9h16V20z" fill="#ffa94d"/>
                    </svg>
                </div>
                <span class="menu-text">직장운</span>
            </a>
            <a href="#" class="menu-item">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <path d="M12,2L5,12l7,10,7-10L12,2z M12,4.6l4.3,6.1L12,16.9l-4.3-6.2L12,4.6z" fill="#748ffc"/>
                        <path d="M5,20h14v2H5V20z" fill="#748ffc"/>
                    </svg>
                </div>
                <span class="menu-text">학업,성적운</span>
            </a>
            <a href="#" class="menu-item">
                <div class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32">
                        <path d="M12 2L4 7v7c0 5.5 3.8 10.7 8 12 4.2-1.3 8-6.5 8-12V7l-8-5z" fill="#fcc419"/>
                        <path d="M11 16h2v2h-2z" fill="white"/>
                        <path d="M11 7h2v6h-2z" fill="white"/>
                    </svg>
                </div>
                <span class="menu-text">건강운</span>
            </a>
        </div>
        
        <div class="content">
            <div class="title">운세의 총운은 <span style="color: #ff6b6b; font-weight: bold;">인과응보</span> 입니다</div>
            <div class="date">${birth}</div>
            <div class="description">
                <p>${fortune}</p>
            </div>
			<div>다시 보시겠습니까? <a href="fortune">운세보기</a></div>
        </div>
    </div>
	
</body>
</html>
