package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_sup_price" )
@ApiModel(value="供应商价格表",description="供应商价格表")
public class StockSupPrice {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "供应商唯一码",hidden = true)
	private String supId;
	@ApiModelProperty(value = "存货编码",hidden = true)
	private String stockNum;
	@ApiModelProperty(value = "普通客户价（10小数位）",hidden = true)
	private String memberprice;
	@ApiModelProperty(value = "状态(1停用，0若其他正常）",hidden = true)
	private String status;
	@ApiModelProperty(value = "最新进价",hidden = true)
	private String newPrice;
	@ApiModelProperty(value = "录入人姓名",hidden = true)
	private String operatorName;
	@ApiModelProperty(value = "录入时间",hidden = true)
	private String operatorTime;

	@ApiModelProperty(value = "零售价（10小数位）", position = 4)
	private String regularPrice;

	@ApiModelProperty(value = "1级客户价（10小数位）", position = 6)
	private String invscost1;
	private String invscost2;    //	2级客户价（10小数位）
	private String invscost3;    //	3级客户价（10小数位）
	private String invscost4;    //	4级客户价（10小数位）
	private String invscost5;    //	5级客户价（10小数位）
	private String invscost6;    //	6级客户价（10小数位）
	private String invscost7;    //	7级客户价（10小数位）
	private String invscost8;    //	8级客户价（10小数位）


	@ApiModelProperty(value = "最低价", position = 11)
	private String minPrice;

	@ApiModelProperty(value = "最高价", position = 11)
	private String maxPrice;

	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;

}
