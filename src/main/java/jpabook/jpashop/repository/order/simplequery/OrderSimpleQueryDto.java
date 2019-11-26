package jpabook.jpashop.repository.order.simplequery;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderSimpleQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;


    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;                 // LAZY 초기화
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;              // LAZY 초기화
    }

    public OrderSimpleQueryDto(Order order) {

        this.orderId = order.getId();
        this.name = order.getMember().getName(); // LAZY 초기화
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        address = order.getDelivery().getAddress(); // LAZY 초기화
    }


}
