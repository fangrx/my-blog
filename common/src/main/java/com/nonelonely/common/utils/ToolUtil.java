package com.nonelonely.common.utils;


import cn.hutool.core.util.StrUtil;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;

import com.nonelonely.common.data.IpInfo;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;



import javax.servlet.http.HttpServletRequest;

import java.io.File;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;


import static java.util.regex.Pattern.*;
import static java.util.regex.Pattern.DOTALL;

/**
 * 通用方法工具类
 * @author nonelonely
 * @date 2018/10/15
 */
public class ToolUtil {
    private static final String UNKNOWN = "unknown";
    /**
     * 获取随机位数的字符串
     * @param length 随机位数
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 获取ascii码中的字符 数字48-57 小写65-90 大写97-122
            int range = random.nextInt(75)+48;
            range = range<97?(range<65?(range>57?114-range:range):(range>90?180-range:range)):range;
            sb.append((char)range);
        }
        return sb.toString();
    }
    /**
     * 描述：截取路径中.后面的文件类型
     *
     * @param src
     * @return
     * @author 杨建全
     * @date 2017年6月7日
     */
    public static String getFileType(String src) {
        String ret = "";
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(src) && src.lastIndexOf(".") != -1) {
            return src.substring(src.lastIndexOf(".") + 1, src.length());
        }
        return ret;
    }
    /**
     * 首字母转小写
     */
    public static String lowerFirst(String word){
        if(Character.isLowerCase(word.charAt(0))) {
            return word;
        } else {
            return String.valueOf(Character.toLowerCase(word.charAt(0))) + word.substring(1);
        }
    }

    /**
     * 首字母转大写
     */
    public static String upperFirst(String word){
        if(Character.isUpperCase(word.charAt(0))) {
            return word;
        } else {
            return String.valueOf(Character.toUpperCase(word.charAt(0))) + word.substring(1);
        }
    }

    /**
     * 获取项目不同模式下的根路径
     */
    public static String getProjectPath(){
        String filePath = ToolUtil.class.getResource("").getPath();
        String projectPath = ToolUtil.class.getClassLoader().getResource("").getPath();
        StringBuilder path = new StringBuilder();

        if(!filePath.startsWith("file:/")){
            // 开发模式下根路径
            char[] filePathArray = filePath.toCharArray();
            char[] projectPathArray = projectPath.toCharArray();
            for (int i = 0; i < filePathArray.length; i++) {
                if(projectPathArray.length > i && filePathArray[i] == projectPathArray[i]){
                    path.append(filePathArray[i]);
                }else {
                    break;
                }
            }
        }else if(!projectPath.startsWith("file:/")){
            // 部署服务器模式下根路径
            projectPath = projectPath.replace("/WEB-INF/classes/", "");
            projectPath = projectPath.replace("/target/classes/", "");
            try {
                path.append(URLDecoder.decode(projectPath,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return projectPath;
            }
        }else {
            // jar包启动模式下根路径
            String property = System.getProperty("java.class.path");
            int firstIndex = property.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = property.lastIndexOf(File.separator) + 1;
            path.append(property, firstIndex, lastIndex);
        }

        File file = new File(path.toString());
        String rootPath = "/";
        try {
            rootPath = URLDecoder.decode(file.getAbsolutePath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootPath.replaceAll("\\\\","/");
    }

    /**
     * 获取文件后缀名
     */
    public static String getFileSuffix(String fileName) {
        if(!fileName.isEmpty()){
            int lastIndexOf = fileName.lastIndexOf(".");
            return fileName.substring(lastIndexOf);
        }
        return "";
    }

    /**
     * 将枚举转成List集合
     * @param enumClass 枚举类
     */
    public static Map<Long, String> enumToMap(Class<?> enumClass){
        Map<Long, String> map = new TreeMap<>();
        try {
        Object[] objects = enumClass.getEnumConstants();
        Method getCode = enumClass.getMethod("getCode");
        Method getMessage = enumClass.getMethod("getMessage");
        for (Object obj : objects) {
            Object iCode = getCode.invoke(obj);
            Object iMessage = getMessage.invoke(obj);
            map.put(Long.valueOf(String.valueOf(iCode)), String.valueOf(iMessage));
        }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return map;
    }

    /**
     * 根据枚举code获取枚举对象
     * @param enumClass 枚举类
     * @param code code值
     */
    public static Object enumCode(Class<?> enumClass, Object code){
        try {
            Object[] objects = enumClass.getEnumConstants();
            Method getCode = enumClass.getMethod("getCode");
            for (Object obj : objects) {
                Object iCode = getCode.invoke(obj);
                if(iCode.equals(code)){
                    return obj;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        }
        return "";
    }

    /**
     * 获取工程的发布路径根目录
     * 即classes的绝对路径
     * file:/E:/idea_workplace/target/classes/
     *
     * @return
     */
    public static String getClassesPath() {
        return Objects.requireNonNull(ToolUtil.class.getClassLoader().getResource("")).getPath();
    }
    /**
     * 获取改文件在工程中所在的完整绝对路径
     *
     * @param filePath 相对classes的路径
     * @return
     */
    public static String getFilePathInClassesPath(String filePath) {
        return getClassesPath() + filePath;
    }

    /**
     * 判断全角的字符串，包括全角汉字以及全角标点
     *
     * @param charSequence
     * @return
     */
    public static int fullAngelWords(CharSequence charSequence) {
        if (charSequence == null) {
            return 0;
        }
        char[] t1 = charSequence.toString().toCharArray();
        int count = 0;
        for (char aT1 : t1) {
            if (Character.toString(aT1).matches("[^\\x00-\\xff]")) {
                System.out.println(aT1);
                count++;
            }
        }
        return count;
    }
    /**
     * 基路径
     *
     * @param request
     * @return
     */
    public static String basePath(HttpServletRequest request) {
        String bathPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        //  log.info("当前域名：[{}]", bathPath);
        return bathPath;
        //        return "http://wuwenbin.me/";
    }
    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤XSS脚本内容
     */
    public static String stripXSS(String value) {
        String rlt = null;

        if (null != value) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            rlt = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = compile("<script>(.*?)</script>", CASE_INSENSITIVE);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid anything in a src='...' type of expression
            /*scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL);
			rlt = scriptPattern.matcher(rlt).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE | Pattern.DOTALL);
			rlt = scriptPattern.matcher(rlt).replaceAll("");*/

            // Remove any lonesome </script> tag
            scriptPattern = compile("</script>", CASE_INSENSITIVE);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = compile("<script(.*?)>", CASE_INSENSITIVE
                    | MULTILINE | DOTALL);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = compile("eval\\((.*?)\\)", CASE_INSENSITIVE
                    | MULTILINE | DOTALL);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = compile("expression\\((.*?)\\)", CASE_INSENSITIVE
                    | MULTILINE | DOTALL);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = compile("javascript:", CASE_INSENSITIVE);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = compile("vbscript:", CASE_INSENSITIVE);
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = compile("onload(.*?)=", CASE_INSENSITIVE
                    | MULTILINE | DOTALL);
            rlt = scriptPattern.matcher(rlt).replaceAll("");
        }

        return rlt;
    }

    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤SQL注入内容
     */
    public static String stripSqlInjection(String value) {
        //value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
        return (null == value) ? null : value.replaceAll("('.+--)|(--)|(%7C)", "");
    }

    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤SQL/XSS注入内容
     */
    public static String stripSqlXSS(String value) {
        return stripXSS(stripSqlInjection(value));
    }

    /**
     * 获取实际ip地址
     *
     * @param request
     * @return
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        String remoteAddress;
        try {
            remoteAddress = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(remoteAddress) || UNKNOWN.equalsIgnoreCase(remoteAddress)) {
                remoteAddress = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(remoteAddress) || remoteAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(remoteAddress)) {
                remoteAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(remoteAddress) || UNKNOWN.equalsIgnoreCase(remoteAddress)) {
                remoteAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(remoteAddress) || UNKNOWN.equalsIgnoreCase(remoteAddress)) {
                remoteAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(remoteAddress) || UNKNOWN.equalsIgnoreCase(remoteAddress)) {
                remoteAddress = request.getRemoteAddr();
            }
        } catch (Exception var3) {
            remoteAddress = request.getRemoteAddr();
        }
        return remoteAddress;
    }

    /**
     * http请求获取ip对象
     *
     * @param ip
     * @return
     */
    public static String getIpInfo(String ip) {
//        String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
//        String resp = HttpUtil.get(url);
//        try {
//            JSONObject jsonObject = JSONUtil.parseObj(resp);
//            return jsonObject.toBean(IpInfo.class);
//        }catch (Exception e){
//            return null;
//        }
        try {
        String url = "http://whois.pconline.com.cn/ipJson.jsp?ip={}";
        url = StrUtil.format(url, ip);
        String resp = HttpUtil.get(url);
        resp =resp.substring(resp.indexOf("{\"ip"),resp.indexOf(");}"));
        JSONObject jsonObj = new JSONObject(resp);
        return "中国-"+jsonObj.get("pro")+"-"+jsonObj.get("city").toString().replace("市","");
        }catch (Exception e){
           return "未知位置";
       }
    }
    /**
     * 判断百度是否收录
     *
     * @param id
     * @return
     */
    public static String getBaiDu(String id,HttpServletRequest request) {
        try {
            String url = "https://www.baidu.com/s?wd=" + basePath(request) + "article/{}";
            url = StrUtil.format(url, id);
            String html = checkBaiduInclude("www.nonelonely.com/article/1584197915527");
            if (!"".equals(html)) {
                Document doc = Jsoup.parse(html);
                Element element = doc.getElementsByClass("content_none").first();
                if (null == element) {
                    return "百度已收录";
                } else {
                    return "百度未收录";

                }
             }
             return "百度未收录";
            }catch(Exception e){
                return "报错啦";
        }
    }


    /**

     * java检测百度是否收录网站url

     * @param siteurl

     * @return

     */

    public static String checkBaiduInclude(String siteurl) {

        String url = "https://www.baidu.com/s?wd="+siteurl;

        try {

            Connection.Response resp = Jsoup.connect(url)

                    .timeout(60000)

                    .method(Connection.Method.GET)

                    .maxBodySize(0)

                    .followRedirects(false)

                    .execute();

            return new String(resp.bodyAsBytes());

        } catch (Exception e) {

            e.printStackTrace();

        }

        return "";

    }


    /**
     * 获取ip地理位置信息
     *
     * @param ipInfo
     * @return
     */
    public static String getIpCnInfo(IpInfo ipInfo) {
        String undefined = "x";
        if (ipInfo == null || ipInfo.getData() == null) {
            return "未知位置";
        }
        boolean isLocal = ipInfo.getData().getIp().contains("127.0.0.1") || ipInfo.getData().getIp().contains("localhost");
        String temp = ipInfo.getData().getCountry() +"-"+ ipInfo.getData().getRegion() + "-"+ipInfo.getData().getCity();
        if (!ipInfo.getData().getCounty().toLowerCase().contains(undefined) && !isLocal) {
            return temp + ipInfo.getData().getCounty();
        } else {
            return temp;
        }
    }
    public static String  detectOS(String sUserAgent) {
        Boolean isWin2K = sUserAgent.indexOf("Windows NT 5.0") > -1 || sUserAgent.indexOf("Windows 2000") > -1;
        if (isWin2K) return "Win2000";
        Boolean isWinXP = sUserAgent.indexOf("Windows NT 5.1") > -1 || sUserAgent.indexOf("Windows XP") > -1;
        if (isWinXP) return "WinXP";
        Boolean isWin2003 = sUserAgent.indexOf("Windows NT 5.2") > -1 || sUserAgent.indexOf("Windows 2003") > -1;
        if (isWin2003) return "Win2003";
        isWin2003 = sUserAgent.indexOf("Windows NT 6.0") > -1 || sUserAgent.indexOf("Windows Vista") > -1;
        if (isWin2003) return "WinVista";
        isWin2003 = sUserAgent.indexOf("Windows NT 6.1") > -1 || sUserAgent.indexOf("Windows 7") > -1;
        if (isWin2003) return "Win7";
        if(sUserAgent.indexOf("Windows NT 7")!=-1){
            return "win7系统";
        }else if(sUserAgent.indexOf("Windows NT 10")!=-1){
            return" win10系统";
        }
        return "None";
    }
    public static Browser  browser (String sUserAgent) {


        //转成UserAgent对象
        UserAgent userAgent = UserAgent.parseUserAgentString(sUserAgent);
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();
        //获取系统信息
        OperatingSystem os = userAgent.getOperatingSystem();
        //系统名称
        String system = os.getName();
        //浏览器名称
        String browserName = browser.getName();
        Version a=browser.getVersion(sUserAgent);
        return browser;//browserName+"("+a.getVersion()+")";
    }

    public static String getHTTP(String urls) throws  Exception{
        URL url = new URL(urls);
        URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(request);

            String result = EntityUtils.toString(response.getEntity());//可以很好的处理中文，保证中文没有乱码
//System.out.println("得到CRM内容:"+result);
            return result;

        } catch (ClientProtocolException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 截取<>中间的字符串，用于获取邮件收件人
     *
     * @param str 待处理的字符串
     * @return
     */
    public static String getByLessThan(String str) {
        String ret = "";
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return ret;
        }
        int start = str.lastIndexOf('<');
        int end = str.lastIndexOf('>');
        if (start != -1 && end != -1) {
            ret = str.substring(start + 1, end);
            return ret;
        }
        return str;
    }
}
