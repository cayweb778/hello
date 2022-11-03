package org.boozsoft.domain.entity.invoice;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("invoice" )
@ApiModel(value="invoice: table",description="invoice: table")
public class Invoice {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "商品品名名称", position = 1)
	private String stockName;

	@ApiModelProperty(value = "规格型号", position = 2)
	private String stockModel;

	@ApiModelProperty(value = "数量", position = 3)
	private String stockNum;

	@ApiModelProperty(value = "单位", position = 4)
	private String unit;

	@ApiModelProperty(value = "单价", position = 5)
	private String price;

	@ApiModelProperty(value = "金额", position = 6)
	private String amount;

	@ApiModelProperty(value = "税率", position = 7)
	private String taxRate;

	@ApiModelProperty(value = "税额", position = 8)
	private String taxes;

	@ApiModelProperty(value = "发票表头唯一码", position = 9)
	private String invoiceHeaderUniqueCode;

	@ApiModelProperty(value = "租户ID", hidden = true)
	private String tenantId;

}
