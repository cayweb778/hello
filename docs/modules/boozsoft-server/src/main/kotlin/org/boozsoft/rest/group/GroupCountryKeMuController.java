package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.SysCountry;
import org.boozsoft.domain.entity.SysZone;
import org.boozsoft.domain.entity.group.GroupAllotRecord;
import org.boozsoft.domain.entity.group.GroupCustomer;
import org.boozsoft.domain.entity.group.GroupCustomerClass;
import org.boozsoft.domain.entity.group.GroupSupplier;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.group.GroupAllotRecordRepository;
import org.boozsoft.repo.group.GroupCodeKemuCountryRepository;
import org.boozsoft.service.GroupCustomerService;
import org.boozsoft.service.SysZoneService;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/sys/country_code")
@Api(value = "集团客户信息", tags = "API系统：集团客户信息")
public class GroupCountryKeMuController {

    @Autowired
    GroupCodeKemuCountryRepository groupCodeKemuCountryRepository;

    @PostMapping("/findAll")
    public Mono<R> findAll(String uniqueAccStandard, String templateId, String cclass) {
        if (cclass.equals("全部")) {
            return groupCodeKemuCountryRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                    .collectList().map(o -> R.ok().setResult(o));
        } else {
            // cclass : 例：1.资产
            return groupCodeKemuCountryRepository.findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(uniqueAccStandard, templateId, cclass.substring(2)).collectList().map(o -> R.ok().setResult(o));
        }
    }


    @PostMapping("/delAllCountryKeMu")
    public Mono delAllCountryKeMu(String uniqueAccStandard, String templateId){
        return groupCodeKemuCountryRepository.delByStandardAndTemplate(uniqueAccStandard,templateId).then(Mono.just(R.ok()));
    }
}
