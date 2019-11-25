package jpabook.jpashop.api;

import com.sun.org.apache.regexp.internal.RE;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.vo.SimpleOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * xToOne
 *
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 **/

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all){
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getStatus();
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){

        // ORDER 2개
        // N +1 -> 1 + 회원 N + 배송 N
       return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());

    }

}
