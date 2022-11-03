package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 首页面板 科目公式计算实体
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccPanelVo {
    private String ccode;
    private String fuhao;
    private BigDecimal yue;
    private BigDecimal yueMd;
    private BigDecimal yueMc;
    private BigDecimal md;
    private BigDecimal mc;
}
