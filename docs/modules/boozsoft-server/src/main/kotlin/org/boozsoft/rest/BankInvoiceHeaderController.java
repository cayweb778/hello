package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.DataLog;
import org.boozsoft.domain.entity.FtpFile;
import org.boozsoft.domain.entity.invoice.rollback.InvoiceRollback;
import org.boozsoft.domain.entity.bank.BankInvoice;
import org.boozsoft.domain.entity.bank.BankInvoiceHeader;
import org.boozsoft.domain.entity.bank.rollback.BankInvoiceHeaderRollback;
import org.boozsoft.domain.entity.bank.rollback.BankInvoiceRollback;
import org.boozsoft.domain.entity.invoice.Invoice;
import org.boozsoft.domain.entity.invoice.InvoiceHeader;
import org.boozsoft.domain.entity.invoice.rollback.InvoiceHeaderRollback;
import org.boozsoft.repo.*;
import org.boozsoft.repo.bank.BankInvoiceHeaderRepository;
import org.boozsoft.repo.bank.BankInvoiceHeaderRollBackRepository;
import org.boozsoft.repo.bank.BankInvoiceRepository;
import org.boozsoft.repo.bank.BankInvoiceRollBackRepository;
import org.boozsoft.util.FileHash;
import org.boozsoft.util.FtpUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 银行票据
 */
