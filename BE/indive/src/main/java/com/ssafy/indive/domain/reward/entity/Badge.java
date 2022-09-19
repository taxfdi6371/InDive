package com.ssafy.indive.domain.reward.entity;

import com.ssafy.indive.domain.member.entity.Member;
import com.ssafy.indive.domain.reward.entity.enumeration.BadgeType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {

    @Id @GeneratedValue
    @Column(name = "badge_seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_owner_seq")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_artist_seq")
    private Member artist;

    @Column(name = "badge_name")
    @Enumerated(EnumType.STRING)
    private BadgeType name;

    @Builder
    public Badge(Long seq, Member owner, Member artist, BadgeType name) {
        this.seq = seq;
        this.owner = owner;
        this.artist = artist;
        this.name = name;
    }
}
