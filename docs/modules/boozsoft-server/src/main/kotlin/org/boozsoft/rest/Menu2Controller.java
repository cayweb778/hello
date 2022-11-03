package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.vo.Menu2Vo;
import org.boozsoft.domain.vo.MenuVO;
import org.boozsoft.repo.Menu2Repository;
import org.boozsoft.repo.MenuRepository;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.resource.TokenResource;
import org.boozsoft.service.MenuServiceImpl;
import org.springbooz.core.tool.node.INode;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.boozsoft.util.MenuUtils.toMenuVo;

@RestController
public class Menu2Controller {
  @Autowired
  MenuServiceImpl menuService;
  @Autowired
  Menu2Repository repository;

  @GetMapping({"/sys/menu/platfroms2"})
  @ApiOperation(
      value = "根据角色ID获取属菜单集合",
      notes = "传入角色ID集合"
  )
  private Mono<R> platfroms() {
    return repository.findPlatforms().collectList().map((item) -> {
      return R.ok().setResult(item);
    });
  }


  @GetMapping({"/sys/menu/getAllPlatform"})
  @ApiOperation(
          value = "根据角色ID获取属菜单集合",
          notes = "传入角色ID集合"
  )
  private Mono<R> getAllPlatform() {
    return platfroms();
  }

  @GetMapping("/sys/menu/tree2")
  public Mono<R> a() {
    return toMenuVo(repository.findAll())
        .map(R::ok);
  }



}

