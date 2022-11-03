package org.boozsoft.rest.group;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysLoginLog;
import org.boozsoft.repo.group.GroupSysLoginLogRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group/sysLoginLog")
public class GroupSysLoginLogController {

    @Autowired
    GroupSysLoginLogRepository repository;

    @PostMapping("saveLog")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> saveLog(@RequestBody GroupSysLoginLog object) {
        //操作时间
        if (object.getLoginTime() == null || object.getLoginTime().equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            object.setLoginTime(sdf.format(new Date()));
        }
        return repository.save(object).map(o -> R.ok().setResult(o));
    }


    @PostMapping("saveAllLog")
    public Mono<R> saveAllLog(@RequestBody List<GroupSysLoginLog> list) {
        list.forEach(object -> {
            //操作时间
            if (object.getLoginTime() == null || object.getLoginTime().equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                object.setLoginTime(sdf.format(new Date()));
            }
        });
        return repository.saveAll(list).collectList().map(o -> R.ok().setResult(o));
    }


    @PostMapping("/condition")
    @ApiOperation(value = "条件查询列表", notes = "传入code")
    public Mono<R> condition(@RequestBody Map map) {
        String theDate = null;
        String theDate2 = null;
        if ( null == map.get("dates")) {
            theDate = DateUtil.today();
            theDate2 = DateUtil.offsetDay(new Date(),-7).toDateStr();
        } else {
            List<String> dates = (List<String>) map.get("dates");
            theDate =dates.get(1);
            theDate2 =dates.get(0);
        }
        String key = map.get("key").toString();
        String account = map.get("account").toString();
        Pageable pageable = PageRequest.of(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("size").toString()));
        String finalTheDate = theDate;
        String finalTheDate2 = theDate2;
        int startIndex = (pageable.getPageNumber()-1) * pageable.getPageSize();
        return (StrUtil.equals("all",account)?
                        repository.findAllByLoginTimeBetweenOrderByLoginTimeDesc(theDate2,theDate,key,startIndex,pageable.getPageSize()).collectList().zipWith(repository.countAllByAccIdAndLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(key,finalTheDate2,finalTheDate)):
                        repository.findAllByUserIdAndLoginTimeBetweenOrderByLoginTimeDesc(theDate2,theDate,key,account,startIndex,pageable.getPageSize()).collectList().zipWith( repository.countAllByAccIdAndUserIdAndLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(key,account,finalTheDate2,finalTheDate))
                ).map(t -> R.page(t.getT1(), t.getT2()));
    }

    @PostMapping("/conditionOld")
    @ApiOperation(value = "条件查询列表", notes = "传入code")
    public Mono<R> conditionOld(@RequestBody Map map) {
        String theDate = null;
        if (map.keySet().size() == 2 || StrUtil.isBlank(map.get("date").toString())) {
            theDate = DateUtil.today();
        } else {
            theDate = map.get("date").toString();
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("size").toString()));
        String finalTheDate = theDate;
        int startIndex = (pageable.getPageNumber()-1) * pageable.getPageSize();
        return repository.findAllByLoginTimeLikeOrderByLoginTimeDesc(theDate+"%",startIndex,pageable.getPageSize()).collectList().flatMap(o -> repository.countAllByLoginTimeLike(finalTheDate + "%").map(t -> R.page(o, t)));
    }
    @PostMapping("/tree")
    @ApiOperation(value = "查询指定月已存在的天", notes = "传入code")
    public Mono<R> tree(@RequestBody Map map) {
        String ym = map.get("date").toString()+"%";
        return repository.findDistinctByLoginTimeLikeOrderByLoginTime(ym).collectList().map(R::ok);
    }
}
