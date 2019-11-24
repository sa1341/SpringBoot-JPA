package jpabook.jpashop.domain;

import javafx.scene.layout.BorderRepeat;
import jpabook.jpashop.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 실제로 지연로딩이기 때문에 Member 엔티티를 생성을 안하고 상속받은 프록시 객체를 하이버네이트가 생성해줍니다. ByteBuddyInterceptor 객체가 생성됩니다.
    //@ManyToOne은 기본적으로 패치 전략이 EAGER이기 때문에.. 반드시 실무에서는 LAZY로 설정을 해줘야한다. 안그러면 N + + 1 문제가 발생한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 연관관계의 주인이 아님을 JPA에게 알려줍니다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>(); //컬렉션은 필드에서 바로 초기화 하는것이 안전함. null문제에서도 안전하다.

    //OneToOne도 디폴트로 패치전략이 EAGER이기 때문에 LAZY로 전략 설정이 필요함. xToOne은 디폴트로 EAGER 이다.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ ORDER, CANCEL ]

    //연관관계 편의 메소드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItems(OrderItem orderItems) {
        this.orderItems.add(orderItems);
        orderItems.setOrder(this);
    }

    public void setDeivery(Delivery deivery) {
        this.delivery = deivery;
        deivery.setOrder(this);
    }


    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {

        Order order = new Order();
        order.setMember(member);
        order.setDeivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItems(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */

    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//

    /**
     * 전체 주문 가격 조회
     */
    // alt + enter로 람다식으로 변경해줌
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


}
