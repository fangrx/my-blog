package com.nonelonely.component.oshi.action;


import com.nonelonely.common.utils.ResultVoUtil;

import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.oshi.model.Server;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-12-07 0:42
 * 个人博客地址：https://www.nonelonely.com
 */
@RequestMapping("/monitor")
@Controller
public class ServerController {
    /**
     * 获取服务器监控信息
     * @param
     * @return com.company.api.base.ResultT<com.company.api.server.Server>
     * @author zengxueqi
     * @since 2020/7/14
     */
    @GetMapping("/server")
    @ResponseBody
    public ResultVo<Server> getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return ResultVoUtil.success(server);
    }


}
