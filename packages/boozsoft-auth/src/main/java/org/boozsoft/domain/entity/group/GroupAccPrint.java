package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账表打印参数设置表", description = "账表打印参数设置表")
@Table("_app_group_acc_print")
@Data
public class GroupAccPrint {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "账套id（一个账套保存一条记录）", hidden = true)
    private String accId;
    @ApiModelProperty(value = "纸张方向（1,横向；2纵向）", hidden = true)
    private String paperFangxiang;
    @ApiModelProperty(value = "菜单英文名称", hidden = true)
    private String menuName;
    @ApiModelProperty(value = "菜单中文名称", hidden = true)
    private String menuChName;
    @ApiModelProperty(value = "左边距", hidden = true)
    private String paperLeft;
    @ApiModelProperty(value = "右边距", hidden = true)
    private String paperRight;
    @ApiModelProperty(value = "上边距", hidden = true)
    private String paperTop;
    @ApiModelProperty(value = "下边距", hidden = true)
    private String paperBottom;
    @ApiModelProperty(value = "自定义参数1", hidden = true)
    private String custom1;
    @ApiModelProperty(value = "自定义参数2", hidden = true)
    private String custom2;
    @ApiModelProperty(value = "自定义参数3", hidden = true)
    private String custom3;
    @ApiModelProperty(value = "自定义参数4", hidden = true)
    private String custom4;
    @ApiModelProperty(value = "自定义参数5", hidden = true)
    private String custom5;
    @ApiModelProperty(value = "自定义参数6", hidden = true)
    private String custom6;
    @ApiModelProperty(value = "自定义参数7", hidden = true)
    private String custom7;
    @ApiModelProperty(value = "自定义参数8", hidden = true)
    private String custom8;
    @ApiModelProperty(value = "自定义参数9", hidden = true)
    private String custom9;
    @ApiModelProperty(value = "自定义参数10", hidden = true)
    private String custom10;

}
