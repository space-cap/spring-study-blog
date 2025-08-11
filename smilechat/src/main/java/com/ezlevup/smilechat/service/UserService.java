package com.ezlevup.smilechat.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * 온라인 사용자 관리 서비스
 */
@Service
public class UserService {

    // 온라인 사용자 목록 (동시성 안전)
    private final ConcurrentHashMap<String, UserInfo> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 사용자 정보 클래스
     */
    public record UserInfo(
            String username,
            String role,      // "PATIENT" 또는 "STAFF"
            String sessionId,
            long connectedTime
    ) {}

    /**
     * 사용자 온라인 상태로 변경
     */
    public void addUser(String username, String role, String sessionId) {
        UserInfo userInfo = new UserInfo(username, role, sessionId, System.currentTimeMillis());
        onlineUsers.put(username, userInfo);
    }

    /**
     * 사용자 오프라인 상태로 변경
     */
    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    /**
     * 온라인 직원 목록 조회 (환자가 상담 요청할 수 있는 대상)
     */
    public Set<String> getOnlineStaff() {
        Set<String> staff = new HashSet<>();
        onlineUsers.values().forEach(user -> {
            if ("STAFF".equals(user.role())) {
                staff.add(user.username());
            }
        });
        return staff;
    }

    /**
     * 온라인 환자 목록 조회 (직원이 확인할 수 있는 대상)
     */
    public Set<String> getOnlinePatients() {
        Set<String> patients = new HashSet<>();
        onlineUsers.values().forEach(user -> {
            if ("PATIENT".equals(user.role())) {
                patients.add(user.username());
            }
        });
        return patients;
    }

    /**
     * 사용자 온라인 상태 확인
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }
}
