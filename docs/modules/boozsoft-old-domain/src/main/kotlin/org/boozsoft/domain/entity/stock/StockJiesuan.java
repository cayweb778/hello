package org.boozsoft.domain.entity.stock;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-08-22 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_jiesuan" )
@ApiModel(value="采购结算单主表",description="")
public class StockJiesuan  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "单据编号（JS-202208-0001）（业务处理日期》=发票到货日期）")
	private String ccode;

	@ApiModelProperty(value = "制单人唯一码（操作员）")
	private String cmaker;

	@ApiModelProperty(value = "单据日期（业务处理日期）")
	private String ddate;

	@ApiModelProperty(value = "单据属性(0:或空蓝字，1:红字)")
	private String bdocumStyle;

	@ApiModelProperty(value = "采购供应商唯一码")
	private String cvencode;

	@ApiModelProperty(value = "采购结算供应商唯一码")
	private String cvencodeJs;

	@ApiModelProperty(value = "业务部门唯一码")
	private String cdepcode;

	@ApiModelProperty(value = "业务员编码唯一码（员工档案）")
	private String cpersoncode;

	@ApiModelProperty(value = "项目唯一码")
	private String citemcode;
	private String ccodeRuku;

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;
}
