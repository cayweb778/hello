package org.boozsoft.domain.entity.bank.rollback;


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
@Table ("bank_invoice_rollback" )
@ApiModel(value="invoice_rollback: table",description="invoice_rollback: table")
public class BankInvoiceRollback {

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

	@ApiModelProperty(value = "变动日期", position = 10)
	private String biandongDate;

	@ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 11)
	private String biandongMethod;

	@ApiModelProperty(value = "操作员姓名", position = 12)
	private String biandongName;

	@ApiModelProperty(value = "操作员唯一编码", position = 13)
	private String biandongUniqueCode;

	@ApiModelProperty(value = "变动id（原始单据ID）", position = 14)
	private Integer biandongId;


}
