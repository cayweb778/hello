package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceItem;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.SingleInvoiceInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.vo.TentcentOCRInfoVO;
import org.boozsoft.domain.vo.TentcentOCRVO;
import org.boozsoft.repo.FtpFileRepository;
import org.boozsoft.repo.SysTaskFileRepository;
import org.boozsoft.repo.SysTaskRepository;
import org.boozsoft.repo.SysTaskUserRepository;
import org.boozsoft.repo.group.GroupSysUserRepository;
import org.boozsoft.service.SysLogService;
import org.boozsoft.util.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * 消息公告
 * @author lz
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
public class SysTaskController {


    @Autowired
    SysLogService sysLogService;
    @Autowired
    SysTaskRepository sysTaskRepository;
    @Autowired
    SysTaskUserRepository sysTaskUserRepository;
    @Autowired
    SysTaskFileRepository sysTaskFileRepository;
    @Autowired
    GroupSysUserRepository groupSysUserRepository;
    @Autowired
    FtpFileRepository ftpFileRepository;
    //查询筛选条件\
    @PostMapping(value = "/findAll")
    @ApiOperation(value = "查询消息列表", notes = "查询消息列表")
    public Mono<R> findAll(@RequestBody Map maps) {
        String type = maps.get("lb").toString();
        String ifrag = maps.get("ifrag").toString();
        String user = maps.get("user").toString();
        Mono<R> map = sysTaskUserRepository.findByUserId(user)
                .collectList()
                .map(flist->{
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    return flist.stream().filter(item -> {

                        if("-1".equals(ifrag)){//全部 不包含隐藏
                            if (item.getIfrag().equals("-1")) {
                                return false;
                            }
                        }else{
                            if (!item.getIfrag().equals(ifrag)) {
                                return false;
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
//                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
//
//                        }
                        return true;
                    }).collect(toList());
                })
                .map(o -> R.ok().setResult(o));

        Mono<R> map1 = sysTaskRepository.findByIfrag(user)
                .collectList()
                .map(flist->{
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    return flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
//                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
//
//                        }
                        return true;
                    }).collect(toList());
                })
                .map(o -> R.ok().setResult(o));
        return Objects.nonNull(type) && "1".equals(type) ? map : map1;
    }

