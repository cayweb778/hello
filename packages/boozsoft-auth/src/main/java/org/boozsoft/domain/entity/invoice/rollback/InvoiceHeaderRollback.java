package org.boozsoft.domain.entity.invoice.rollback;


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
@Table ("invoice_header_rollback" )
@ApiModel(value="发票表头回滚表",description="发票表头回滚表")
public class InvoiceHeaderRollback {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "操作时间", position = 1)
	private String operationDate;

	@ApiModelProperty(value = "档案唯一编码", position = 2)
	private String uniqueCode;

	@ApiModelProperty(value = "操作账户唯一码", position = 3)
	private String userUniqueCode;

	@ApiModelProperty(value = "发票数量", position = 4)
	private String fapiaoSum;

	@ApiModelProperty(value = "发票类型表ID", position = 5)
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
	private String fapiaoSaveDir;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	@ApiModelProperty(value = "销售方单位名称", position = 30)
	private String sellSupplier;

	@ApiModelProperty(value = "销售方单位税号", position = 31)
	private String sellShuihao;

	@ApiModelProperty(value = "销售方单位地址电话", position = 32)
	private String sellAddrPhone;

	@ApiModelProperty(value = "销售方单位开户行及账号", position = 33)
	private String sellBankAccount;

	@ApiModelProperty(value = "发票验真状态：1已验真、0未验真", position = 34)
	private String fapiaoCheck;

	@ApiModelProperty(value = "变动日期", position = 35)
	private String biandongDate;

	@ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 36)
	private String biandongMethod;

	@ApiModelProperty(value = "操作员姓名", position = 37)
	private String biandongName;

	@ApiModelProperty(value = "操作员唯一编码", position = 38)
	private String biandongUniqueCode;

	@ApiModelProperty(value = "变动id（原始单据ID）", position = 39)
	private String biandongId;


}
