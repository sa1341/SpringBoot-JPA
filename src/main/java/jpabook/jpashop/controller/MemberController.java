package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){ // 바인딩 객체를 쓰지 않으면 검증실패시 컨트롤러 단에서 튕겨저 나가서 /error로 빠진다.
        // BindingResult는 MemberForm에서 스프링에서 검증을 하는데 실패가 나면 에러내용를 담고 있는 객체로 다시 뷰단으로 끌고 가는 기능을 가지고 있음.
        //Client 에서 요청한 MemberForm에 바인딩 된 값도 그대로 다시 가져오기 때문에 회원가입 폼에도 다시 고객이 작성한 내용이 그대로 보존된다.
        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }

}
