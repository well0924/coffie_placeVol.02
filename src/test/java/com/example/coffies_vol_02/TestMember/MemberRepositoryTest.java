package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.TestQueryDslConfig;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponseDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 검색 테스트")
    public void memberSearchTest(){
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        String keyword= "well4149";

        Page<MemberResponseDto> result = memberRepository.findByAllSearch(keyword,pageable);

        System.out.println(result);
    }
}
