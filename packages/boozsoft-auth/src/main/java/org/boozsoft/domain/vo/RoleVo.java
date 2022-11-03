package org.boozsoft.domain.vo;
import lombok.Data;

import java.util.Date;

@Data
public class RoleVo {
  private String id;
  private Integer orderNo;
  private String roleName;
  private String roleValue;
  private Date createTime;
  private String remark;
  private String status;
}
