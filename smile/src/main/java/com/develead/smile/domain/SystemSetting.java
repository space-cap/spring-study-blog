package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "system_setting") @Getter @Setter
public class SystemSetting {
    @Id
    private String settingKey;
    @Lob
    private String settingValue;
    private String description;
    private LocalDateTime updatedAt;

    // [수정] 엔티티가 저장되거나 업데이트되기 전에 현재 시간을 자동으로 설정합니다.
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