    @PostMapping(value = "/findAllIndex")
    @ApiOperation(value = "首页查询消息列表4条", notes = "首页查询消息列表4条")
    public Mono<R> findAllIndex(@RequestBody Map maps) {
        String user = maps.get("user").toString();
        return sysTaskUserRepository.findByUserIdLimtFour(user)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
    @PostMapping(value = "/findNewMseeage")
    @ApiOperation(value = "新消息数量", notes = "新消息数量")
    public Mono<R> findNewMseeage(@RequestBody Map maps) {
        String user = maps.get("user").toString();
        return sysTaskUserRepository.findNewMseeageCount(user)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping(value = "/hide/{id}/{userId}")
    @ApiOperation(value = "隐藏", notes = "隐藏")
    public Mono<R> hide(@PathVariable String id, @PathVariable String userId) {
        return sysTaskUserRepository.updateById(id, userId)
                .then(sysTaskRepository.updateById(id))
                .thenReturn(R.ok());
    }

    @GetMapping(value = "/chexiao/{id}/{TaskId}")
    @ApiOperation(value = "撤销", notes = "撤销")
    public Mono<R> chexiao(@PathVariable String id, @PathVariable String TaskId) {
        return sysTaskRepository.deleteById(id)
                .then(sysTaskUserRepository.deleteByMessageId(TaskId))
                .thenReturn(R.ok());
    }

    @GetMapping(value = "/read/{id}/{userId}")
    @ApiOperation(value = "已阅", notes = "已阅")
    public Mono<R> read(@PathVariable String id, @PathVariable String userId) {
        return sysTaskUserRepository.updateIfragById(id,userId,LocalDate.now())
                .then(sysTaskRepository.updateIfragById(id))
                .thenReturn(R.ok());
    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "save", notes = "save")
    public Mono<R> save(@RequestBody SysTask sysTask) {

        sysTask.setSendTime(LocalDateTime.now());
        sysTask.setMessageId(UUID.randomUUID().toString());
        sysTask.setIfrag("0");
        ArrayList<String> user = sysTask.getUser();
        List<SysTaskUser> muList = new ArrayList<>();
        user.forEach(v->{
            SysTaskUser s = new SysTaskUser();
            s.setUserId(v);
            s.setMessageId(sysTask.getMessageId());
            s.setIfrag("1");
            muList.add(s);
        });

        ArrayList<FtpFile> file = sysTask.getFile();
        List<SysTaskFile> mfList = new ArrayList<>();
        file.forEach(v->{
            SysTaskFile s = new SysTaskFile();
            s.setFileId(v.getId());
            s.setMessageId(sysTask.getMessageId());
            mfList.add(s);
        });
        return sysTaskRepository.save(sysTask)
                .zipWith(sysTaskUserRepository.saveAll(muList).collectList())
                .zipWith(sysTaskFileRepository.saveAll(mfList).collectList())
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping(value = "/getUserList")
    @ApiOperation(value = "操作人员list", notes = "操作人员list")
    public Mono<R> getUserList() {
        return groupSysUserRepository.findByflag().collectList()
                .map(o-> R.ok().setResult(o));
    }


    @PostMapping("delete")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysTaskRepository.deleteByIds(list)
                .then(sysTaskUserRepository.deleteByIds(list))
                .then(Mono.just(R.ok()));
    }


    @PostMapping("/upload")
    @Transactional
    public Mono<R> upload(@RequestPart("file") FilePart filePartParm) throws IOException {
        Path tempFilePath = Files.createTempFile("", filePartParm.filename());
        String tempFileName = tempFilePath.getFileName().toString();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        String suffix = tempFileName.substring(tempFileName.lastIndexOf("."));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 存到文件上传记录，方便FTP查找
                .map(item -> {
                    int size = 0;

                    FtpFile ftpFile = new FtpFile();
                    try {
                        String imgPath = "task/file";
                        File tempFileObject = new File(tempFilePath.toString());
                        FileInputStream in = new FileInputStream(tempFileObject);
                        size = in.available();
                        FtpUtil.uploadFile(imgPath, "task_"+time + "_" +URLEncoder.encode(filePartParm.filename(), StandardCharsets.UTF_8.displayName()) , in);
                        ftpFile.setUrl("ncpz/" + imgPath);
                        ftpFile.setType(suffix);
                        ftpFile.setSize(String.valueOf(size));
                        ftpFile.setNewName("task_"+time + "_" + filePartParm.filename());
                        ftpFile.setOldName(filePartParm.filename());
                        ftpFile.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {

                        }
                    }
                    return ftpFileRepository.save(ftpFile).then(Mono.just(ftpFile));
                })
                .flatMap(item-> item)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping(value = "/getFileList/{messageId}")
    @ApiOperation(value = "文件list", notes = "文件list")
    public Mono<R> getFileList(@PathVariable String messageId) {
        return ftpFileRepository.findByMessageId(messageId).collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("/download/{fileId}")
    public Mono<Void> download(ServerHttpResponse response,@PathVariable String fileId) throws IOException {
        return ftpFileRepository.findById(fileId).map(item->{
            InputStream inputStream = FtpUtil.downloadFile2(item.getUrl(), item.getNewName());
            //临时存储路径
            File file = new File("C:/temporary/"+item.getNewName());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                copyInputStreamToFile(inputStream, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  file;
        }).flatMap(file ->  {
            ZeroCopyHttpOutputMessage zeroCopyHttpOutputMessage = (ZeroCopyHttpOutputMessage) response;
            try {
                response.getHeaders()
                        .set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(
                                URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.displayName())));
                response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return zeroCopyHttpOutputMessage.writeWith(file, 0, file.length());
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException();
            }finally {
                // 最后删除
                file.delete();
            }
        });
    }

    private Mono<Void> downloadFile(ServerHttpResponse response, File file, String fileName) {
        ZeroCopyHttpOutputMessage zeroCopyHttpOutputMessage = (ZeroCopyHttpOutputMessage) response;
        try {
            response.getHeaders()
                    .set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(
                            URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName())));
            response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return zeroCopyHttpOutputMessage.writeWith(file, 0, file.length());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException();
        } finally {
            // 最后删除
            file.delete();
        }
    }
    // InputStream -> File
    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            // commons-io
            //IOUtils.copy(inputStream, outputStream);

        }

    }
    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
