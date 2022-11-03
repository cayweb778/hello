package org.boozsoft.domain.vo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Menu2Vo {
  private String id;
  private String menuName;
  private String icon;
  private String permission;
  private String component;
  private Double orderNo;
  private Date createTime;
  private String status;
  private String parentMenu;
  private List<Menu2Vo> children=new ArrayList<>();

}
