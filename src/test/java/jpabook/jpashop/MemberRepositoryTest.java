package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


//JPA를 사용하면 항상 트랜잭션 안에서 데이터를 변경해야한다. 트랜잭션 없이 데이터를 변경하면 예외가 발생한다.
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;



    @Test
    @Transactional //스프링 프레임워크를 사용하기 때문에 스프링에서 제공해주는 트랜잭션을 사용하기를 권고함 //테스트에 해당 어노테이션이 붙어있으면 테스트가 성공해도 롤백된다.
    @Rollback(false) //롤백 수행을 안한다.
    public void testMember() throws Exception {

        //given
        Member member = new Member();
        member.setName("memberA");

        //cmd + alt + v 변수 뽑아오는 리펙토링 단축키 <- 엄청 유용하니 자주 사용하는 습관을 기르자.
        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        Assertions.assertThat(findMember).isEqualTo(member);
     }



}