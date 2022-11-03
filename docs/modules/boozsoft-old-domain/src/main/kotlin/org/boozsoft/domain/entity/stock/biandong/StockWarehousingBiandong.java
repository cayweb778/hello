package org.boozsoft.domain.entity.stock.biandong;

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
 * @Date 2022-06-16 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_warehousing_biandong" )
@ApiModel(value="",description="")
public class StockWarehousingBiandong  implements Serializable {


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
	@ApiModelProperty(value = "制单人",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "入库类别（收发方式中的收方向编码）",hidden = true)
	private String bstyle;

	@ApiModelProperty(value = "客户ID",hidden = true)
	private String customerCode;
	@ApiModelProperty(value = "供应商ID",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "结算供应商ID",hidden = true)
	private String cvencodeJs;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "部门编码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "业务员编码",hidden = true)
	private String cpersoncode;

	@ApiModelProperty(value = "库管员编码唯一码（员工档案）",hidden = true)
	private String cwhcodeUser;
	@ApiModelProperty(value = "联系人&电话",hidden = true)
	private String cvencodeContact;
	@ApiModelProperty(value = "送货地址",hidden = true)
	private String deliveryAddress;
	@ApiModelProperty(value = "物流单号",hidden = true)
	private String deliveryId;
	@ApiModelProperty(value = "物流电话",hidden = true)
	private String deliveryTel;
	//

	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）",hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "关闭时间",hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "关闭人",hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "主数量合计",hidden = true)
	private String squantity;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity1;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity2;
	@ApiModelProperty(value = "金额合计",hidden = true)
	private String icost;
	@ApiModelProperty(value = "价税合计",hidden = true)
	private String isum;
	@ApiModelProperty(value = "单据类型id （CGDHD采购到货单、HZHCD红字回冲单、CGRKD采购入库单、DBRKD调拨入库单、XTZHRKD形态转换单、QTRKD其他入库单,PYRKD盘盈入库单,YFD应付单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据属性(0:或空蓝字入库单据，1:红字入库单据)",hidden = true)
	private String bdocumStyle;
	@ApiModelProperty(value = "税额合计",hidden = true)
	private String taxAmount;
	@ApiModelProperty(value = "发票类型（1专用发票、2普通发票、3农副产品发票、4收据）",hidden = true)
	private String invoiceStyle;
	@ApiModelProperty(value = "核销状态（1核销完成，0或其他核销未完成）")
	private String hxFlag;
	@ApiModelProperty(value = "累计核销金额")
	private String hxIsum;
	@ApiModelProperty(value = "税率")
	private String rate;
	@ApiModelProperty(value = "累计入库数量 10位小数")
	private String squantityLj;
	@ApiModelProperty(value = "单位类型:etc其他,cust客户,supplier供应商,user员工,project项目")
	private String unitType;
	@ApiModelProperty(value = "业务单位")
	private String unitValue;

	@ApiModelProperty(value = "成本核算状态 1已核算")
	private String costStatus;
	@ApiModelProperty(value = "成本核算销人")
	private String costCode;
	@ApiModelProperty(value = "成本核算时间")
	private String costDate;
	@ApiModelProperty(value = "红字回冲次数",hidden = true)
	private String hzhcNum;
	@ApiModelProperty(value = "发票代码")
	private String billCode;
	@ApiModelProperty(value = "发票号码")
	private String billNumber;
	@ApiModelProperty(value = "发票日期")
	private String billDate;

	// 是否完成状态 默认0 1完成
	private String rukuStatus;
	private String tuihuoStatus;
	private String kaipiaoStatus;
	private String daohuoStatus;
	private String jiesuanStatus;
	@ApiModelProperty(value = "系统时间",hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "成本回冲单：1是 （月末结账生成的红蓝字单据不允许任何操作）",hidden = true)
	private String chengbenHc;

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
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree12;

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;

	@ApiModelProperty(value = "是否变动，0否1是")
	private String biandong;
	@ApiModelProperty(value = "变动人")
	private String biandongUser;
	@ApiModelProperty(value = "变动时间")
	private String baindongDate;

	@ApiModelProperty(value = "复核状态（1复核，其他未核）",hidden = true)
	private String bworkable;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bworkableTime;
	@ApiModelProperty(value = "审核人",hidden = true)
	private String bworkableUser;
}
