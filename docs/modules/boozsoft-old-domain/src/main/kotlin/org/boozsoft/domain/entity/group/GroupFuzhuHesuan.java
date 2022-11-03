package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：辅助核算定义", description = "辅助核算定义")
@Table("_app_group_fuzhu_hesuan")
@Data
public class GroupFuzhuHesuan {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "编码", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "名称", hidden = true)
    private String cname;
    @ApiModelProperty(value = "参考对象", hidden = true)
    private String cankaoDuixiang;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "说明", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "参考对象表名", hidden = true)
    private String cankaoDuixiangTable;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;
    @ApiModelProperty(value = "对应凭证表的(cdfine)1-30", hidden = true)
    private Integer cdfine;
    @ApiModelProperty(value = "参考对象key", hidden = true)
    private String cankaoDuixiangKey;
    @ApiModelProperty(value = "参考对象Label", hidden = true)
    private String cankaoDuixiangLabel;
    @ApiModelProperty(value = "参考对象where条件", hidden = true)
    private String cankaoDuixiangWhere;
    @ApiModelProperty(value = "参考对象flag条件", hidden = true)
    private String cankaoDuixiangFlag;
    @ApiModelProperty(value = "参考对象code", hidden = true)
    private String cankaoDuixiangCode;
    @ApiModelProperty(value = "类型（1.系统档案；2.自定义档案）", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "凭证字段", hidden = true)
    private String cfield;
    @ApiModelProperty(value = "是否末级（1.末级；0.标准）", hidden = true)
    private String bend;
}