@Slf4j
@RestController
@RequestMapping("/bankelectronicInvoice")
@Api(value = "银行票据", tags = "API系统：银行票据")
public class BankInvoiceHeaderController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    BankInvoiceHeaderRepository invoiceHeaderRepository;
    @Autowired
    InvoiceTypeRepository invoiceTypeRepository;
    @Autowired
    FtpFileRepository ftpFileRepository;
    @Autowired
    BankInvoiceRepository invoiceRepository;
    @Autowired
    DataLogRepository dataLogRepository;
    @Autowired
    BankInvoiceHeaderRollBackRepository headerRollBackRepository;
    @Autowired
    BankInvoiceRollBackRepository invoiceRollBackRepository;


    /**
     * 上传文件
     *
     * @return
     * @throws FileNotFoundException
     */
    @PostMapping(value = "/uploadInvocie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    
    public Mono<R> uploadInvocie(@RequestPart("file") FilePart filePartParm) throws IOException {
        Path tempFilePath = Files.createTempFile("", filePartParm.filename());
        String tempFileName = tempFilePath.getFileName().toString();
        String suffix = tempFileName.substring(tempFileName.lastIndexOf("."));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        /* 账套名暂时写死，以后动态获取 */
        String imgPath = "bjxgkj-001/" + year;
        imgPath += suffix.indexOf("jpg") > 0 ? "/img" : "/file";
        String imgHash = FileHash.getFileHash(tempFilePath.toString());
        // 根据文件哈希值判断文件是否存在
        String finalImgPath = imgPath;
        return ftpFileRepository.countByHash(imgHash)
                .map(item -> item)
                .flatMap(sum -> {
                    Mono<String> monoEmpty = Mono.just("");
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
                                int size = 0;
                                try {
                                    File tempFileObject = new File(tempFilePath.toString());
                                    FileInputStream in = new FileInputStream(tempFileObject);
                                    size = in.available();
                                    log.info("文件大小:" + size);
                                    FtpUtil.uploadFile(finalImgPath, time + "_" + filePartParm.filename(), in);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    assert tempFilePath != null;
                                    try {
                                        Files.delete(tempFilePath);
                                    } catch (IOException e) {

                                    }
                                }
                                return size;
                            })
                            // 保存记录到数据库
                            .flatMap(item ->
                                    ftpFileRepository.save(new FtpFile().setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                                            .setNewName(time + "_" + filePartParm.filename())
                                            .setOldName(filePartParm.filename())
                                            .setType(suffix)
                                            .setSize(String.valueOf(item))
                                            .setHash(imgHash)
                                            .setUrl("/ncpz/" + finalImgPath)).map(o -> o)
                            );
                    return sum != 0 ? monoEmpty : ftpFileMono;
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
    
    public Mono<R> findAllElectronicInvoice() {
        return invoiceHeaderRepository.findAll().collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    
    public Mono save(@RequestBody BankInvoiceHeader invoice) {
        String uniqueCode = IdUtil.objectId();
        invoice.setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setUniqueCode(uniqueCode).setUserUniqueCode("user-00001");
        String type = invoice.getId() == null ? "10" : "1";
        String type2 = invoice.getId() == null ? "新增" : "修改";
        return ftpFileRepository.countByOldName(invoice.getImgName())
                // 判断文件是否已存在
                .flatMap(sum -> {
                    // 判断上传ftp文件是否存在
                    Mono<BankInvoiceHeader> monoEmpty = invoiceHeaderRepository.save(invoice);
                    // 存在；获取图片ID保存
                    Mono<BankInvoiceHeader> ftpFileMono = Mono.just(invoice)
                            .flatMap(item -> {
                                return ftpFileRepository.findByOldName(invoice.getImgName())
                                        .flatMap(item2 ->
                                                invoiceHeaderRepository.save(invoice.setFapiaoQrCode(item2.getId())).map(o -> o)
                                        );
                            });
                    return sum != 0 ? ftpFileMono : monoEmpty;
                })
                // 回滚表
                .flatMap(invoiceHeader->{
                    BankInvoiceHeaderRollback h = new BankInvoiceHeaderRollback();
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
                            .setBiandongId(invoice.getId()==null?"0":invoice.getId())
                            .setBiandongDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setBiandongMethod("2")
                            .setBiandongName("test0001")
                            .setBiandongUniqueCode("test00001");
                    return headerRollBackRepository.save(h).map(o->invoiceHeader);
                })
                // 添加日志
                .flatMap(item -> {
                    Calendar date = Calendar.getInstance();
                    String year = String.valueOf(date.get(Calendar.YEAR));
                    return dataLogRepository.save(new DataLog().setUniqueCode("test0001").setUserName("test001").setAccId("bjxgkj001").setIyear(year).setOperationCont("【test0001在"+new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())+"】"+type2+"一张发票。购买方名称【"+item.getBuyerSupplier()+"】、销售方【"+item.getSellSupplier()+"】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod(type))
                            .map(o -> item);
                })
                // 添加发票表体
                .flatMapMany(header -> {
                    List<BankInvoice> invoiceList = new ArrayList<>();
                    if (invoice.getTableData() != null) {
                        JSONArray pri_json = JSON.parseArray(invoice.getTableData());
                        for (int i = 0; i < pri_json.size(); i++) {
                            BankInvoice invoice1 = new BankInvoice();
                            JSONObject job = pri_json.getJSONObject(i);
                            invoice1.setInvoiceHeaderUniqueCode(uniqueCode)
                                    .setStockName(job.getString("productName"))
                                    .setStockModel(job.getString("cgGgxh"))
                                    .setStockNum(job.getString("cnCgum"))
                                    .setUnit(job.getString("cgUnit"))
                                    .setPrice(job.getString("cgPrice"))
                                    .setAmount(job.getString("cgMoney"))
                                    .setTaxRate(job.getString("cgGglv"))
                                    .setTaxes(job.getString("cgGgSE"));
                            invoiceList.add(invoice1);
                        }
                    }
                    Flux<BankInvoice> Notempty = invoiceRepository.saveAll(invoiceList);
                    Flux<BankInvoice> empty = Flux.empty();
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
     * 获取发票表体
     */
    @PostMapping("findByHeaderUniqueInvoice")
    
    public Mono<R> findByHeaderUniqueInvoice(String headerunique) {
        return invoiceRepository.findAllByInvoiceHeaderUniqueCode(headerunique).collectList().map(o -> R.ok().setResult(JSON.toJSONString(o)));
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
                    BankInvoiceHeaderRollback h = new BankInvoiceHeaderRollback();
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
                // 回滚表增加
                .flatMap(item->{
                    return headerRollBackRepository.save(item).map(o->item);
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
                                List<BankInvoiceRollback> invoiceRollbackList = new ArrayList<>();
                                item2.forEach((BankInvoice invoice)->{
                                    BankInvoiceRollback rollback=new BankInvoiceRollback();
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
                                            .setBiandongId(Integer.valueOf(String.valueOf(invoice.getId())));
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
     * 删除发票表头、发票表体；删除前添加回滚表
     *
     * @param id
     * @return
     */
    @PostMapping("dlownloadById")
    
    public Mono<Void>  dlownloadById(String id, ServerHttpResponse response){

        return ftpFileRepository.findById(id).map(item->{
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
}

