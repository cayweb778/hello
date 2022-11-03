package org.boozsoft.util;

import org.boozsoft.domain.vo.Menu2Vo;
import org.boozsoft.repo.entity.Menu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuUtils {
  public static Mono<List<Menu2Vo>> toMenuVo(Flux<Menu> flux) {
    return flux
        .map(item -> {
          Menu2Vo vo = new Menu2Vo();
          vo.setId(item.getId());
          vo.setMenuName(item.getName());
          vo.setCreateTime(item.getCreateTime());
          vo.setOrderNo(item.getSortNo());
          vo.setIcon(item.getIcon());
          vo.setParentMenu(item.getParentId());
          vo.setStatus(item.getStatus());
          vo.setComponent(item.getComponent());
          vo.setPermission(item.getPerms());
          return vo;
        })
        .collectList()
        .map(ForestNodeMerger::merge);
  }

}
class ForestNodeMerger {
  public ForestNodeMerger() {
  }

  public static  List<Menu2Vo> merge(List<Menu2Vo> items) {
    ForestNodeManager forestNodeManager = new ForestNodeManager(items);
    items.forEach((forestNode) -> {
      if (!forestNode.getParentMenu().equals("0")) {
        Menu2Vo node = forestNodeManager.getTreeNodeAT(forestNode.getParentMenu());
        if (node != null) {
          node.getChildren().add(forestNode);
        }
      }

    });
    return forestNodeManager.getRoot();
  }
}
class ForestNodeManager{
  private List<Menu2Vo> list;
  private List<String> parentIds = new ArrayList();

  public ForestNodeManager(List<Menu2Vo> items) {
    this.list = items;
  }

  public Menu2Vo getTreeNodeAT(String id) {
    Iterator var2 = this.list.iterator();

    Menu2Vo forestNode;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      forestNode = (Menu2Vo)var2.next();
    } while(!forestNode.getId().equals(id));

    return forestNode;
  }

  public void addParentId(String parentId) {
    this.parentIds.add(parentId);
  }

  public List<Menu2Vo> getRoot() {
    List<Menu2Vo> roots = new ArrayList();
    Iterator var2 = this.list.iterator();

    while(true) {
      Menu2Vo forestNode;
      do {
        if (!var2.hasNext()) {
          return roots;
        }

        forestNode = (Menu2Vo)var2.next();
      } while(!forestNode.getParentMenu().equals("0") && !this.parentIds.contains(forestNode.getId()));

      roots.add(forestNode);
    }
  }

}
