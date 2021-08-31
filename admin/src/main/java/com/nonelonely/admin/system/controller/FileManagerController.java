package com.nonelonely.admin.system.controller;

import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.fileUpload.FileUpload;

import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-15 13:56
 * 个人博客地址：https://www.nonelonely.com
 */
@Controller
@RequestMapping("/fileManager")
public class FileManagerController {

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:filemanage:index")
    @NBAuth(value = "system:filemanage:add", remark = "文件管理", type = OTHER, group = ROUTER)

    public String index(Model model){

        return "/system/filemanage/index";
    }
    /**
     * 获取某个文件夹下的所有文件
     *
     *
     * @param path 文件夹的路径
     * @return
     */
    @RequestMapping("/getAllFileName")
    @ResponseBody
    public Map getAllFile(String path,int page,int limit) {
        ArrayList<Map> files = new ArrayList<Map>();
        if(path == null || path.equals("")){
            path = FileUpload.getUploadPath();
        }
        //获取文件
        getAllFileName(path,files);
        //开始下标
        int startIndex = (page - 1) * limit;
        //结束下标 subList()方法不包含结束下标的元素
        int endIndex = page * limit;
        //list总条数
        int total = files.size();
        //总页数
        int pageCount = 0;
        //通过取余决定是否给总页数加1
        int num = total % limit;
        if (num == 0) {
            pageCount = total / limit;
        } else {
            pageCount = total / limit + 1;
        }
        //如果当前页是最后一页的话 要包含集合的最后一条数据，因为sublist方法本身结束的下标是不包含当前元素的
        if (page == pageCount) {
            endIndex = total;
        }
        Map map = new HashMap();
        if(total == 0){
            map.put("data",new ArrayList<>());
        }else {
            map.put("data", files.subList(startIndex, endIndex));
        }
        map.put("count",total);
        return map;
    }
    public List getAllFileName(String path, ArrayList<Map> files) {
        try {

        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList == null){
            return files;
        }
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                Map map = new HashMap();
               // System.out.println("tempList[i].getPath():"+tempList[i].getPath());

                map.put("thumb", tempList[i].getPath().replace(ToolUtil.getProjectPath().replace("/",File.separator),""));
                map.put("name",tempList[i].getName());
                String type = "image";
                map.put("type",tempList[i].getName().substring(tempList[i].getName().lastIndexOf(".")+1));
                map.put("path",tempList[i].getParent());
                files.add(map);
            }
            if (tempList[i].isDirectory()) {
                Map map = new HashMap();
                map.put("thumb","");
                map.put("name",tempList[i].getName());
                map.put("type","directory");
                map.put("path",tempList[i].getPath());
                files.add(map);
             //   getAllFileName(tempList[i].getAbsolutePath(),files);
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        return files;
    }
    /**
     * 获取某个文件夹下的所有文件
     *
     *
     * @param path 文件夹的路径
     * @return
     */
    @RequestMapping("/createFolder")
    @ResponseBody
    public ResultVo createFolder(String path, String folder) {

        if(path == null || path.equals("")){
            path = FileUpload.getUploadPath();
        }

       String  destDirName = path  +"/" +folder;
        File dir = new File(destDirName);
        if (dir.exists()) {
            return  ResultVoUtil.error("创建目录" + destDirName + "失败，目标目录已经存在");
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            return  ResultVoUtil.success("创建目录" + destDirName + "成功！");
        } else {
            return  ResultVoUtil.error("创建目录" + destDirName + "失败！");
        }
    }


}
