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
@Table("stock_jrjcs" )
@ApiModel(value="借入借出子表",description="借入借出子表")
public class StockJrjcs {
    String id;      // 主键
    String tenantId;    //         公司唯一码
    String iyear;       // 年度标志
    String ccode;       //         借入借出单号
    String lineId;      // 行次(从1开始记数并排序）
    String ddate;   //         单据日期
    String cinvode;     //         存货唯一码
    String cwhcode;     //         仓库唯一码
    String cwhcode1;    //         仓库级次一名称
    String cwhcode2;    //         仓库级次二名称
    String cwhcode3;    //         仓库级次三名称
    String cwhcode4;    //         仓库级次四名称
    String cwhcode5;    //         仓库级次五名称
    String cwhcode6;    //         仓库级次六名称
    String batchId;     //         批次号
    String dpdate;      //         生产日期
    String dvdate;   // //         失效日期
    String baseQuantity;   //   //         主数量 （10位小数点）
    String subQuantity1;   //   //         数量1 （10位小数点）
    String subQuantity2;   //   //         数量2 （10位小数点）
    String jrjcFlag;   //
    String jrjcCcode;   //
    String jrjcLineId;   //
    String jrjcQuantity;   //
    String jrjcDate;   //
    String cmemo;   //
    String cfree1;   //
    String cfree2;   //
    String cfree3;   //
    String cfree4;   //
    String cfree5;   //
}



























