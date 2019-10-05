package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Items;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Items item){
        itemRepository.save(item);
    }

    public List<Items> findItems(){
        return itemRepository.findAll();
    }

    public Items findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        //실무에서는 set으로 어떤 필드를 수정하지 말고 엔티티 레벨에서 수정 가능하게 add, change(price, name ,stockQuantity) 방식으로 메소드를 구현하는 습관을 기르자.
        Items findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);

    }

}
