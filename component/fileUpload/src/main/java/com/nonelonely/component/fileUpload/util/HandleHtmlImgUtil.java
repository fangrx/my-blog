package com.nonelonely.component.fileUpload.util;

import com.nonelonely.component.fileUpload.FileUpload;
import com.nonelonely.modules.system.domain.Article;
import com.nonelonely.modules.system.domain.Upload;
import com.nonelonely.modules.system.service.UploadService;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-12 12:14
 * 个人博客地址：https://www.nonelonely.com
 */
public class HandleHtmlImgUtil {

    /**
     * 将html中的图片下载到服务器，并且使用服务器上图片的地址替换图片的网络路径
     * @param
     * @param request
     *
     * @return
     */
    public static void transHtml(Article article, HttpServletRequest request, UploadService uploadService){
        String content = article.getContent();

        String  Md= article.getMdContent() == null ? "":article.getMdContent();
        List<String> imgList = getImgStrList(content,request);
        article.setContent(content.replace("�C","–"));
        for (String imgStr : imgList) {
            try {
                String newUrl = reSaveImage(imgStr,uploadService);
                if (article.getCover() == null ||article.getCover().equals("")) {
                    article.setCover(newUrl);
                }
                content = content.replace(imgStr, newUrl);
                Md = Md.replace(imgStr, newUrl);
                article.setContent(content);
                article.setMdContent(Md);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将指定的网络图片保存到本地指定目录
     * @param httpUrl 图片原来的网络路径
     *
     *
     * @return httpUrl newPath
     */
    private static String reSaveImage(String httpUrl,  UploadService uploadService){

        HttpURLConnection conn = null;
        InputStream inputStream = null;
        ByteArrayOutputStream   byteArrayOutputStream = null;
        Upload uploadSha1 = new Upload();
        try {
            if (!httpUrl.startsWith("http")&&!httpUrl.startsWith("https")){
                httpUrl = "https:"+httpUrl;
            }
            // 建立链接
            URL url=new URL(httpUrl);
            conn=(HttpURLConnection) url.openConnection();
            //以Post方式提交表单，默认get方式
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream=conn.getInputStream();
            //保存在内存中  多次使用
             byteArrayOutputStream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len;
            //先将流读取出来 然后再写入到ByteArrayOutputStream中
            while ((len=inputStream.read(buffer))>-1){
                byteArrayOutputStream.write(buffer,0,len);
            }

            // 判断图片是否存在
             uploadSha1 = uploadService.getBySha1(FileUpload.getFileSha1(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));

            if (uploadSha1 == null) {
                uploadSha1 = FileUpload.getFile(httpUrl, "/article/picture");
                FileUpload.transferTo(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), uploadSha1);
                // 将文件信息保存到数据库中
                uploadService.save(uploadSha1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                inputStream.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return uploadSha1.getPath();
    }

    /**
     * 提取HTML字符串中的img
     * @param htmlStr 要处理的html字符串
     * @return
     */
    private static List<String> getImgStrList(String htmlStr,HttpServletRequest request) {
        List<String> list = new ArrayList<>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = m_image.group();
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                String path = m.group(1);
                if(!path.startsWith(getUrlPrefix(request))&&(path.contains(".com")||path.contains(".net")||path.contains(".cn"))){
                    list.add(handleSrc(path));
                }
            }
        }
        return list;
    }

    /**
     * 去除src路径中的前后引号
     * @param src 图片的src路径
     * @return
     */
    private static String handleSrc(String src) {
        if (src != null) {
            if (src.startsWith("'")|| src.startsWith("\"")) {
                return src.substring(1, src.length());
            }
            if (src.endsWith("'")|| src.endsWith("\"")) {
                return src.substring(0, src.length());
            }
        }


        return src.split("\\?")[0];
    }

    /**
     * 获取网站的URL
     * @param request
     * @return 例如：http://192.168.11.3:8089
     */
    public static String getUrlPrefix(HttpServletRequest request) {
        StringBuffer str = new StringBuffer();
        str.append(request.getScheme());
        str.append("://");
        str.append(request.getServerName());
        if (80 != request.getServerPort()) {
            str.append(":" + request.getServerPort());
        }
        str.append(request.getContextPath());
        return str.toString();
    }




}
