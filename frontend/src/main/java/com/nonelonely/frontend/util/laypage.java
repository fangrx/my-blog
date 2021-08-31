package com.nonelonely.frontend.util;

/**
 * @ProjectName: noteblogv4
 * @Package: me.wuwenbin.noteblogv4.util
 * @ClassName: ${CLASS_NAME}
 * @Author: nonelonely
 * @CreateDate: 2019-01-03 11:17
 * @Version: 1.0
 * @Copyright: Copyright Reserved (c) 2019, http://www.nonelonely.com
 * @Dependency:
 * @Description: java类作用描述
 * -
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2019-01-03 11:17
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
public class laypage {

 public static String getPage(int currentPage,int totalPages,String url){
        String pageStr="";

        pageStr+="<div  class=\"layui-laypage wow rotateInDownLeft\">";

        if(currentPage>0 && totalPages>0 && currentPage<=totalPages){
            if(currentPage==1){
                pageStr+="<a href=\"javascript:;\" class=\"layui-disabled\">上一页</a>";
            }else{
                pageStr+="<a href=\""+url.replace("{page}",String.valueOf(currentPage-1))+"\">上一页</a>";
            }

            if(!(currentPage<=3) && totalPages>5){
                pageStr+="<a href=\""+url.replace("{page}",String.valueOf(1))+"\">首页</a>";
                pageStr+="<span class=\"layui-laypage-spr\">…</span>";
            }

            for(int i=1;i<=totalPages;i++){
                if(Math.abs(currentPage-i)<3 && currentPage==i){
                    pageStr+="<span class=\"layui-laypage-curr\">";
                    pageStr+="<em class=\"layui-laypage-em\" style=\"background-color:#009688;\"></em>";
                    pageStr+="<em>"+currentPage+"</em>";
                    pageStr+="</span>";
                }else if( (currentPage<=3 && i<=5) || ((totalPages-currentPage)<3 && i>(totalPages-5)) || (Math.abs(currentPage-i)<3 && currentPage!=i) ){
                    pageStr+="<a href=\""+url.replace("{page}",String.valueOf(i))+"\">"+i+"</a>";
                }
            }

            if(!((totalPages-currentPage)<3) && totalPages>5){
                pageStr+="<span class=\"layui-laypage-spr\">…</span>";
                pageStr+="<a href=\""+url.replace("{page}",String.valueOf(totalPages))+"\">尾页</a>";
            }

            if(currentPage==totalPages){
                pageStr+="<a href=\"javascript:;\" class=\"layui-disabled\">下一页</a>";
            }else{
                pageStr+="<a href=\""+url.replace("{page}",String.valueOf(currentPage+1))+"\">下一页</a>";
            }
        }

        pageStr+="</div>";

        return pageStr;
    }
}
