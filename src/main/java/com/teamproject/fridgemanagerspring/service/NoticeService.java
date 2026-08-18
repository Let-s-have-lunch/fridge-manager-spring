package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.notice.Notice;
import com.teamproject.fridgemanagerspring.dto.notice.request.NoticeRequest;
import com.teamproject.fridgemanagerspring.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public Page<Notice> getNoticeList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return noticeRepository.findAllByOrderByIdDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_NOTICE"));
    }

    @Transactional
    public Notice createNotice(NoticeRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, NoticeRequest request) {
        Notice notice = getNoticeById(id);
        notice.updateNotice(request.getTitle(), request.getContent());
        return notice;
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = getNoticeById(id);
        noticeRepository.delete(notice);
    }
}