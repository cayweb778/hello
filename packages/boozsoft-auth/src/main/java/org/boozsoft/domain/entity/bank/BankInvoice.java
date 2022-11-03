package org.boozsoft.domain.entity.bank;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("bank_invoice" )
@ApiModel(value="invoice: table",description="invoice: table")
public class BankInvoice {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
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


}
