package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


// componentscan 으로 인해서 자동으로 스프링 빈으로 등록되어 진다.
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 스프링이 알아서.. EntityManager 를 생성에서 빈으로 주입해준다. 원래는 앤터티매니저 팩토리를 생성해서 직접 엔터티매니저를 생성해줘야 됬음
    // 스프링 데이터 JPA가 지원해줍니다.
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // 단건 조회
    }


    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) // sql로 번역된다. 엔티티를 대상으로 쿼리를 만들어야됨.
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name).getResultList();
    }

}
