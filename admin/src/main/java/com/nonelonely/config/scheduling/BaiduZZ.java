package com.nonelonely.config.scheduling;


import com.nonelonely.component.scheduledTask.config.ScheduledTaskJob;
import com.nonelonely.modules.system.domain.WebSite;

import com.nonelonely.modules.system.repository.WebSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-13 20:22
 * 个人博客地址：https://www.nonelonely.com
 */
@Component
@Slf4j
public class BaiduZZ implements ScheduledTaskJob {
    private static final String URL="http://data.zz.baidu.com/urls";
    private static final String site="https://www.nonelonely.com";
    private static final String token="MNgcjXSb60TCFV7n";


    @Autowired
    private WebSiteRepository webSiteRepository;

    @Override
    public void run() {
        List<WebSite> list = webSiteRepository.findAllByBaidu(false);
        for(WebSite webSite : list){
            String url = site+"/article/"+webSite.getContentId();
            boolean flag= Post(url);
            webSite.setBaidu(flag);
            webSiteRepository.save(webSite);
        }
    }
    public static Boolean Post(String urlsStr) {
        String result="";
        PrintWriter out=null;
        BufferedReader in=null;
        try {
            //建立URL之间的连接
            URLConnection conn=new URL(URL+"?site="+site+"&token="+token).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host","data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out=new PrintWriter(conn.getOutputStream());

            out.print(urlsStr);
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=in.readLine())!= null){
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求出现异常！"+e);
        } finally{
            try{
                if(out != null){
                    out.close();
                }
                if(in!= null){
                    in.close();
                }

            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        log.info("向百度推送了-----》"+urlsStr+"----->"+result);
        if(result.contains("success")){
            return true;
        }
        return false;
    }

    //Array转String
    public static String urlsArrToString(String []urlsArr) {
        String tempResult="";
        for(int i=0;i<urlsArr.length;i++) {
            if(i==urlsArr.length-1) {
                tempResult+=urlsArr[i].trim();
            }else {
                tempResult+=(urlsArr[i].trim()+"\n");
            }
        }
        return tempResult;
    }
}
