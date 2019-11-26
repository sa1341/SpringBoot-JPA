package jpabook.jpashop.vo;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {

    private String itemName;
    private int orderPrice;
    private int count;


    public OrderItemDto(OrderItem orderItem) {

        this.itemName = orderItem.getItems().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();

    }
}
