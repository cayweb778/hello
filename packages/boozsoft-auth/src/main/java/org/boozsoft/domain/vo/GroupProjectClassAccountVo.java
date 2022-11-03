package org.boozsoft.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "GroupProjectClassAccountVo对象", description = "GroupProjectClassAccountVo对象")
public class GroupProjectClassAccountVo {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "分配类型(1,组织;2,账套)", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "分配人", hidden = true)
    private String assignUser;
    @ApiModelProperty(value = "分配人真实姓名", hidden = true)
    private String assignUserName;
    @ApiModelProperty(value = "分配时间", hidden = true)
    private String assignDate;
    @ApiModelProperty(value = "引入状态(0,未引入;1,已引入)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "引入人", hidden = true)
    private String acceptUser;
    @ApiModelProperty(value = "引入人真实姓名", hidden = true)
    private String acceptUserName;
    @ApiModelProperty(value = "引入时间", hidden = true)
    private String acceptDate;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String itemCode;
    @ApiModelProperty(value = "项目分类编码", hidden = true)
    private String projectClassCode;
    @ApiModelProperty(value = "项目分类名称", hidden = true)
    private String projectClassName;
    @ApiModelProperty(value = "上级项目分类ID", hidden = true)
    private String parentId;
    @ApiModelProperty(value = "是否末级（0.不是；1是）", hidden = true)
    private String bend;
    @ApiModelProperty(value = "级次", hidden = true)
    private String jici;
    @ApiModelProperty(value = "所属项目大类编码", hidden = true)
    private String projectCateCode;

}
