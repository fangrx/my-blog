package com.nonelonely.component.fileUpload;

import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.component.fileUpload.config.properties.UploadProjectProperties;
import com.nonelonely.component.fileUpload.enums.UploadResultEnum;
import com.nonelonely.modules.system.domain.Upload;
import com.nonelonely.modules.system.service.UploadService;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传处理工具
 * @author nonelonely
 * @date 2018/11/4
 */
public class FileUpload {

    /**
     * 创建一个Upload实体对象
     * @param multipartFile MultipartFile对象
     * @param modulePath 文件模块路径
     */
    public static Upload getFile(MultipartFile multipartFile, String modulePath){


        return   getFile(multipartFile,modulePath,"");
    }
    /**
     * 创建一个Upload实体对象
     * @param httpUrl MultipartFile对象
     * @param modulePath 文件模块路径
     */
    public static Upload getFile(String httpUrl, String modulePath){
        String fileName = "";
        if(httpUrl.lastIndexOf("/") == -1){
            fileName = httpUrl;
        }else {
            fileName = httpUrl.substring(httpUrl.lastIndexOf("/"));
        }
        Upload upload = new Upload();
        upload.setName(FileUpload.genFileName(fileName));
        upload.setPath(getPathPattern() + modulePath + FileUpload.genDateMkdir("yyyyMMdd") + upload.getName());
        return   upload;
    }

    /**
     * 创建一个Upload实体对象
     * @param multipartFile
     * @param modulePath
     * @param path
     * @return
     */
    public static Upload getFile(MultipartFile multipartFile, String modulePath,String path){
        if (multipartFile.getSize() == 0){
            throw new ResultException(UploadResultEnum.NO_FILE_NULL);
        }
        Upload upload = new Upload();
        upload.setMime(multipartFile.getContentType());
        upload.setSize(multipartFile.getSize());

        if("".equals(path)) {
            upload.setName(FileUpload.genFileName(multipartFile.getOriginalFilename()));
            upload.setPath(getPathPattern() + modulePath + FileUpload.genDateMkdir("yyyyMMdd") + upload.getName());
        }else{
            upload.setName(multipartFile.getOriginalFilename());
            if(path.equals("true")){ //上传jar包用到  特列
                upload.setPath( upload.getName());
            }else {
                upload.setPath(path.replace(ToolUtil.getProjectPath(),"") +"/"  +UUID.randomUUID().toString().replace("-", "")+ upload.getName());
            }
        }
        return upload;
    }
    /**
     * 判断文件是否为支持的格式
     * @param multipartFile MultipartFile对象
     * @param types 支持的文件类型数组
     */
    public static boolean isContentType(MultipartFile multipartFile, String[] types){
        List<String> typeList = Arrays.asList(types);
        return typeList.contains(multipartFile.getContentType());
    }

    /**
     * 获取文件上传保存路径
     */
    public static String getUploadPath(){
        UploadProjectProperties properties = SpringContextUtil.getBean(UploadProjectProperties.class);
        return properties.getFilePath();
    }

    /**
     * 获取文件上传目录的静态资源路径
     */
    public static String getPathPattern(){
        UploadProjectProperties properties = SpringContextUtil.getBean(UploadProjectProperties.class);
        return properties.getStaticPath().replace("/**", "");
    }

