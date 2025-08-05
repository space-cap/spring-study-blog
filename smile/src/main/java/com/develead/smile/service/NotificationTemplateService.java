package com.develead.smile.service;
import com.develead.smile.domain.NotificationTemplate;
import com.develead.smile.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class NotificationTemplateService {
    private final NotificationTemplateRepository templateRepository;
    public List<NotificationTemplate> findAll() { return templateRepository.findAll(); }
    public Optional<NotificationTemplate> findById(Integer id) { return templateRepository.findById(id); }
    public NotificationTemplate save(NotificationTemplate template) { return templateRepository.save(template); }
}
