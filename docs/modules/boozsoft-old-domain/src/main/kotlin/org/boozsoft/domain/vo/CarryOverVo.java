package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.carryovers.AccCarryOver;

/**
 * @Author Jesse
 * @Description TODO
 * @Date 2021/1/29 上午11:30
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CarryOverVo extends AccCarryOver {

    private String carryOverEntries;       // 分录 内包含 公式分录

    private String carryOverSum;           // 结转金额

    private String carryOverIsShow = "0";  // 是否查看 1 已生成可查看 2未生产不可查看

    private String carryOverShowNum;       // 可查看状态的单号

    private String carryOverModel;         // 生单模型

    private String carryOverTransferInNum; // 临时记录转出科目

}