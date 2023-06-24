package com.example.coffies_vol_02.member.domain;

import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_user",indexes = {
        @Index(name = "member_index1",columnList = "userId"),
        @Index(name = "member_index2",columnList = "memberName"),
        @Index(name = "member_index3",columnList = "userEmail")
})
public class Member extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String userId;
    @Setter
    private String password;
    private String memberName;
    private String userPhone;
    private String userGender;
    private String userAge;
    private String userEmail;
    private String userAddr1;
    private String userAddr2;
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;
    @Column(name = "failed_attempt")
    private Integer failedAttempt;
    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    @Enumerated(EnumType.STRING)
    private Role role;


    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<FavoritePlace>wishList = new ArrayList<>();

    @Builder
    public Member(Integer id,
                  String userId,
                  String password,
                  String memberName,
                  String userPhone,
                  String userGender,
                  String userAge,
                  String userEmail,
                  String userAddr1,
                  String userAddr2,
                  Boolean accountNonLocked,
                  Integer failedAttempt,
                  LocalDateTime lockTime,
                  Role role){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.memberName = memberName;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userAddr1 = userAddr1;
        this.userAddr2 = userAddr2;
        this.accountNonLocked = accountNonLocked;
        this.failedAttempt = failedAttempt;
        this.lockTime = lockTime;
        this.role = role;
        this.getCreatedTime();
        this.getUpdatedTime();
    }

    //회원 수정(Dirty Checking)
    public void updateMember(MemberRequest memberCreateDto){
        this.userId = memberCreateDto.userId();
        this.memberName = memberCreateDto.memberName();
        this.userAge = memberCreateDto.userAge();
        this.userEmail = memberCreateDto.userEmail();
        this.userGender =  memberCreateDto.userGender();
        this.userPhone = memberCreateDto.userPhone();
        this.userAddr1 = memberCreateDto.userAddr1();
        this.userAddr2 = memberCreateDto.userAddr2();
    }

}
