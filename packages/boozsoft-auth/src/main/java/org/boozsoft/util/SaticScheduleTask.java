package org.boozsoft.util;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockCangku;
import org.boozsoft.domain.entity.stock.StockCangkuLevelName;
import org.boozsoft.repo.stock.StockCangkuLevelNameRepository;
import org.boozsoft.repo.stock.StockCangkuRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class SaticScheduleTask implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //3.添加定时任务
    @Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        //System.err.println();
        //System.err.println("执行静态定时任务时间[5s/次]: " + LocalDateTime.now());

        StockCangkuRepository stockCangkuRepository = (StockCangkuRepository) this.getBean("stockCangkuRepository");
        StockCangkuLevelNameRepository stockCangkuLevelNameRepository = (StockCangkuLevelNameRepository) this.getBean("stockCangkuLevelNameRepository");

        List<StockCangku> list = stockCangkuRepository.findAll().collectList().block();
        for (int i = 0; i < list.size(); i++) {
            //System.out.println(list.get(i).getCkNum() + "==" + list.get(i).getCkName());
        }

        List<StockCangkuLevelName> list2 = stockCangkuLevelNameRepository.findAll().collectList().block();
        for (int i = 0; i < list2.size(); i++) {
            //System.out.println(list2.get(i).getLevelName() + "==" + list2.get(i).getLevelType());
        }

        List<StockCangkuLevelName> list3 = stockCangkuLevelNameRepository.findAll().collectList().block();
        for (int i = 0; i < list3.size(); i++) {
            //System.out.println(list3.get(i).getLevelName() + "==" + list3.get(i).getLevelType());
            for (int j = 0; j < list2.size(); j++) {
                //System.out.print("        " + list2.get(j).getLevelName() + "==" + list2.get(j).getLevelType());
            }
        }
    }
}
