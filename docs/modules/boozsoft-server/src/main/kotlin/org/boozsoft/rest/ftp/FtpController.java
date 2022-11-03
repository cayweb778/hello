package org.boozsoft.rest.ftp;//package org.boozsoft.rest;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.repo.FtpFileRepository;
import org.boozsoft.util.FtpUtil;
import org.springbooz.core.tool.result.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * ftp操作
 */
@Slf4j
@RestController
@RequestMapping("/ftp")
public class FtpController {
    @Autowired
    FtpFileRepository ftpFileRepository;

    /**
     * 关闭弹框删除ftp已上传的文件
     *
     * @return
     */
    @PostMapping(value = "/closePopDelFtp")

    public Mono<R> closePopDelFtp(String filename){
        FtpUtil ftpUtil = new FtpUtil();
        return ftpFileRepository.findByOldName(filename)
                .map(item->{
                    ftpUtil.delFile(item.getUrl(), item.getNewName());
                    return item.getId();
                })
                .flatMap(ftpFileRepository::deleteById)
                .map(o -> R.ok().setResult(o));
    }

}
