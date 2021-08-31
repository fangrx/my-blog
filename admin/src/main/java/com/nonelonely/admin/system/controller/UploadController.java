package com.nonelonely.admin.system.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.email.vo.MailAttachmentVo;
import com.nonelonely.component.fileUpload.FileUpload;
import com.nonelonely.component.fileUpload.enums.UploadResultEnum;
import com.nonelonely.component.fileUpload.util.WaterMarkUtil;
import com.nonelonely.modules.system.domain.Upload;
import com.nonelonely.modules.system.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author nonelonely
 * @date 2018/11/02
 */
@Controller
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @ResponseBody
    public Object uploadFile(@RequestParam("file") MultipartFile multipartFile,@RequestParam("reqType") String reqType) {
        // 创建Upload实体对象
        try {
            if("nk".equals(reqType)){
                Upload upload = FileUpload.getFile(multipartFile, "/"+reqType);
                upload.setName(multipartFile.getOriginalFilename());
                saveFile(multipartFile, upload);
                Map map = new HashMap();
                map.put("code","000");
                map.put("url",upload.getUrl());
                return  map;
            }else if ("sendmail".equals(reqType)){
                Upload upload = FileUpload.getFile(multipartFile, "/"+reqType);
                upload.setName(multipartFile.getOriginalFilename());
                saveFile(multipartFile, upload);
                MailAttachmentVo vo = new MailAttachmentVo();
                vo.setName(multipartFile.getOriginalFilename());
                vo.setSize(upload.getSize());
                vo.setUrl(upload.getPath());
                return  ResultVoUtil.success(vo);
            }else {
                Upload upload = FileUpload.getFile(multipartFile, "/"+reqType);
                upload.setName(multipartFile.getOriginalFilename());
                ResultVo resultVo=saveFile(multipartFile, upload);
                return resultVo;
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            return ResultVoUtil.error("上传图片失败");
        }
    }
    /**
     * 上传文件
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public Object uploadFiles(@RequestParam("file") MultipartFile multipartFile,@RequestParam("path") String path) {
        if(path == null || path.equals("")){
            path = FileUpload.getUploadPath();
        }
        // 创建Upload实体对象
        Upload upload = FileUpload.getFile(multipartFile, "",path);
        try {
            ResultVo  resultVo = saveFile(multipartFile, upload);
            return  resultVo;
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResultVoUtil.error("上传图片失败");
        }
    }

    /**
     * 上传web格式图片
     */
    @PostMapping("/upload/image")
    @ResponseBody
    public ResultVo uploadImage(@RequestParam("image") MultipartFile multipartFile) {

        // 创建Upload实体对象
        Upload upload = FileUpload.getFile(multipartFile, "/images");
        upload.setName(multipartFile.getOriginalFilename());
        try {
            return saveImage(multipartFile, upload,true);
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResultVoUtil.error("上传图片失败");
        }
    }

    /**
     * 上传web格式头像
     */
    @PostMapping("/upload/picture")
    @ResponseBody
    public ResultVo uploadPicture(@RequestParam("picture") MultipartFile multipartFile) {

        // 创建Upload实体对象
        Upload upload = FileUpload.getFile(multipartFile, "/picture");
        try {
            return saveImage(multipartFile, upload,false);
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResultVoUtil.error("上传头像失败");
        }
    }
    /**
     * 上传jar包
     */
    @PostMapping("/upload/jar")
    @ResponseBody
    public ResultVo uploadJar(@RequestParam("jar") MultipartFile multipartFile) {

        // 创建Upload实体对象
        Upload upload = FileUpload.getFile(multipartFile, "", "true");
        try {
            FileUpload.transferToJar(multipartFile, upload);
            return ResultVoUtil.success(upload);
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResultVoUtil.error(e.getMessage());
        }
    }
    /**
     * 保存上传的web格式图片
     */
    private ResultVo saveImage(MultipartFile multipartFile, Upload upload,boolean WaterMark) throws IOException, NoSuchAlgorithmException {
        // 判断是否为支持的图片格式
        String[] types = {
                "image/gif",
                "image/jpg",
                "image/jpeg",
                "image/png"
        };
        if(!FileUpload.isContentType(multipartFile, types)){
            throw new ResultException(UploadResultEnum.NO_FILE_TYPE);
        }

        // 判断图片是否存在
        Upload uploadSha1 = uploadService.getBySha1(FileUpload.getFileSha1(multipartFile));
        if (uploadSha1 != null) {
            return ResultVoUtil.success(uploadSha1);
        }
        if(WaterMark){
            WaterMarkUtil.markImageMultipartFile(null,multipartFile,upload,null);
        }else {
            FileUpload.transferTo(multipartFile, upload);
        }

        // 将文件信息保存到数据库中
        uploadService.save(upload);
        return ResultVoUtil.success(upload);
    }
    /**
     * 保存上传所有文件
     */
    private ResultVo saveFile(MultipartFile multipartFile, Upload upload) throws IOException, NoSuchAlgorithmException {
         String path = upload.getPath().replace("\\","/");
          upload.setPath(path);
        // 判断图片是否存在
       //Upload uploadSha1 = uploadService.getBySha1(FileUpload.getFileSha1(multipartFile),path);
      //  if (uploadSha1 != null) {
     //       return ResultVoUtil.success(uploadSha1);
     //   }
        FileUpload.transferTo(multipartFile, upload);

        // 将文件信息保存到数据库中
        uploadService.save(upload);
        return ResultVoUtil.success(upload);
    }
    @RequestMapping(value = "/upload/editorMD", method = RequestMethod.POST)
    @ResponseBody
    public Object uploadEditorMD(@RequestParam(value = "editormd-image-file") MultipartFile multipartFile) {
        try {


            // 创建Upload实体对象
            Upload upload = FileUpload.getFile(multipartFile, "/images");
            saveImage(multipartFile, upload,false);
            String visitUrl = upload.getPath();
            return MapUtil.of(new Object[][]{
                    {"success", 1}, {"message", "上传成功！"}, {"url", visitUrl}
            });
        } catch (IOException | NoSuchAlgorithmException e) {

            return MapUtil.of("success", 0);
        }
    }
}
