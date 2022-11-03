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
@Table("stock_print_body" )
@ApiModel(value="借入借出子表",description="借入借出子表")
public class StockPrintBody {
    String id;          // 主键
    String tenantId;    // 公司唯一码
    String ccode;       // 对应模板唯一码
    String printClass;  // 栏目属性（1系统、0自定义、2文本）
    String printName;   // 表体对应字段名称（原表格所有表体字段，文本类型不用读）
    String printNameP;  // 表体字段打印名称
    String isPrint;     // 是否打印（1打印、0不打印）
    String printNum;    // 位置编码（1开始）
    String printDq;     // 对齐方式（L左，B中间，R右）
    String printKd;     // 打印宽度（1——1000）
    String fontSime;    // 字体大小
}
