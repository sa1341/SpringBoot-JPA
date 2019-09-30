package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    //@ManyToOne은 기본적으로 패치 전략이 EAGER이기 때문에.. 반드시 실무에서는 LAZY로 설정을 해줘야한다. 안그러면 N + + 1 문제가 발생한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
     // 연관관계의 주인이 아님을 JPA에게 알려줍니다.
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    //OneToOne도 디폴트로 패치전략이 EAGER이기 때문에 LAZY로 전략 설정이 필요함.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ ORDER, CANCEL ]

}
