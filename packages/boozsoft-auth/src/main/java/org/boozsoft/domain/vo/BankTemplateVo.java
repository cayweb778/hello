package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.account.BankTemplate;
import org.boozsoft.domain.entity.account.BankTemplateColumn;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "BankTemplateVo对象", description = "BankTemplateVo对象")
public class BankTemplateVo extends BankTemplate {

    private List<BankTemplateColumn> table;

}
