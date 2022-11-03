package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "公司：工厂信息", description = "工厂信息实体类")
@Table("_app_group_sys_plant")
@Data
public class GroupSysPlant {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "公司唯一码", hidden = true)

    private String corpUniqueCode;
    @ApiModelProperty(value = "工厂代码（四位数字字母）", hidden = true)

    private String plantCode;
    @ApiModelProperty(value = "工厂全称（允许重复）", hidden = true)

    private String plantNamefull;
    @ApiModelProperty(value = "工厂简称（不允许重复）", hidden = true)

    private String plantNamej;
    @ApiModelProperty(value = "管理类型（1自有，2联营，3代工）", hidden = true)

    private String plantClass;
    @ApiModelProperty(value = "国家（地区）（默认中国）", hidden = true)
    private String countryRegion;
    @ApiModelProperty(value = "城市及邮政编码", hidden = true)
    private String cityPostcode;
    @ApiModelProperty(value = "        工厂注释", hidden = true)
    private String plantNotes;
    @ApiModelProperty(value = "收/发货地址", hidden = true)
    private String addr;
    @ApiModelProperty(value = "        联系人及电话", hidden = true)
    private String contact;
    @ApiModelProperty(value = "全球管理代码（随机生成30位码）", hidden = true)
    private String globalCode;
    @ApiModelProperty(value = "地球经纬度", hidden = true)
    private String longitLatitu;
    @ApiModelProperty(value = "        时区", hidden = true)
    private String timeZone;
    @ApiModelProperty(value = "状态 1可用 0不可用", hidden = true)
    private String status;
    private String beiyong1; // ptf图片路径
    private String beiyong2; // 临时beae64
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;


    @ApiModelProperty(value = "业务删除")
    private String isDel;
    @ApiModelProperty(value = "删除操作员")
    private String delName;
    @ApiModelProperty(value = "删除时间")
    private String delDate;
}
