package org.boozsoft.service;

import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

public interface AccvoucherService {
    Mono<String> queryMaxInoid();
    Mono<List<Accvoucher>> queryAllYaerAndMonthMaxInoid();

    /********************* Mr. Ye *******************/
    Mono<String[]> getThisTemplateTitles(SysAccvoucherTemplate accvoucherTemplate, String type);
    Mono<Tuple2<List<String>, List<String>>> getTheHeaderOfTheCurrentlyImportedFile(String unCode, String type, String type2);
    String[] getThisTemplatePropertys();
    Mono<String[]> checkPingZhengDbRepeatNumber(List<Map<Object,Object>> list);
    Accvoucher modifyPingZhengEntityPropertyByAuxiliaryItem(Accvoucher accvoucher, Object[] objects, List<Integer> keys, List<Integer> indexs);
    Mono<String[]> checkPingZhengYearIsClose(List<Map<Object, Object>> new_listMap);
    Mono<Map<String, Object>> getAllKuaiJiQiJianInfoByAccId(String accId);
    Mono<GroupSysAccount>  queryAccountByAccId(String accId);
    /********************* Mr. Ye *******************/
    Mono editOrDel_AccVoucherOrQc(String ccode, List<Map<String, Object>> listmap, CodeKemu km);
}
