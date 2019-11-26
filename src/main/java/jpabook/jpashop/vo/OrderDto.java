package jpabook.jpashop.vo;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItem> orderItems;

    // Dto 안에는 엔터티가 있으면 안됩니다. 의존관계를 완전히 끊어줘야 나중에 API 스펙이 변경되어도 orderItem 도메인이 깨질일이 없기 때문입니다.
    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress();
        order.getOrderItems().stream().forEach(o -> o.getItems().getName());
        this.orderItems = order.getOrderItems();
    }
}
