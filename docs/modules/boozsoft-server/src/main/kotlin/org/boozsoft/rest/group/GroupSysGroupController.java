package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysGroup;
import org.boozsoft.repo.group.GroupSysClassIndustryRepository;
import org.boozsoft.repo.group.GroupSysGroupRepository;
import org.boozsoft.util.FtpUtil;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/group/sys-group")
public class GroupSysGroupController {

    @Autowired
    GroupSysGroupRepository groupSysGroupRepository;


    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable) {
        return groupSysGroupRepository.findAll().flatMap(item->{
                    if (StrUtil.isNotBlank(item.getBeiyong1())){
                        item.setBeiyong2(item.getBeiyong1());
                        item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group",item.getBeiyong1()));
                    }
                    return Mono.just(item);
                }).collectList()
                .flatMap(item -> groupSysGroupRepository.countAllBy()
                        .map(total -> R.page(item, pageable, total)));
    }

    @GetMapping("all")
    @ApiOperation(value = "查询所有集团", notes = "传入code")
    public Mono<R> findByAll() {
        return groupSysGroupRepository.findAll().collectList().map(R::ok);
    }

    @Transactional
    @PostMapping("/img") // 导入
    public Mono<R> importImg(@RequestPart("file") FilePart filePartParm,@RequestPart("oldPath") String oldPath) throws Exception {
        String newName = filePartParm.filename().substring(filePartParm.filename().lastIndexOf("."));
        Path tempFilePath = Files.createTempFile("", newName);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> System.out.println("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 存到文件上传记录，方便FTP查找
                .map(item -> {
                    String fileName = "img_" + time + newName;
                    try {
                        String imgPath = "group";
                        File tempFileObject = new File(tempFilePath.toString());
                        FileInputStream in = new FileInputStream(tempFileObject);
                        if (StrUtil.isNotBlank(oldPath) && !oldPath.equals("null")) FtpUtil.delFile("ncpz/group",oldPath);
                        boolean b = FtpUtil.uploadFile(imgPath, fileName, in);
                        if (b)return fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {

                        }
                    }
                    return "";
                }).map(R::ok);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysGroup entity) {
        if (null == entity.getId()) {
            entity.setUniqueCode(IdUtil.objectId());
            entity.setFlag("1");
        }
        entity.setBeiyong2(null);
        return groupSysGroupRepository.save(entity)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysGroup entity) {
        //添加回滚信息，删除项目信息
        return groupSysGroupRepository.deleteById(entity.getId())
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询编码重复", notes = "传入code")
    public Mono findByCode(String id, String groupCode) {
        return groupSysGroupRepository.findFirstByGroupCode(groupCode).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                });
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String id, String groupName) {
        return groupSysGroupRepository.findFirstByGroupName(groupName).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                });
    }
}
