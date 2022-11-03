//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.boozsoft.domain.entity.SysMessage;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ApiModel(
  value = "消息公告对象",
  description = "消息公告对象"
)
@Data
public class SysMessageUserVo extends SysMessage {

  @ApiModelProperty(value = "状态 1已阅0未阅2隐藏(已阅可以隐藏)", position = 4)
  private String ifrag;

  @ApiModelProperty(value = "已阅时间", position = 5)
  private LocalDateTime redtime;

  @ApiModelProperty(value = "发送人", position = 4)
  private String username;

  @ApiModelProperty(value = "标题", position = 4)
  private String title;

  @ApiModelProperty(value = "类型id", position = 4)
  private String tid;

  @ApiModelProperty(value = "类型", position = 4)
  private String typename;
}
