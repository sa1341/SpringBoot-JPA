package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // readOnly를 true로 설정하면 DB에서 리소스를 절약하여 읽기전용으로 조회하기 때문에 성능상의 이점을 누린다. public 에만 적용됨.
@RequiredArgsConstructor  // final 필드만 생성자 호출시에 값을 초기화 해줌. 스프링 4.3부터는 @Autowired 없이 생성자만 넣어주면 알아서 주입해준다.*
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional // 우선순위를 가짐
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId(); // h2를 쓰기 때문에 기본 키 생성전략를 자동으로 DB에 위임시 시퀀스 전략을 사용한다. 시퀀스 전략은 시퀀스 값을 얻은 후 엔티티에 할당 후 영속성화 시킴
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
