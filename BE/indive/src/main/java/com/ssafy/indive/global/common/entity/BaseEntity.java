package com.ssafy.indive.global.common.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @CreatedBy
    private String createMember;

    @LastModifiedBy
    private String updateMember;

    @PrePersist
    public void prePersist() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();

//        if (authentication != null) {
//            createMember = authentication.getName();
//            updateMember = authentication.getName();
//        }
    }

    @PreUpdate
    public void preUpdate() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        updateDate = LocalDateTime.now();

//        if (authentication != null) {
//            updateMember = authentication.getName();
//        }
    }
}
