package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("stock_price")
@Accessors(chain = true)
@ApiModel(value = "存货价格表", description = "存货价格表")
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "公司唯一码", position = 1)
    private String tenantId;

    @ApiModelProperty(value = "存货编码", position = 2)
    private String stockNum;

    @ApiModelProperty(value = "零售价（10小数位）", position = 4)
    private String regularPrice;

    @ApiModelProperty(value = "普通客户价（10小数位）", position = 5)
    private String memberprice;

    @ApiModelProperty(value = "1级客户价（10小数位）", position = 6)
    private String invscost1;
    private String invscost2;    //	2级客户价（10小数位）
    private String invscost3;    //	3级客户价（10小数位）
    private String invscost4;    //	4级客户价（10小数位）
    private String invscost5;    //	5级客户价（10小数位）
    private String invscost6;    //	6级客户价（10小数位）
    private String invscost7;    //	7级客户价（10小数位）
    private String invscost8;    //	8级客户价（10小数位）

    @ApiModelProperty(value = "状态(1停用，0若其他正常）", position = 9)
    private String status;

    @ApiModelProperty(value = "最新进价", position = 10)
    private String newPrice;

    @ApiModelProperty(value = "录入人名称", position = 11)
    private String operatorName;

    @ApiModelProperty(value = "录入时间", position = 12)
    private String operatorTime;

    @ApiModelProperty(value = "最低价", position = 11)
    private String minPrice;

    @ApiModelProperty(value = "最高价", position = 11)
    private String maxPrice;

    private String cfree1;
    private String cfree2;
    private String cfree3;
    private String cfree4;


}
