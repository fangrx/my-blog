package com.nonelonely.common.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * @ProjectName: noteblogv4
 * @Package: me.wuwenbin.noteblogv4.config.configuration
 * @ClassName: ${CLASS_NAME}
 * @Author: nonelonely
 * @CreateDate: 2019-01-15 9:46
 * @Version: 1.0
 * @Copyright: Copyright Reserved (c) 2019, http://www.nonelonely.com
 * @Dependency:
 * @Description: java类作用描述
 * -
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2019-01-15 9:46
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
@Configuration
public class WebSocketConfig {
   @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

