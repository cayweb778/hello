package org.boozsoft.domain.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.invoice.Invoice;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceHeaderVo {


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
	private String fapiaoSaveDir;

	@ApiModelProperty(value = "发票验真状态：1已验真、0未验真", position = 24)
	private String fapiaoCheck;

	@ApiModelProperty(value = "销售方单位名称", position = 30)
	private String sellSupplier;

	@ApiModelProperty(value = "销售方单位税号", position = 31)
	private String sellShuihao;

	@ApiModelProperty(value = "销售方单位地址电话", position = 32)
	private String sellAddrPhone;

	@ApiModelProperty(value = "销售方单位开户行及账号", position = 33)
	private String sellBankAccount;

	@ApiModelProperty(value = "发票类型 1进项 2销项", position = 34)
	private String fpType;

	@ApiModelProperty(value = "发票类型 1电子普通发票 2电子专用发票 3普通发票 4专用发票", position = 35)
	private String ywType;

	@ApiModelProperty(value = "发票种类 1正常 2作废 3冲红 4异常", position = 36)
	private String fpStatus;

	@ApiModelProperty(value = "发票状态  1已审核 2未审核", position = 37)
	private String djStatus;

	@ApiModelProperty(value = "认证状态 1已认证 2未认证", position = 38)
	private String rzStatus;

	@ApiModelProperty(value = "认证人", position = 39)
	private String rzCode;

	@ApiModelProperty(value = "认证日期", position = 40)
	private String rzDate;

	@ApiModelProperty(value = "租户ID", hidden = true)
	private String tenantId;

	@ApiModelProperty(value = "对应凭证", hidden = true)
	private String accuniqueCode;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	@Transient
	private String imgName;
	@Transient
	private List<Invoice> tableData; // 发票表体
	@Transient
	private String csign;
	@Transient
	private String inoid;
}
