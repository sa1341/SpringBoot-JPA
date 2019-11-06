package jpabook.jpashop.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMemberRequest {

    @NotEmpty
    private String name;

}
