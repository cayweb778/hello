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

import java.io.Serializable;

@Data
@Table("stock_boms")
@Accessors(chain = true)
@ApiModel(value = "物料清单子表", description = "物料清单子表")
public class StockBoms implements Serializable {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码", hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "BOM唯一码", hidden = true)
	private String bomUniqueId;
	@ApiModelProperty(value = "工序号", hidden = true)
	private String gxId;
	@ApiModelProperty(value = "子件存货编码", hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "子件存货名称", hidden = true)
	private String cinvName;
	@ApiModelProperty(value = "子件规格型号", hidden = true)
	private String guigexh;
	@ApiModelProperty(value = "子件条形码", hidden = true)
	private String tiaoxinma;
	@ApiModelProperty(value = "子件计量单位", hidden = true)
	private String unitId;
	@ApiModelProperty(value = "子件数量", hidden = true)
	private String isum;
	@ApiModelProperty(value = "子件主计量单位", hidden = true)
	private String unitIdZhu;
	@ApiModelProperty(value = "子件主数量", hidden = true)
	private String isumZhu;
	@ApiModelProperty(value = "子件扩展", hidden = true)
	private String isKuoZhan;
	@ApiModelProperty(value = "是否固定用量", hidden = true)
	private String isGuding;
	@ApiModelProperty(value = "子件损耗率（默认0,大于等于零）", hidden = true)
	private String sunhaoLv;
	@ApiModelProperty(value = "供应类型（默认1领用、2直接供应、3虚拟件、4入库倒冲、5工序倒冲）", hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "默认仓库唯一码", hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "默认领料部门唯一码", hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "默认供应商唯一码", hidden = true)
	private String cvencodeCg;
	@ApiModelProperty(value = "备注", hidden = true)
	private String explain;


	@ApiModelProperty(value = "", hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree5;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree12;


}
