//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@ApiModel(
  value = "user对象",
  description = "user对象"
)
@Data
public class GroupUserVo implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(
    value = "key",
    hidden = true
  )
  private String key;

  @ApiModelProperty(
    value = "人员姓名",
    hidden = true
  )
  private String title;

  @ApiModelProperty(
          value = "是否已选中",
          hidden = true
  )
  private Boolean chosen = false;
}