    /**
     * 生成随机且唯一的文件名
     */
    public static String genFileName(String originalFilename){
        String fileSuffix = ToolUtil.getFileSuffix(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + fileSuffix;
    }

    /**
     * 生成指定格式的目录名称(日期格式)
     */
    public static String genDateMkdir(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return "/" + sdf.format(new Date()) + "/";
    }

    /**
     * 获取目标文件对象
     * @param upload 上传实体类
     */
    public static File getDestFile(Upload upload) throws IOException {

        // 创建保存文件对象
        String path = upload.getPath().replace(getPathPattern(), "");
        String filePath = getUploadPath() + path;
        File dest = new File(filePath.replace("//", "/"));

        if(!dest.exists()){
            dest.getParentFile().mkdirs();
            dest.createNewFile();
        }

        return dest;
    }
    /**
     * 获取目标文件对象
     * @param upload 上传实体类
     */
    public static File getJarFile(Upload upload) throws IOException {

        // 创建保存文件对象
        String path = upload.getPath().replace(getPathPattern(), "");
        String filePath = ToolUtil.getProjectPath() +"/"+ path;
        File dest = new File(filePath.replace("//", "/"));
        if(!dest.exists()){
            dest.getParentFile().mkdirs();
            dest.createNewFile();
        }

        return dest;
    }
    /**
     * 保存文件及获取文件MD5值和SHA1值
     * @param multipartFile MultipartFile对象
     * @param upload Upload
     */
    public static void transferTo(MultipartFile multipartFile, Upload upload) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = new byte[4096];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        try (OutputStream fos = Files.newOutputStream(getDestFile(upload).toPath()); InputStream fis = multipartFile.getInputStream()) {
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                md5.update(buffer, 0, len);
                sha1.update(buffer, 0, len);
            }
            fos.flush();
        }
        BigInteger md5Bi = new BigInteger(1, md5.digest());
        BigInteger sha1Bi = new BigInteger(1, sha1.digest());
        upload.setMd5(md5Bi.toString(16));
        upload.setSha1(sha1Bi.toString(16));
    }

    /**
     *
     * @param file
     * @param upload
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void transferTo(File file, Upload upload) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = new byte[4096];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        try (OutputStream fos = Files.newOutputStream(file.toPath()); InputStream fis = new FileInputStream(file)) {
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                md5.update(buffer, 0, len);
                sha1.update(buffer, 0, len);
            }
            fos.flush();
        }
        BigInteger md5Bi = new BigInteger(1, md5.digest());
        BigInteger sha1Bi = new BigInteger(1, sha1.digest());
        upload.setMd5(md5Bi.toString(16));
        upload.setSha1(sha1Bi.toString(16));
    }
    /**
     *
     * @param fis
     * @param upload
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void transferTo(InputStream fis , Upload upload) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = new byte[4096];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        try (FileOutputStream fos = new FileOutputStream(getDestFile(upload))) {
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                md5.update(buffer, 0, len);
                sha1.update(buffer, 0, len);
            }
            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        BigInteger md5Bi = new BigInteger(1, md5.digest());
        BigInteger sha1Bi = new BigInteger(1, sha1.digest());
        upload.setMd5(md5Bi.toString(16));
        upload.setSha1(sha1Bi.toString(16));
    }
    /**
     * 保存文件及获取文件MD5值和SHA1值
     * @param multipartFile MultipartFile对象
     * @param upload Upload
     */
    public static void transferToJar(MultipartFile multipartFile, Upload upload) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = new byte[4096];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        try (OutputStream fos = Files.newOutputStream(getJarFile(upload).toPath()); InputStream fis = multipartFile.getInputStream()) {
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                md5.update(buffer, 0, len);
                sha1.update(buffer, 0, len);
            }
            fos.flush();
        }
        BigInteger md5Bi = new BigInteger(1, md5.digest());
        BigInteger sha1Bi = new BigInteger(1, sha1.digest());
        upload.setMd5(md5Bi.toString(16));
        upload.setSha1(sha1Bi.toString(16));
    }
    /**
     * 获取文件的SHA1值
     */
    public static String getFileSha1(MultipartFile multipartFile) {
        if (multipartFile.getSize() == 0){
            throw new ResultException(UploadResultEnum.NO_FILE_NULL);
        }
        byte[] buffer = new byte[4096];
        try (InputStream fis = multipartFile.getInputStream()) {
            return getFileSha1(fis);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件的SHA1值
     */
    public static String getFileSha1(InputStream fis) {

        byte[] buffer = new byte[4096];
        try  {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                sha1.update(buffer, 0, len);
            }
            BigInteger sha1Bi = new BigInteger(1, sha1.digest());
            return sha1Bi.toString(16);
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static Upload saveFile(InputStream is, String name, String modulePath,UploadService uploadService){
        //保存在内存中  多次使用
        ByteArrayOutputStream byteArrayOutputStream = null;
        Upload upload = null;
        try {
            byteArrayOutputStream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len;
            //先将流读取出来 然后再写入到ByteArrayOutputStream中
            while ((len=is.read(buffer))>-1){
                byteArrayOutputStream.write(buffer,0,len);
            }

            // 判断图片是否存在
            upload =uploadService.getBySha1(FileUpload.getFileSha1(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            if (upload == null) {
                upload = FileUpload.getFile(name,modulePath);
                FileUpload.transferTo(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), upload);
                upload.setName(name);
                // 将文件信息保存到数据库中
                uploadService. save(upload);
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return  upload;
    }
}
