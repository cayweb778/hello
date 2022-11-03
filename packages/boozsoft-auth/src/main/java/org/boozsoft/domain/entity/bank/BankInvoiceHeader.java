package org.boozsoft.domain.entity.bank;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("bank_invoice_header" )
@ApiModel(value="银行票据",description="银行票据")
public class BankInvoiceHeader {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "操作时间", position = 1)
	private String operationDate;

	@ApiModelProperty(value = "档案唯一编码", position = 2)
	private String uniqueCode;

	@ApiModelProperty(value = "操作账户唯一码", position = 3)
	private String userUniqueCode;

	@ApiModelProperty(value = "发票数量", position = 4)
	private String fapiaoSum;

	@ApiModelProperty(value = "发票类型编码(1:普票、2：专票)", position = 5)
	private String fapiaoType;

	@ApiModelProperty(value = "购买方单位名称", position = 6)
	private String buyerSupplier;

	@ApiModelProperty(value = "购买方单位税号", position = 7)
	private String buyerShuihao;

	@ApiModelProperty(value = "购买方单位地址电话", position = 8)
	private String buyerAddrPhone;

	@ApiModelProperty(value = "购买方单位开户行及账号", position = 9)
	private String buyerBankAccount;

	@ApiModelProperty(value = "开票日期", position = 10)
	private String fapiaoDate;

	@ApiModelProperty(value = "发票代码", position = 11)
	private String fapiaoCode;

	@ApiModelProperty(value = "发票号码", position = 12)
	private String fapiaoNumber;

	@ApiModelProperty(value = "校验码", position = 13)
	private String fapiaoCheckCode;

	@ApiModelProperty(value = "机器编号", position = 14)
	private String machineCode;

	@ApiModelProperty(value = "开票人", position = 15)
	private String fapiaoDrawer;

	@ApiModelProperty(value = "复核人", position = 16)
	private String fapiaoCheckPsn;

	@ApiModelProperty(value = "收款人", position = 17)
	private String fapiaoPayee;

	@ApiModelProperty(value = "发票开票内容", position = 18)
	private String fapiaoContent;

	@ApiModelProperty(value = "发票金额", position = 19)
	private String fapiaoMoney;

	@ApiModelProperty(value = "发票税额", position = 20)
	private String fapiaoTaxAmount;

	@ApiModelProperty(value = "发票价税合计", position = 21)
	private String fapiaoTotalAmount;

	@ApiModelProperty(value = "发票备注", position = 22)
	private String fapiaoRemarks;

	@ApiModelProperty(value = "发票二维码图片ID", position = 23)
	private String fapiaoQrCode;

	@ApiModelProperty(value = "发票PDF保存路径ID", position = 24)
	private Integer fapiaoSaveDir;

	@ApiModelProperty(value = "发票验真状态：1已验真、0未验真", position = 24)
	private Integer fapiaoCheck;

	@ApiModelProperty(value = "销售方单位名称", position = 30)
	private String sellSupplier;

	@ApiModelProperty(value = "销售方单位税号", position = 31)
	private String sellShuihao;

	@ApiModelProperty(value = "销售方单位地址电话", position = 32)
	private String sellAddrPhone;

	@ApiModelProperty(value = "销售方单位开户行及账号", position = 33)
	private String sellBankAccount;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;


	@Transient
	private String imgName;
	@Transient
	private String tableData; // 发票表体
}
