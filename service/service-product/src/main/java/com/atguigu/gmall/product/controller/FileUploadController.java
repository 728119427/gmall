package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utils.FastDfsClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.util.Strings;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传接口
 */
@Api("文件上传接口")
@RestController
@RequestMapping("/admin/product/file")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    @Autowired
    private FastDfsClient fastDfsClient;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws Exception {

   /*     //加载配置文件
        ClassPathResource classPathResource = new ClassPathResource("tracker.properties");
        //初始化fastdfs对象
        ClientGlobal.init(classPathResource.getPath());
        //初始化trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //获取服务连接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //初始化storageClient
        StorageClient storageClient = new StorageClient(trackerServer, null);
        //获取文件拓展名
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        //上传文件
        String[] strings = storageClient.upload_appender_file(file.getBytes(), filenameExtension, null);
        //获取imgUrl
        String imgUrl = fileUrl+strings[0]+"/"+ strings[1];*/

        String path = fastDfsClient.upload(file);
        return Result.ok(fileUrl+path);
    }
}
