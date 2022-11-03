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
 * @date 2021/3/17 15:59 下午
 */
@ApiModel(value = "数据表：行政区划", description = "行政区划")
@Table("administrative_zone")
@Data
public class AdministrativeZone {
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
}
