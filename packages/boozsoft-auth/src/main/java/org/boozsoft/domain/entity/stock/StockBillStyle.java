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
@Table ( "stock_bill_style" )
@ApiModel(value="单据自定义项样式设置表",description="单据自定义项样式设置表")
public class StockBillStyle {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "数据模块（1.表头;2.表体）",hidden = true)
	private String model;
	@ApiModelProperty(value = "单据类型id （CGDD采购订单、CGDHD采购到货单、XSDD销货单、XHD销货单、CGRKG采购入库单、XSCKD销售出库单、DBRKD调拨入库单、XTZHRKD形态转换单、QTRKD其他入库单、QTCKD其他出库单）",hidden = true)
	private String billModel;
	@ApiModelProperty(value = "单据对应cfree列名称（如cfree1）",hidden = true)
	private String cfreeX;
	@ApiModelProperty(value = "栏目显示名称",hidden = true)
	private String cfreeName;
	@ApiModelProperty(value = "栏目显示位置",hidden = true)
	private String cfreeLocation;
	@ApiModelProperty(value = "是否系统档案（1系统档案，0自定义项）",hidden = true)
	private String brecord;
	@ApiModelProperty(value = "系统档案名称ID(customer客户信息、供应商信息、project项目信息、personal人员信息、department部门信息)",hidden = true)
	private String recordId;
	@ApiModelProperty(value = "系统档案对应字段",hidden = true)
	private String recordName;
	@ApiModelProperty(value = "自定义项编码",hidden = true)
	private String defineCode;
	@ApiModelProperty(value = "档案编码",hidden = true)
	private String deCode;
	@ApiModelProperty(value = "档案名称",hidden = true)
	private String deName;
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
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;

}
