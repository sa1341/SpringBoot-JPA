package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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
    public List<OrderSimpleQueryDto> ordersV2(){

        // ORDER 2개
        // N +1 -> 1 + 회원 N + 배송 N
       return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderSimpleQueryDto::new)
                .collect(toList());

    }
    //갓오브 갓 성능 최적화 하는데 최고인 페치조인은 100% 이해해야 합니다. 실무에서 너무 많이쓰이는 명령어입니다.
    // V3 같은 경우에는 재사용성이 V4에 비하여 뛰어나기 때문에 서로 우열을 가릴수가 없습니다.
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3() {

        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(toList());
    }

    // 화면에 최적화된 데이터만 보여줄때는 최고이지만 재사용성을 할 수 없기 때문에 V3에 비해서 딱딱합니다.
    // 이런식으로 JPA에서 조회한 값을 Dto로 바로 변환하면 API 스펙이 repo에 의존하기 때문에 query 관련 패키지를 만들어서 따로 쿼리 레포 클래스를 정의해서 사용해야합니다.
    // 순수 레파지토리는 도메인을 대상으로 조회해야 의미가 있고, api 스펙이 변경된다고 해도 재사용성이 증가하는 장점이 있습니다.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();

    }


}
