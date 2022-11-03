package org.boozsoft.domain.entity;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("stock_print" )
// 单据打印表头模板表]
public class StockPrint {

    String id;          // 主键
    String tenantId;    // 公司唯一码
    String ccode;       // 对应模板唯一码
    String printModel;  // 模板名称
    String iyear;  // 单据类型（CGDHD采购到货单、XHD销货单、........）
    String cstyle;  //单据类型（1系统模板、0自定义模板）系统模板不可更改
    String titleName;  // 表格大标题打印名称（限制字数）
    String titleSize;  // 表格大标题打印字段大小（字号大小）
}
