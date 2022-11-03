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
@Table ( "stock_jhzx_stock" )
@Accessors(chain = true)
@ApiModel(value="拣货装箱单对应存货表",description="拣货装箱单对应存货表")
public class StockJhzxStock {
	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "存货唯一码 ",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "仓库唯一码 ",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "仓库级次一名称 ",hidden = true)
	private String cwhcode1;
	@ApiModelProperty(value = "仓库级次二名称 ",hidden = true)
	private String cwhcode2;
	@ApiModelProperty(value = "仓库级次三名称 ",hidden = true)
	private String cwhcode3;
	@ApiModelProperty(value = "仓库级次四名称 ",hidden = true)
	private String cwhcode4;
	@ApiModelProperty(value = "仓库级次五名称 ",hidden = true)
	private String cwhcode5;
	@ApiModelProperty(value = "仓库级次六名称 ",hidden = true)
	private String cwhcode6;
	@ApiModelProperty(value = "批次号 ",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "生产日期 ",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期 ",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "数量 （10位小数点） ",hidden = true)
	private String quantity;
	@ApiModelProperty(value = "主数量 （10位小数点） ",hidden = true)
	private String baseQuantity;
	@ApiModelProperty(value = "计量单位",hidden = true)
	private String xsUnitId;
	@ApiModelProperty(value = "主计量单位编码",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "备注 ",hidden = true)
	private String cmemo;

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
