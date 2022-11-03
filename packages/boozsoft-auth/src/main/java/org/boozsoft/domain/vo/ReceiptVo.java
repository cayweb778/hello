package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.account.Receipt;
import org.boozsoft.domain.entity.account.ReceiptHeader;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "ReceiptVo对象", description = "ReceiptVo对象")
public class ReceiptVo extends ReceiptHeader {

    private List<Receipt> table;

}
