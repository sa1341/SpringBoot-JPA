package jpabook.jpashop.vo;


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
public class SimpleOrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;


    public SimpleOrderDto(Order order) {

        this.orderId = order.getId();
        this.name = order.getMember().getName(); // LAZY 초기화
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        address = order.getDelivery().getAddress(); // LAZY 초기화
    }
}
