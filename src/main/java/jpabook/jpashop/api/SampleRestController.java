package jpabook.jpashop.api;

import jpabook.jpashop.vo.SampleVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleRestController {

    @RequestMapping("/sample")
    public ResponseEntity<SampleVO> sampleVO(final SampleVO sampleVO){
        System.out.println(sampleVO.getName());
        System.out.println(sampleVO.getAge());

        return new ResponseEntity<>(sampleVO, HttpStatus.OK);
    }

}
