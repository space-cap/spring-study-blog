package com.develead.smile.service;
import com.develead.smile.domain.SystemSetting;
import com.develead.smile.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingService {
    private final SystemSettingRepository settingRepository;

    public Map<String, String> getAllSettings() {
        return settingRepository.findAll().stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue));
    }

    public String getSettingValue(String key) {
        return settingRepository.findById(key)
                .map(SystemSetting::getSettingValue)
                .orElse(null);
    }

    public void saveSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            SystemSetting setting = settingRepository.findById(key)
                    .orElse(new SystemSetting());
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            settingRepository.save(setting);
        });
    }
}
