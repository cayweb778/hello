package org.boozsoft.rest;

import org.boozsoft.domain.vo.RoleVo;
import org.boozsoft.repo.RoleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Role2Controller {
  @Autowired
  RoleRepository roleRepository;

  @GetMapping("/sys/getAllRoleList")
  public Mono<R> a() {
    return getData()
        .map(R::ok);
  }



  public Mono<java.util.List<RoleVo>> getData() {
    return roleRepository.findAll()
        .map(item -> {
          RoleVo roleVo = new RoleVo();
          roleVo.setId(item.getId());
          roleVo.setRoleValue(item.getRoleAlias());
          roleVo.setRoleName(item.getRoleName());
          roleVo.setOrderNo(item.getSort());
          roleVo.setStatus("1");
          roleVo.setRemark("无");
          return roleVo;
        })
        .collectList();
  }

  @GetMapping("/sys/getRoleListByPage")
  public Mono<R> getRoleListByPage(Pageable pageable) {
    return getData()
        .map(R::page);
  }
}
