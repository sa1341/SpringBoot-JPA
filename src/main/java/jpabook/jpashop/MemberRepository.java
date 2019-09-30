package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


//cmd + shift + t를 누르면 인텔리제이에서 해당 클래스에 대한 테스트 클래스파일을 만들어준다. JUnit4를 사용함..꼭 그런건아니지만 강좌대로 해봄
@Repository
public class MemberRepository {


    @PersistenceContext
    private EntityManager em;

    //커멘드 객체를 반환하는 메소드랑 분리
    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

}
