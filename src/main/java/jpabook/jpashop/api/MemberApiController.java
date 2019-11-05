package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

// @Controller + @ResponseBody와 같습니다.
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    // api를 만들때에는 엔티티를 외부에 노출하지 않아야 됩니다. 실무에서는 다양한 api가 존재하는데 그때마다 엔티티 하나로 다양한 api 요청 요구사항을 맞추기 어렵습니다.
    // 엔티티가 변경되면 api 스펙이 변경된다는 사실을 염두해야 합니다.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){

        log.info("" + request.getName());
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @Data
    @NoArgsConstructor
    public static class CreateMemberRequest{

        private String name;

        public CreateMemberRequest(String name) {
            this.name = name;
        }
    }


    @Data
    public static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
