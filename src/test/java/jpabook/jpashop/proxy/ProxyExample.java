package jpabook.jpashop.proxy;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProxyExample {


    @Autowired
    DefaultMyService defaultMyService;

    @Test
    public void 인터페이스_기반_프록시생성() throws Exception {
        //given

        //when
        defaultMyService.doSomeThing();
        System.out.println(defaultMyService.getClass());
        //then
    }


}
