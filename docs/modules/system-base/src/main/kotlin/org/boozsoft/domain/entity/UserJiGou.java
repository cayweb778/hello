package org.boozsoft.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("sys_user_jigou")
@Data
public class UserJiGou {
  @Id
  public String id;
  public String userId;
  public String jigouId;

}
