package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Items;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;


    // 카테고리와 아이템 사이를 1:다 or 다:1로 풀어주는 조인테이블을 만듭니다.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Items> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    //OneToMany는 기본적으로 LAZY가 디폴트이기 때문에 바꿔줄 필요는 없다.
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 연관관계 편의 메소드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(parent);
    }

}
