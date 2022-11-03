package org.boozsoft.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/3/18 18:00 下午
 */
@ApiModel(value = "数据表：行政区划回滚表", description = "行政区划回滚表")
@Table("administrative_zone_roll")
@Data
public class AdministrativeZoneRoll {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "省", hidden = true)
    private String province;
    @ApiModelProperty(value = "市", hidden = true)
    private String city;
    @ApiModelProperty(value = "区", hidden = true)
    private String district;
    @ApiModelProperty(value = "街道", hidden = true)
    private String station;
    @ApiModelProperty(value = "", hidden = true)
    private String population;
    @ApiModelProperty(value = "", hidden = true)
    private String area;
    @ApiModelProperty(value = "", hidden = true)
    private String administrativeDivisionCode;
    @ApiModelProperty(value = "电话区号", hidden = true)
    private String areaCode;
    @ApiModelProperty(value = "邮编", hidden = true)
    private String postcode;
    @ApiModelProperty(value = "", hidden = true)
    private String lng;
    @ApiModelProperty(value = "", hidden = true)
    private String lat;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String changeDate;
    @ApiModelProperty(value = "变动方式（修改/删除/停用）", hidden = true)
    private String changeMode;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String operatorName;
    @ApiModelProperty(value = "操作员唯一码", hidden = true)
    private String operatorUniqueCode;
}
