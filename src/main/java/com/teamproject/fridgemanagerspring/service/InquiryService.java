package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.inquriy.Inquiry;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.dto.inquiry.request.InquiryRequest;
import com.teamproject.fridgemanagerspring.repository.InquiryRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Inquiry> getInquiryList(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return inquiryRepository.findByUserIdOrderByIdDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Inquiry getInquiryById(Long inquiryId) {
        return inquiryRepository.findByIdWithUser(inquiryId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_INQUIRY"));
    }

    @Transactional
    public Inquiry createInquiry(Long userId, InquiryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Inquiry inquiry = Inquiry.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public Inquiry updateInquiry(Long userId, Long inquiryId, InquiryRequest request) {
        Inquiry inquiry = getInquiryById(inquiryId);

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new RuntimeException("NOT_YOUR_INQUIRY");
        }
        if (inquiry.getAnswer() != null) {
            throw new RuntimeException("ALREADY_ANSWERED");
        }

        inquiry.updateInquiry(request.getTitle(), request.getContent());
        return inquiry;
    }

    @Transactional
    public void deleteInquiry(Long userId, Long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new RuntimeException("NOT_YOUR_INQUIRY");
        }
        if (inquiry.getAnswer() != null) {
            throw new RuntimeException("ALREADY_ANSWERED");
        }

        inquiryRepository.delete(inquiry);
    }
}