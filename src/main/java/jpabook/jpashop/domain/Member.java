package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    //연관관계의 주인을 정해주기 위해서 mappedBy 속성을 명시해줘야 한다. 연관관계를 맺을때 누가 외래키를 가지고 있는지 알려주어야 JPA의 혼란을 막을 수 있기 때문이다...
    @OneToMany(mappedBy = "member") // 연관관계의 매핑된 거울임. 읽기만 가능...
    private List<Order> orders = new ArrayList<>();


}
