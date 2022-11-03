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
@Table("stock_jrjc" )
@ApiModel(value="借入借出主表",description="借入借出主表")
public class StockJrjc {
    String id;// 主键
    String tenantId;// 公司唯一码
    String iyear;// 年度标志
    String ccode;// 下游单据类型id （WLSHD物流送货单）
    String ddate;// 本单据类型id （JHZXD拣货装箱单）
    String jrjcUser;// 本单单号
    String jrjcUserId;// 本单单号日期
    String jrjcStyle;// 下游单号
    String cwhcode;// 下游单号日
    String cdepcode;//
    String kgUserid;//
    String bcheck;//
    String bcheckTime;//
    String bcheckUser;//
    String cmaker;//
    String cmakeDate;//
    String cfree1;
    String cfree2;
    String cfree3;
    String cfree4;
    String cfree5;
}
