package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by xyzzg on 2018/6/14.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    //将上传的文件名返回
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名。从后开始查询，查到第一个点那里，往后移一位。
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //使用宇宙无敌UUID确保文件名不重复！
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        //这里因为经常使用，所以打个日志
        logger.info("开始上传文件，文件名是：{}，路径是：{}新文件名：{}",fileName,path,uploadFileName);

        File file1 = new File(path);
        if (!file1.exists()){
            //赋予权限可写
            file1.setWritable(true);
            file1.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经上传成功

            //将targetFile上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
