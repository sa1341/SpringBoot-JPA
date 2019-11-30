package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.vo.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> odersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // intelliJ에서 제공해주는 자동완성기능을 활용합시다.  -> iter
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItems().getName());
        }
        
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> odersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return  collect;

    }


    // v2와 사실 코드가 바뀐게 없지만 repositoty의 JPQL 메소드를 어떻게 구현하느냐에 따라 데이터베이스에 전송되는 쿼리수를 줄이므로써 성능을 높일 수 있습니다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> odersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return  collect;
    }


    // offset은 몇번째 row부터 출력할 지를 결정합니다. 1번째 row이면 0입니다.
    // 네트워크 호출 수가 많냐.... 데이터베이스 데이터 전송량 사이에 trade off를 해야합니다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> odersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return  collect;
    }

    // jpa에서 dto로 직접 반환하기 때문에 도메인을 조회하는 repository와 분리해서 만들어야 됩니다.
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> odersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }


}
