package org.boozsoft.domain.vo;

import com.tencentcloudapi.ocr.v20181119.models.Rect;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.boozsoft.domain.entity.FtpFile;

import java.util.List;

/**
 * ocr识别字段
 * Created by CodeGenerator on 2020/09/04.
 */
@Data
public class TentcentOCRVO {

    private String billName;                // 发票名称
    private String invoiceCode;             // 发票代码
    private String invoiceNum;              // 发票号码
    private String invoiceCheckCode;        // 校验码
    private String machineId;               // 机器编号
    private String taxNum;                  // 购买方识别号
    private String comName;                 // 购方名称
    private String comHome;                 // 购买方地址、电话
    private String comBank;                 // 购买方开户行及账号

    private String xsComName;               // 销售方供应名称
    private String xsfNum;                  // 销售方识别号
    private String xsfPhone;                // 销售方地址、电话
    private String xsfBankName;             // 销售方开户行
    private String xsfNumber;               // 销售方开户账号

    private String recheck;                 // 复核人
    private String drawer;                  // 开票人
    private String payee;                   // 收款人
    private String comment;                 // 发票备注

    private FtpFile ftpFile;                // ftp文件

    private String invoiceJson;             // 发票JSON
    private String xxje;                    // 发票小写金额
    private String hjje;                    // 发票合计金额
    private String hjse;                    // 发票合计税额

    /* ******************************** 以下是混合票据使用 **************************************** */

    private String xsfId;                // 往来单位唯一码
    private Boolean xsfAdd;              // 往来单位是否添加
    private String strAddress;           // 出发地
    private String endAddress;           // 目的
    private String travelDate;           // 日期
    private String travelName;           // 旅客姓名/姓名
    private String bxMoney;              // 金额 （价税合计、金额）   报销金额
    private String sbMoney;              // 识别金额
    private String xtype;                // 类型 (席别)
    //住宿费用 专票 普票
    private String slMoney;              // 税额
    private String sl;                   // 税率
    private String yjMoney;              // 金额

    //坐标位置 切割用
    private Rect rect;
    //坐标位置 切割用
    private String base64Image;

    public String codeNum;                   //唯一标识 （发票代码+发票号码/火车编号/飞机客运编号）

    private String bxNum;                    // 单号
    private String expNum;                   // 关联 市内|差旅交通  0 1
    private String fileId;                   // 切割后的图片id

    private String invFlgs;                  //1 已验真 2假

    private List<TentcentOCRInfoVO> tableData;

    private String fpType;                   // 1进项 2销项

    private String ywType;                   // 2专票 1普票

    private String fpStatus;

    private String djStatus;

    private String rzStatus;
}
