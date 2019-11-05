package jpabook.jpashop.proxy;

import org.springframework.stereotype.Service;

@Service
public class DefaultMyService implements MyService {
    @Override
    public void doSomeThing() {
        System.out.println("고고 씽");
    }
}
