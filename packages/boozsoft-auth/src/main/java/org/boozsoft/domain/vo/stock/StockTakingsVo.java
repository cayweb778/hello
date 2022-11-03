package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockCurrentstock;
import org.boozsoft.domain.entity.stock.StockTaking;
import org.boozsoft.domain.entity.stock.StockTakings;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description  盘点单明细
 * @Author  lz 
 * @Date 2022-04-09
 */

@Data
@ApiModel(value="盘点单明细",description="盘点单明细")
public class StockTakingsVo  extends StockTakings implements Serializable {

	@ApiModelProperty(value = "存货编码")
	private String stockNum;
	@ApiModelProperty(value = "存货名称")
	private String stockName;
	@ApiModelProperty(value = "规格型号")
	private String stockGgxh;
	@ApiModelProperty(value = "助记码")
	private String stockZjm;
	@ApiModelProperty(value = "存货分类")
	private String stockClass;
	@ApiModelProperty(value = "条形码")
	private String stockBarcode;

	@ApiModelProperty(value = "计量类型 单/多")
	private String umtype;
	@ApiModelProperty(value = "计量id")
	private String utcode;

	@ApiModelProperty(value = "主计量")
	private String cunitname;
	@ApiModelProperty(value = "计量类型")
	private String utype;
	@ApiModelProperty(value = "多主计量")
	private String cunitnames;
	@ApiModelProperty(value = "计量类型")
	private String utypes;
	@ApiModelProperty(value = "多辅计量一")
	private String fnames1;
	@ApiModelProperty(value = "计量一比例")
	private String rate1;
	@ApiModelProperty(value = "多辅计量二")
	private String fnames2;
	@ApiModelProperty(value = "计量二比例")
	private String rate2;

	@ApiModelProperty(value = "盘点明细id",hidden = true)
	private String pdid;

	@ApiModelProperty(value = "记录的主计量数 （10位小数点）",hidden = true)
	private BigDecimal bq;
	@ApiModelProperty(value = "记录的计量一数 （10位小数点）",hidden = true)
	private BigDecimal sq1;
	@ApiModelProperty(value = "记录的计量二数 （10位小数点）",hidden = true)
	private BigDecimal sq2;

	@ApiModelProperty(value = "是否批次管理 1是")
	private String stockPropertyBatch;

	private String uimtype;
	private String uimid;

	private String sb;
	private String sb1;
	private String sb2;

}
