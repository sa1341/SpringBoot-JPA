package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Items;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자의 접근제어를 protected로 설정하면 외부에서 생성자를 통해서 객체생성 불가함... 유지보수를 위한 큰그림
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Items items;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격

    private int count; //주문 수량


    //==생성 메서드 ==//
    public static OrderItem createOrderItem(Items item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItems(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }


    //==비즈니스 로직==//
    public void cancel() {
        getItems().addStockQuantity(count);
    }

    /**
     * 주문상품 전체 가격 조회
     */
    //==조회 로직==//
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }


}
