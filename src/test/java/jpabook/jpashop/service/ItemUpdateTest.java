package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;
    

    @Test
    public void updateTest() throws Exception {
                
        Book book = em.find(Book.class, 1L);
        
        // TX
        book.setName("객체지향과 디자인 패턴");

        //변경 감지 == dirty checking 변경감지 기능을 사용해야한다. 실무에서.. 머지를 사용하면 영속성 엔티티의 필드를 다 갈아버리기 때문에.. null값이 있으면 디비에 널로 업데이트 할 위험성이 있음.

        //TX END



     }

}
