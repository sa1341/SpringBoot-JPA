package jpabook.jpashop.repository.order.query;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;
    // 이 쿼리메소드도 결국 N +1 문제가 발생합니다. query 1번 -> N
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // Query N 번
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        " from OrderItem oi" +
                        " join oi.items i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                 "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)  from Order o" +
                         " join o.member m" +
                         " join o.delivery d", OrderQueryDto.class)
                 .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {

        // 주문, 회원, 배송 엔티티 join한 결과를 OrderQueryDto 타입을 가지고 있는 컬렉션 List 객체 반환
        List<OrderQueryDto> result = findOrders();
        // orderItem을 최적화로 조회하기 위해서 OrderQueryDto가 가지고 있는 order_id들을 List 객체로 반환

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count ) " +
                        " from OrderItem oi" +
                        " join oi.items i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                    .map(o -> o.getOrderId())
                    .collect(Collectors.toList());
    }
}
