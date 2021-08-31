package com.nonelonely.component.email.util.parser;


import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.component.fileUpload.FileUpload;
import com.nonelonely.modules.system.domain.Upload;
import com.nonelonely.modules.system.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 邮件HTML图片标签解析器
 *
 * @author YangJianquan
 */
@Component
@Slf4j
public class HtmlParse {

    @Autowired
    UploadService uploadService;

    /**
     * 根据传入参数选着解析方法
     *
     * @param htmlContent
     * @param from
     * @param parser
     * @return
     */
    public String parse(String htmlContent, String from, MimeMessageParser parser) {
        return parseImgSrc(htmlContent, from, parser);
    }

    /**
     * 解析html中的img标签，下载图片，然后将src替换成本地的
     *
     * @param html 待解析的html标签
     * @return
     */
    private String parseImgSrc(String html, String from, MimeMessageParser parser) {
        if (StringUtils.isEmpty(html)) {
            return "";
        }
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img");
        for (Element element : elements) {
            log.info(element.toString());
            String src = element.attr("src");
            if (StringUtils.isEmpty(src)) {
                continue;
            }
            String imagePath = src;
            try {
                if (src.contains("cid:")) {
                    // 图片在附件中
                    String url = parseCidImg(parser, src);
                    element.attr("src", url);
                } else if (src.contains("base64,")) {
                    // base64的url就不解析
                    element.attr("src", imagePath);
                } else {
                    // 非base64的url,非cid的图片
                    String url = saveToCloud(new URL(src).openStream(), ToolUtil.getFileType(src), src);
                    element.attr("src", url);
                }
            } catch (IOException e) {
                log.error("解析邮件中的图片发生异常！", e);
            }
        }
        return doc.toString();
    }

    /**
     * 将图片上传到云，返回url地址
     *
     * @param ins
     * @param name
     * @return
     * @throws IOException
     */
    private String saveToCloud(InputStream ins, String name, String src) throws IOException {
        Upload upload = FileUpload.saveFile(ins, name,"/mail/recmail",uploadService);
        if (upload == null) {
            return src;
        }
        return upload.getPath();
    }

    /**
     * 转换cid对应图片
     *
     * @param parser 邮件解析对象
     * @param src    图片标签src
     * @return
     */
    private String parseCidImg(MimeMessageParser parser, String src) throws IOException {
        String[] cids = src.split(":");
        String cid = "";
        if (null != cids && cids.length == 2) {
            cid = cids[1];
        }
        DataSource ds = parser.findAttachmentByCid(cid);
        if (null == ds) {
            return "";
        }
        return saveToCloud(ds.getInputStream(), ds.getName(), src);
    }

}
