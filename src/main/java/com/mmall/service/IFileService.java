package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by xyzzg on 2018/6/14.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
