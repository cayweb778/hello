//package org.boozsoft.rest;
//
//
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.boozsoft.repo.group.GroupSysNcPanelRepository;
//import org.springbooz.core.tool.result.R;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@RestController
//@RequestMapping("/nc")
//@Api(value = "NC面板设置", tags = "NC面板设置")
//public class NcPanelController {
//    @Autowired
//    GroupSysNcPanelRepository groupSysUserRepository;
//
//    @PostMapping("findAllByNcPanel")
//    public Mono<R> findAllByNcPanel(){
//        return groupSysUserRepository.findAllOederByPanelOrder().collectList().map(o -> R.ok().setResult(o));
//    }
//
//    @PostMapping("editFlagAndAccId")
//    public Mono<R> editFlagAndAccId(String flag,String accId,String id){
//        return groupSysUserRepository.editFlagAndAccId(flag,accId,id).then(Mono.just(R.ok()));
//    }
//    @PostMapping("editOrderAndAccId")
//    public Mono<R> editOrderAndAccId(Integer order,String accId,String id){
//        return groupSysUserRepository.editOrderAndAccId(order,accId,id).then(Mono.just(R.ok()));
//    }
//}
