package jpabook.jpashop.vo;

import lombok.Data;

@Data
public class SampleVO {

    private String name;
    private int age;

    public SampleVO(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
