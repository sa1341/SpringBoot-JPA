package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.vo.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // v1은 도메인을 외부에 노출하는 api로써 지연로딩으로 설정한 연관관계는 조회할때 로딩하지 않고 프록시로 반환하기 때문에 json으로 렌더링할때 강제로 프록시 초기화를 해줘야 됩니다.
    // 마찬가지로 프록시를 초기화하면 해당 연관관계를 가진 엔티티를 데이터베이스에서 죄회하기 때문에 조회 쿼리가 1 + N + N 만큼 나오기 때문에 절대로 이렇게 쓰지 말기....
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
    // v2는 도메인을 외부에 노출하지 않고 Dto로 변환하여 외부에 공개하는 api로 v1보다는 좋은 전략이지만.. 여전히 쿼리가 많이 나가기 때문에 성능을 개선해야 됩니다.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> odersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return  collect;

    }


    // v2와 사실 코드와 마찬가지로 Dto를 외부에 노출하는 api입니다. 하지만 join fetch를 사용하므로써 한번의 쿼리로 다 조회하기 때문에 성능상으로 이전 버전보다 크게 개선되었습니다.
    // 단점은 컬렉션 패치이기 때문에 페이징 처리가 불가능합니다. 그리고 일대다 조인이 포함되기 때문에 데이터베이스에서 중복로우를 반환하기 때문에 메모리에서 하이버네이트가 distinct를 이용해서 중복을 제거해야합니다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> odersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return  collect;
    }


    // ToOne 연관관계를 가진 엔티티와 join fetch를 하고 컬렉션은 fetch_size를 이용해서 지연로딩의 성능을 개선하여 조회합니다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> odersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return  collect;
    }

    // JPA에서 쿼리 결과를 Dto로  직접 넣어서 반환하기 때문에 도메인을 조회하는 repository와 분리해서 만들어야 됩니다.
    // 테이블에서 Order 엔티티의 수 만큼 OrderItem 조회 쿼리가 발생하기 때문에 결국 v4도 1 + N 문제가 발생합니다.
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> odersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }


    //orderQueryRepository는 도메인을 이용하여 핵심 비즈니스 로직을 처리하지 않기 때문에 repository 디렉토리와 별도로 분리하였습니다.
    // 루트 조회(ToOne 연관관계를 모두 한번에 조회) + orderItem 컬렉션을 Map 한방에 조회하여 루프를 돌면서 컬렉션을 추가합니다. 총 2번의 조회 쿼리 발생
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> odersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 쿼리는 한방이지만 조인으로 인해  DB에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에 따라 V5보다 더 느릴 수 있습니다. 데이터가 많지 않을때 쓰는게 좋습니다.
    // 애플리케이션에서 추가 작업이 큽니다. Order를 기준으로 페이징 처리는 불가능함.
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> odersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                        o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                        o.getItemName(), o.getOrderPrice(), o.getCount()), toList()) )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }

}
