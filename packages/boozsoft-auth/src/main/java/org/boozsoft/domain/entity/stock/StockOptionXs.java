package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("stock_option_xs")
@Accessors(chain = true)
@ApiModel(value = "销售参数设置表", description = "销售参数设置表")
public class StockOptionXs {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码", hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "销货单审核生成出库单（1是，0否）", hidden = true)
	private String b01;
	@ApiModelProperty(value = "销售退货必须参照销货单（1是，0否）", hidden = true)
	private String b02;
	@ApiModelProperty(value = "出库完成自动关闭订单（1是，0否）", hidden = true)
	private String b03;
	@ApiModelProperty(value = "普通销售必有订单（1是，0否）", hidden = true)
	private String b04;


	@ApiModelProperty(value = "允许超订单生成销货单（1是，0否）", hidden = true)
	private String b05;
	@ApiModelProperty(value = "允许超销货单生成发票（1是，0否）", hidden = true)
	private String b06;
	@ApiModelProperty(value = "控制客户信用（1是，0否）", hidden = true)
	private String b07;
	@ApiModelProperty(value = "控制部门信用（1是，0否）", hidden = true)
	private String b08;

	@ApiModelProperty(value = "可用量包含采购购在途量（1是，0否）", hidden = true)
	private String b09;
	@ApiModelProperty(value = "允许非批次存货超货销货单出库（1是，0否）", hidden = true)
	private String b10;
	@ApiModelProperty(value = "允许批次存货超货销货单出库（1是，0否）", hidden = true)
	private String b11;
	@ApiModelProperty(value = "销货单保存时检查销货可用量（1是，0否）", hidden = true)
	private String b12;
	@ApiModelProperty(value = "是否促销价格表优先（1否，0或空是）", hidden = true)
	private String bcxj;
	@ApiModelProperty(value = "价格表未设置取最近一次售价（1否，1或空是）", hidden = true)
	private String bkh;
	@ApiModelProperty(value = "最低售价控制密码（1否，0或空是）", hidden = true)
	private String pwd;
	@ApiModelProperty(value = "（订单）销货单数量小数位（0-10）", hidden = true)
	private String b13;


	@ApiModelProperty(value = "（订单）销货单单价小数位（0-10）", hidden = true)
	private String b14;
	@ApiModelProperty(value = "（订单）销货单税率小数位（0-10）", hidden = true)
	private String b15;
	@ApiModelProperty(value = "税额单行容差（0.000-1.000）", hidden = true)
	private String b16;
	@ApiModelProperty(value = "税额整单容差（0.000-1.000）", hidden = true)
	private String b17;

	@ApiModelProperty(value = "控制客户信用（1是，0否）", hidden = true)
	private String b18;
	@ApiModelProperty(value = "控制仓库信用（1是，0否）", hidden = true)
	private String b19;
	@ApiModelProperty(value = "控制存货信用（1是，0否）", hidden = true)
	private String b20;
	@ApiModelProperty(value = "控制业务员信用（1是，0否）", hidden = true)
	private String b21;
	@ApiModelProperty(value = "客户价格表优先（1是，0否）", hidden = true)
	private String b22;
	@ApiModelProperty(value = "控制密码内容", hidden = true)
	private String b23;
	private String b24;
	private String b25;
	private String b26;
	private String b27;
	private String b28;
	private String b29;
	private String b30;


}
