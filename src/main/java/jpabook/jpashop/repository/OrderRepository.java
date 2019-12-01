package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }


    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order  o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        List<Order> resultList = query.getResultList();

        return resultList;
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" +
                    orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    // 지연로딩을 무시하고 한번에 다 가져오는 쿼리. 프록시조차 무시하고 진짜 엔티티를 가져옵니다.
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch){

        JPAQueryFactory query = new JPAQueryFactory(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;



        return query.select(order)
              .from(order)
              .join(order.member, member)
              .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
              .limit(1000)
              .fetch();

    }

    private BooleanExpression nameLike(String memberName) {
        if(!StringUtils.hasText(memberName)){
            return null;
        }
        return QMember.member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    //default_batch_fetch_size 크기만큼 in 쿼리의 파라미터로 추가가 되기 때문에 지연 로딩 성능이 향상되어서 컬렉션 패치를 페이징 처리하는데 더 성능이 좋습니다.
    //default_batch_fetch_size을 사용하면 1 + N + N -> 1 + 1 + 1로 됩니다.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        )       .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }



    public List<Order> findAllWithItem() {

         // DB 쿼리를 날릴때 distinct를 키워드를 넣어서 날립니다.
        // ditstinct는 실제 DB 로우가 완전히 같지 않는 이상 먹히지 않기 때문에 결과는 달라지지 않지만 애플리케이션단에서 식별자 값을 기준으로 중복을 제거해서 collection에 넣어줍니다.
        // 컬렉션 페치조인을 사용하면  페이징 처리가 불가능합니다. 또한 컬렉션 페치 조인은 1개만 사용할 수 있습니다.
        return em.createQuery(
                "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.items i", Order.class
        )       .setFirstResult(0) // offset 1번 인덱스부터 가져옴... 그런데 현재 Order 엔티티가 테이블에 2건밖에 없기 때문에 결국 한개를 가져옵니다.
                .setMaxResults(100) // limit 100개를 가져옴  HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
                .getResultList();
    }
}










































































































































































































































































