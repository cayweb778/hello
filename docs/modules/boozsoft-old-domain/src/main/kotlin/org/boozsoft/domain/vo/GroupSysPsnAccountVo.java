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
@ApiModel(value = "GroupSysPsnAccountVo对象", description = "GroupSysPsnAccountVo对象")
public class GroupSysPsnAccountVo {

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
    @ApiModelProperty(value = "人员编码（不允许重复）", hidden = true)
    private String psnCode;
    @ApiModelProperty(value = "人员姓名", hidden = true)
    private String psnName;
    @ApiModelProperty(value = "人员性别", hidden = true)
    private String psnSex;
    @ApiModelProperty(value = "人员所属类别唯一编码", hidden = true)
    private String uniquePsnType;
    @ApiModelProperty(value = "员工属性（内部或外部，默认内部员工）", hidden = true)
    private String psnType;
    @ApiModelProperty(value = "手机号", hidden = true)
    private String cellPhoneNum;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String psnFlag;
    @ApiModelProperty(value = "生效状态（1是；0否）", hidden = true)
    private String successState;

}
