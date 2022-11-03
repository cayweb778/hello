//package org.boozsoft.rest;
//
//import org.boozsoft.repo.SupplierRepository;
//import org.boozsoft.repo.repository.group.GroupInfoRepository;
//import org.springbooz.datasource.r2dbc.helper.R2dbcHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@RestController
//public class HelloAbcController {
//  @Autowired
//  public GroupInfoRepository groupInfoRepository;
//  @Autowired
//  public SupplierRepository supplierRepository;
//
//  @RequestMapping("/auth/aa")
//  public Mono<Void> a() {
//    // 第一种写法
//    Flux flux = R2dbcHelper.use("boozsoft-nc", "bjxgkj-001").exec(() -> groupInfoRepository.findAll());
//
//    // 第二种写法
//    Flux flux2 = Mono.just("").flatMapMany(item -> R2dbcHelper.use("boozsoft-nc", "bjxgkj-001").exec(() -> {
//      return groupInfoRepository.findAll();
//    }));
//
//    // 第三种写法
//    Flux flux3 = Mono.just("")
//        .map(item -> R2dbcHelper.use("boozsoft-nc", "bjxgkj-001"))
//        .flatMapMany(item -> item.exec(() -> groupInfoRepository.findAll()));
//    return null;
//  }
//}
