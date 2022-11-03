package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.DataLog;
import org.boozsoft.domain.entity.FtpFile;
import org.boozsoft.domain.entity.invoice.rollback.InvoiceRollback;
import org.boozsoft.domain.entity.invoice.Invoice;
import org.boozsoft.domain.entity.invoice.InvoiceHeader;
import org.boozsoft.domain.entity.invoice.rollback.InvoiceHeaderRollback;
import org.boozsoft.domain.vo.InvoiceHeaderVo;
import org.boozsoft.repo.*;
import org.boozsoft.util.FileHash;
import org.boozsoft.util.FtpUtil;
import org.boozsoft.util.PdfUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 客户分类档案
 */
@Slf4j
@RestController
@RequestMapping("/electronicInvoice")
@Api(value = "电子发票", tags = "API系统：电子发票")
public class InvoiceHeaderController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    InvoiceHeaderRepository invoiceHeaderRepository;
    @Autowired
    InvoiceTypeRepository invoiceTypeRepository;
    @Autowired
    FtpFileRepository ftpFileRepository;
    @Autowired
    SysOcrApiRepository sysOcrApiRepository;
    @Autowired
    SysUsageRepository sysUsageRepository;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    DataLogRepository dataLogRepository;
    @Autowired
    InvoiceRollBackRepository invoiceRollBackRepository;

    /**
     * 上传文件
     *
     * @return
     * @throws FileNotFoundException
     */
    @PostMapping(value = "/uploadInvocie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<R> uploadInvocie(@RequestPart("file") FilePart filePartParm, @RequestParam String accId, @RequestParam String year) throws IOException {
        Path tempFilePath = Files.createTempFile("", filePartParm.filename());
        String tempFileName = tempFilePath.getFileName().toString();
        String suffix = tempFileName.substring(tempFileName.lastIndexOf("."));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        String imgPath = accId + "/" + year;
        imgPath += suffix.indexOf("jpg") > 0 ? "/img" : "/file";
        String imgHash = FileHash.getFileHash(tempFilePath.toString());
        // 根据文件哈希值判断文件是否存在 代孕公司
        String finalImgPath = imgPath;
        return ftpFileRepository.countByHash(imgHash)
                .map(item -> item)
                .flatMap(sum -> {
                    Mono<FtpFile> ftpFileMono = Mono.just(filePartParm)
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
                            // 上传到ftp
                            .map(item -> {
                                FtpFile ftpFile = new FtpFile();
                                int size = 0;
                                try {
                                    File tempFileObject = new File(tempFilePath.toString());
                                    FileInputStream in = new FileInputStream(tempFileObject);
                                    size = in.available();
                                    log.info("文件大小:" + size);

                                    //pdf 转图片 
                                    if(filePartParm.filename().contains(".pdf")){
                                        String newFile = filePartParm.filename().substring(0,filePartParm.filename().lastIndexOf("."));
                                        PdfUtil.pdfToImage(finalImgPath, time + "_" + newFile + ".png", PdfUtil.DEFAULT_DPI,in);
                                        ftpFile.setNewName(time + "_"  + newFile + ".png");
                                    }
                                    String oldName = time + "_" + filePartParm.filename();
                                    FtpUtil.uploadFile(finalImgPath, oldName, in);
                                    ftpFile.setOldName(time + "_" + oldName);
                                    ftpFile.setSize(String.valueOf(size));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    assert tempFilePath != null;
                                    try {
                                        Files.delete(tempFilePath);
                                    } catch (IOException e) {

                                    }
                                }
                                return ftpFile;
                            })
                            // 保存记录到数据库
                            .flatMap(item ->
                                    ftpFileRepository.save(item.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                                            .setType(suffix)
                                            .setHash(imgHash)
                                            .setUrl("/ncpz/" + finalImgPath)).map(o -> o)
                            );
                    return ftpFileMono;
                })
                .flatMap(ietm->{
                    return ftpFileRepository.findByNewName(ietm.getNewName())
                            .map(v->{
                                HashMap map =  new HashMap();
                                map.put("baseUrl", FtpUtil.ImgToBase64(ietm.getUrl(), ietm.getNewName()));
                                map.put("id", v.getId());
                                return map;
                            });
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 发票类型信息
     *
     * @return
     */
    @PostMapping("findAllInvoiceType")
    public Mono<R> findAllInvoiceType() {
        return invoiceTypeRepository.findAll().collectList().map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult("404 - 发票类型信息不存在！"));
    }

    @PostMapping("findAllElectronicInvoice")
    public Mono<R> findAllElectronicInvoice(@RequestBody Map maps) {
        String dw = Objects.isNull(maps.get("dw")) ? "" : maps.get("dw").toString();
        String wb = Objects.isNull(maps.get("wb")) ? "" : maps.get("wb").toString();
        int page = Integer.parseInt(maps.get("page").toString());
        int pageSize = Integer.parseInt(maps.get("size").toString());
        AtomicReference<Integer> totalAR = new AtomicReference();
        return invoiceHeaderRepository.findAllPzList().collectList()
                .map(list -> queryFilter(list, maps))
                .map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR))
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    private List<InvoiceHeaderVo> queryFilter(List<InvoiceHeaderVo> list, Map map) {
        Map<String, Object> variableMap = ((HashMap<String, HashMap<String, Object>>) map.get("condition")).get("variable");
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));

        //凭证唯一码
        List<String> s =  (List<String>) map.get("treeConditon");

        if (list.size() > 0) {
            Map<String, String> invoiceHeader = ((HashMap<String, String>) map.get("invoiceHeader"));

            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(invoiceHeader.get("fapiaoCode")) && !item.getFapiaoCode().contains(invoiceHeader.get("fapiaoCode"))) {
                    return false;
                }
                if (StrUtil.isNotBlank(invoiceHeader.get("ywType")) && !item.getYwType().contains(invoiceHeader.get("ywType"))) {
                    return false;
                }
                if (StrUtil.isNotBlank(invoiceHeader.get("fpType")) && !invoiceHeader.get("fpType").contains(item.getFpType())) {
                    return false;
                }
                if (!"0".equals(invoiceHeader.get("fpStatus")) && !invoiceHeader.get("fpStatus").contains(item.getFpStatus())) {
                    return false;
                }
                if (Objects.nonNull(s)  && !s.contains(item.getUniqueCode())) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

        }
        return list;
    }

    private List<InvoiceHeaderVo> countFilter(List<InvoiceHeaderVo> list, int maxNumber) {
        List<InvoiceHeaderVo> filterList = new ArrayList<>();
        int count = 0;
        String thisOPZNumber = "";
        for (InvoiceHeaderVo accvoucher : list) {
            ++count;
            if (count < (maxNumber)) filterList.add(accvoucher);
        }

        return filterList;
    }

    public static List<InvoiceHeaderVo> splitList(List<InvoiceHeaderVo> list, int pageNo, int pageSize, AtomicReference<Integer> titlesAR) {
        if (CollectionUtils.isEmpty(list)) {
            titlesAR.set(0);
            return new ArrayList<>();
        }
        // 后端排序 总数
        //List<Accvoucher> sortList = ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getInoId()).compareTo(Integer.valueOf(o2.getInoId())));
        int totalCount = list.size();
        titlesAR.set(totalCount);
        pageNo = pageNo - 1;
        int fromIndex = pageNo * pageSize;
        // 分页不能大于总数
        if (fromIndex >= totalCount) {
            return null;
        }
        int toIndex = ((pageNo + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }


    @PostMapping("save")
    public Mono save(@RequestBody InvoiceHeader invoice) {
        String uniqueCode = IdUtil.objectId();
        invoice.setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setUniqueCode(uniqueCode).setUserUniqueCode("user-00001");
        String type = invoice.getId() == null ? "10" : "1";
        String type2 = invoice.getId() == null ? "新增" : "修改";
        return ftpFileRepository.countByOldName(invoice.getImgName())
                // 判断文件是否已存在
                .flatMap(sum -> {
                    // 判断上传ftp文件是否存在
                    Mono<InvoiceHeader> monoEmpty = invoiceHeaderRepository.save(invoice);
                    // 存在；获取图片ID保存
                    Mono<InvoiceHeader> ftpFileMono = Mono.just(invoice)
                            .flatMap(item -> {

                                return ftpFileRepository.findByOldName(invoice.getImgName())
                                        .flatMap(item2 ->{
                                           return  invoiceHeaderRepository.save(
                                                        invoice.setFapiaoQrCode(item2.getId())).map(o -> o);
                                        });
                            });
                    return sum != 0 ? ftpFileMono : monoEmpty;
                })
                // 添加日志
                /*.flatMap(item -> {
                    Calendar date = Calendar.getInstance();
                    String year = String.valueOf(date.get(Calendar.YEAR));
                    return dataLogRepository.save(new DataLog().setUniqueCode("test0001").setUserName("test001").setAccId("bjxgkj001").setIyear(year).setOperationCont("【test0001在"+new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())+"】"+type2+"一张发票。购买方名称【"+item.getBuyerSupplier()+"】、销售方【"+item.getSellSupplier()+"】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod(type))
                            .map(o -> item);
                })*/
                // 添加发票表体
                .flatMapMany(header -> {
                    invoice.getTableData().forEach(v->{
                        v.setInvoiceHeaderUniqueCode(header.getUniqueCode());
                        v.setTenantId(header.getTenantId());
                    });
                    Flux<Invoice> Notempty = invoiceRepository.saveAll(invoice.getTableData());
                    Flux<Invoice> empty = Flux.empty();
                    return invoice.getTableData() == null ? empty : Notempty;
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByImgid")
    public Mono findByImgid(String id) {
        return ftpFileRepository.findById(id)
                .map(item -> {
                    String s = FtpUtil.ImgToBase64(item.getUrl(), item.getNewName());
                    return CollectOfUtils.mapof("name", item.getNewName(), "url", s, "ftpUrl", item.getUrl(), "id", item.getId());
                }).map(o -> R.ok().setResult(o));
    }

    @PostMapping("delByImgid")
    public Mono delByImgid(String id, String ftpName, String ftpUrl) {
        return Mono.just(id)
                .map(idd -> {
                    FtpUtil.delFile(ftpUrl, ftpName);
                    return idd;
                }).flatMap(ftpFileRepository::deleteById).then();
    }

    /**
     * 查询集团OCRAPI接口是否配置
     *
     * @return
     */
    @PostMapping("findAllOcrApi")
    public Mono findAllOcrApi() {
        return sysOcrApiRepository.findAll().collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 查询 OCR识别剩余次数
     *
     * @return
     */
    @PostMapping("findByOcrSurplusNumber")
    public Mono findByOcrSurplusNumber() {
        return sysUsageRepository.findByServerName("票据识别")
                .map(ctx -> {
                    int sum = Integer.valueOf(ctx.getRemainingTimes());
                    if (sum == 0) {
                        return Integer.valueOf("0");
                    }
                    return Integer.valueOf(ctx.getRemainingTimes());
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 获取是否验真状态
     *
     * @return
     */
    @PostMapping("findByCheckFlag")
    public Mono findByCheckFlag() {
        return sysUsageRepository.findByServerName("发票验真").map(item -> R.ok().setResult(item.getFlag()));
    }

    /**
     * 获取发票表体
     */
    @GetMapping("findByHeaderUniqueInvoice")
    public Mono<R> findByHeaderUniqueInvoice(String uniqueCode) {
        return invoiceRepository.findAllByInvoiceHeaderUniqueCode(uniqueCode).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 删除发票表头、发票表体；删除前添加回滚表
     *
     * @param id
     * @return
     */
    @PostMapping("delInvoice")
    public Mono<R> delInvoice(String id) {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return invoiceHeaderRepository.findById(id)
                // 查询发票表头
                .map(invoiceHeader -> {
                    InvoiceHeaderRollback h = new InvoiceHeaderRollback();
                        h.setOperationDate(invoiceHeader.getOperationDate())
                            .setUniqueCode(invoiceHeader.getUniqueCode())
                            .setUserUniqueCode(invoiceHeader.getUserUniqueCode())
                            .setFapiaoSum(invoiceHeader.getFapiaoSum())
                            .setFapiaoType(invoiceHeader.getFapiaoType())
                            .setBuyerSupplier(invoiceHeader.getBuyerSupplier())
                            .setBuyerShuihao(invoiceHeader.getBuyerShuihao())
                            .setBuyerAddrPhone(invoiceHeader.getBuyerAddrPhone())
                            .setBuyerBankAccount(invoiceHeader.getBuyerBankAccount())
                            .setFapiaoDate(invoiceHeader.getFapiaoDate())
                            .setFapiaoCode(invoiceHeader.getFapiaoCode())
                            .setFapiaoNumber(invoiceHeader.getFapiaoNumber())
                            .setFapiaoCheckCode(invoiceHeader.getFapiaoCheckCode())
                            .setMachineCode(invoiceHeader.getMachineCode())
                            .setFapiaoDrawer(invoiceHeader.getFapiaoDrawer())
                            .setFapiaoPayee(invoiceHeader.getFapiaoPayee())
                            .setFapiaoCheckPsn(invoiceHeader.getFapiaoCheckPsn())
                            .setFapiaoContent(invoiceHeader.getFapiaoContent())
                            .setFapiaoMoney(invoiceHeader.getFapiaoMoney())
                            .setFapiaoTaxAmount(invoiceHeader.getFapiaoTaxAmount())
                            .setFapiaoTotalAmount(invoiceHeader.getFapiaoTotalAmount())
                            .setFapiaoRemarks(invoiceHeader.getFapiaoRemarks())
                            .setFapiaoQrCode(invoiceHeader.getFapiaoQrCode())
                            .setFapiaoSaveDir(invoiceHeader.getFapiaoSaveDir())
                            .setFapiaoCheck(invoiceHeader.getFapiaoCheckCode())
                            .setSellSupplier(invoiceHeader.getSellSupplier())
                            .setSellShuihao(invoiceHeader.getSellShuihao())
                            .setSellAddrPhone(invoiceHeader.getSellAddrPhone())
                            .setSellBankAccount(invoiceHeader.getSellBankAccount())
                            .setBiandongId(id)
                            .setBiandongDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setBiandongMethod("2")
                            .setBiandongName("test0001")
                            .setBiandongUniqueCode("test00001");
                        return h;
                })
                // 并删除原数据
                .flatMap(item2->{
                    return invoiceHeaderRepository.deleteById(item2.getBiandongId()).then(Mono.just(item2));
                })
                // 增加删除日志
                .flatMap(item->{
                    return dataLogRepository.save(new DataLog().setUniqueCode("test0001").setUserName("test001").setAccId("bjxgkj001").setIyear(year).setOperationCont("【test0001在"+new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())+"】删除一张发票。购买方名称【"+item.getBuyerSupplier()+"】、销售方【"+item.getSellSupplier()+"】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod("2"))
                            .map(o -> item.getUniqueCode());
                })
                // 查询发票表体
                .flatMap(oldunique->{
                    return invoiceRepository.findAllByInvoiceHeaderUniqueCode(oldunique).collectList()
                            .map(item2->{
                                List<InvoiceRollback> invoiceRollbackList = new ArrayList<>();
                                item2.forEach((Invoice invoice)->{
                                    InvoiceRollback rollback=new InvoiceRollback();
                                    rollback.setStockName(invoice.getStockName())
                                            .setStockModel(invoice.getStockModel())
                                            .setStockNum(invoice.getStockNum())
                                            .setUnit(invoice.getUnit())
                                            .setPrice(invoice.getPrice())
                                            .setAmount(invoice.getAmount())
                                            .setTaxRate(invoice.getTaxRate())
                                            .setTaxes(invoice.getTaxes())
                                            .setInvoiceHeaderUniqueCode(invoice.getInvoiceHeaderUniqueCode())
                                            .setBiandongDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                                            .setBiandongMethod("2")
                                            .setBiandongName("test0001")
                                            .setBiandongUniqueCode("test0001")
                                            .setBiandongId(invoice.getId());
                                    invoiceRollbackList.add(rollback);
                                });
                                return invoiceRepository.deleteAll(item2).then(Mono.just(invoiceRollbackList));
                            });
                })
                .flatMap(list->list)
                .flatMapMany(list->{
                    return invoiceRollBackRepository.saveAll(list);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }


    /**
     *
     * @param id
     * @return
     */
    @PostMapping("dlownloadById")
    public Mono<Void>  dlownloadById(String id, ServerHttpResponse response){

        return ftpFileRepository.findById(id).map(item->{
            InputStream inputStream = FtpUtil.downloadFile2(item.getUrl(), item.getNewName());
            //临时存储路径
            File  file = new File("C:/temporary/"+item.getOldName());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                copyInputStreamToFile(inputStream, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  file;
        }).flatMap(file1 -> {
            ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
            try {
                //截取最后一个_
                String fileName =  file1.getName().substring(file1.getName().lastIndexOf("_")+1, file1.getName().length());
                response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));//输出文件名乱码问题处理
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return zeroCopyResponse.writeWith(file1, 0, file1.length());
        });
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

    @PostMapping("djStatus")
    @ApiOperation(value = "审核", notes = "传入code")
    public Mono djStatus(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return invoiceHeaderRepository.updateDjStatusByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("rzStatus")
    @ApiOperation(value = "认证", notes = "传入code")
    public Mono rzStatus(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return invoiceHeaderRepository.updateRzStatusByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("delByIds")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono delByIds(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return invoiceHeaderRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findNext")
    public Mono findNext(String id,String type){
        // 下一张 上一张
        return "1".equals(type) ? invoiceHeaderRepository.findByNextById(id).defaultIfEmpty(new InvoiceHeader()).map(v->R.ok().setResult(v))
                : invoiceHeaderRepository.findByUpById(id).defaultIfEmpty(new InvoiceHeader()).map(v->R.ok().setResult(v));

    }

    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("dateTree")
    public Mono<R> dateTree(String yearMonth) {
        return accvoucherRepository.findAllVoucherTreeByDbillDateLike(yearMonth + "%").collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("pzAssociationFp")
    public Mono pzAssociationFp(@RequestBody Map maps) {
        String pzId =  maps.get("pzId").toString();
        List<String> list =  (List<String>)maps.get("ids");
        return invoiceHeaderRepository.updateAccuniqueCodeByIds(pzId,list)
                .map(o -> R.ok().setResult(o));
    }


}

