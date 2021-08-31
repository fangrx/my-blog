package com.nonelonely.config.listener;

import cn.hutool.core.map.MapUtil;
import com.nonelonely.common.constant.ParamConst;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.config.GlobalContext;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.domain.permission.NBSysResource;

import com.nonelonely.modules.system.repository.ParamRepository;
import com.nonelonely.modules.system.repository.RoleRepository;
import com.nonelonely.modules.system.repository.permission.ResourceRepository;

import com.nonelonely.modules.system.service.UserService;
import com.nonelonely.modules.system.service.impl.InitService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.*;

import static com.nonelonely.common.constant.ParamConst.INIT_NOT;
import static com.nonelonely.common.constant.ParamConst.INIT_STATUS;


/**
 * 资源监听器
 * 检测{@code @NBAuth}注解的资源存入数据库（默认给网站管理员全部权限，否则无法访问应用）
 * created by Wuwenbin on 2018/7/19 at 22:03
 *
 * @author wuwenbin
 */
@Slf4j
@Component
public class ResourceListener implements ApplicationListener<ContextRefreshedEvent> {


    @Autowired
    private  ParamRepository paramRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GlobalContext context;
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private InitService initService;




    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
             log.info("「nonelonely」App 判断是否首次加载...");
             String init_status =ParamUtil.value("init_status");
             if (init_status == null) {
                 log.info("「nonelonely」App 判断是首次加载...");
                 log.info("「nonelonely」App 正在初始化基本数据内容...");
                 Resource resource = new ClassPathResource("/scripts/schema.sql");
                 initService.initSettings(resource);

             }else{
                 log.info("「nonelonely」App 判断不是首次加载...");
             }
             log.info("「nonelonely」App 正在初始化基本数据内容完成，接下来扫描资源文件...");
            log.info("「nonelonely」App 正在扫描资源中，请稍后...");
            List<Map<String, Object>> resources = new ArrayList<>(50);
            //以防万一，先移除以前的资源
            context.removeApplicationObj("MASTER_RESOURCES");
            Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(Controller.class);
            beans.putAll(event.getApplicationContext().getBeansWithAnnotation(RestController.class));
            int cnt = 0;

            for (Object bean : beans.values()) {
                if (isControllerPresent(bean)) {
                    String[] prefixes = getPrefixUrl(bean);

                    for (String prefix : prefixes) {
                        Method[] methods = AopProxyUtils.ultimateTargetClass(bean).getDeclaredMethods();

                        for (Method method : methods) {
                            String[] lasts = getLastUrl(method);

                            for (String last : lasts) {
                                String url = getCompleteUrl(prefix, last);
                                if (method.isAnnotationPresent(NBAuth.class)) {
                                    RequestMethod[] requestMethods = getRequestMethod(method);
                                    String[] produces = getRequestProduces(method);
                                    log.info("资源：[url = '{}'，请求方式：'{}'，媒体类型：'{}'] 扫描到@NBAuth，准备处理...", url, Arrays.toString(requestMethods), produces);
                                    NBAuth nbAuth = method.getAnnotation(NBAuth.class);
                                    String  permission = isNotEmpty(nbAuth.permission()) ? nbAuth.permission() : nbAuth.value();
                                    NBSysResource.ResType type = nbAuth.type();
                                    Map<String, Object> tempMap = MapUtil.of("permission", permission);
                                    tempMap.put("remark", nbAuth.remark());
                                    tempMap.put("url", url);
                                    tempMap.put("type", type);
                                    tempMap.put("group", nbAuth.group());
                                    resources.add(tempMap);
                                    cnt++;
                                } else {
                                    log.info("资源：[{}] 未扫描到@NBAuth，略过处理步骤", url);
                                }
                            }

                        }

                    }

                }
            }
            log.info("扫描资源完毕，共计处理资源数目：[{}]，等待下一步插入数据库赋给网站管理员角色..", cnt);
           context.setApplicationObj("MASTER_RESOURCES", resources);
        log.info("「nonelonely」App 正在准备角色和用户信息，请稍后...");
        long roleCnt = roleRepository.count();
        //没有初始化角色信息
        if (roleCnt == 0) {
            log.info("「nonelonely」App 正在初始化权限系统，请稍后...");
            setUpAuthority(false);
            log.info("「nonelonely」App 权限系统初始化完毕...");
        } else {
            //已经包含初始化后的角色信息，查出角色名为ROLE_MASTER的对象，没有就抛出异常
            Optional<Role> role = roleRepository.findOne(Example.of(Role.builder().name("admin").build()));
            Optional<Role> userRole = roleRepository.findOne(Example.of(Role.builder().name("ROLE_USER").build()));
            context.setApplicationObj(ParamConst.WEBMASTER_ROLE_ID,
                    role.orElseThrow(() -> new RuntimeException("未找到角色「ROLE_MASTER」")).getId());
            context.setApplicationObj(ParamConst.WEBUSER_ROLE_ID,
                    userRole.orElseThrow(() -> new RuntimeException("未找到角色「ROLE_USER」")).getId());
            setUpAuthority(true);
        }

        log.info("「nonelonely」APP 正在检测初始化状态，请稍后...");
        Param nbParam = paramRepository.findFirstByName(INIT_STATUS);
        if (nbParam == null || StringUtils.isEmpty(nbParam.getValue())) {
            log.info("「nonelonely」APP 未初始化，初始化中，请稍后...");
            setUpAppInitialState();
        }else {
            log.info("「nonelonely」APP 已初始化，开始准备其它内容，请稍后...");
        }
        log.info("「nonelonely」App 启动成功...");
        }


    /**
     * 在参数表中插入一条记录
     * 记录程序是否被初始化过（有没有在初始化界面设置过东西，设置过改为1）
     * 当然，此处是程序第一次启动，当然插入值是未初始化过的：0
     */
    private void setUpAppInitialState() {
        Param initStatus = Param.builder()
                .name(INIT_STATUS)
                .value(INIT_NOT)
                .status(StatusEnum.OK.getCode())
                .remark("标记用户是否在「nonelonely」App 的初始化设置页面设置过")
               // .level(0)
                .build();
        paramRepository.save(initStatus);
    }

    private static boolean isNotEmpty(Object str) {
        return str != null && !"".equals(str);
    }

    /**
     * 是否包含Controller或者RestController
     * 此处的ultimateTargetClass方法是用来获取被spring的cglib代理类的原始类，这样才能获取到类上面的 注解（因为cglib代理类的原理是继承原始的类成成一个子类来操作）
     *
     * @param bean
     * @return
     */
    private static boolean isControllerPresent(Object bean) {
        return AopProxyUtils.ultimateTargetClass(bean).isAnnotationPresent(RestController.class) || AopProxyUtils.ultimateTargetClass(bean).isAnnotationPresent(Controller.class);
    }

    /**
     * 获取Controller上的RequestMapping中的value值，即url的前缀部分
     *
     * @param bean
     * @return
     */
    private static String[] getPrefixUrl(Object bean) {
        String[] prefixes;
        if (AopProxyUtils.ultimateTargetClass(bean).isAnnotationPresent(RequestMapping.class)) {
            prefixes = AopProxyUtils.ultimateTargetClass(bean).getAnnotation(RequestMapping.class).value();
        } else {
            prefixes = new String[]{""};
        }
        return prefixes.length == 0 ? new String[]{""} : prefixes;
    }

    /**
     * 获取Controller上的RequestMapping中的method值，即请求方式
     *
     * @param bean
     * @return
     */
    private static RequestMethod[] getRequestMethod(Object bean) {
        RequestMethod[] prefixes;
        if (AopProxyUtils.ultimateTargetClass(bean).isAnnotationPresent(RequestMapping.class)) {
            prefixes = AopProxyUtils.ultimateTargetClass(bean).getAnnotation(RequestMapping.class).method();
        } else if (bean instanceof Method && ((Method) bean).isAnnotationPresent(RequestMapping.class)) {
            prefixes = ((Method) bean).getAnnotation(RequestMapping.class).method();
        } else {
            prefixes = new RequestMethod[]{};
        }
        return prefixes.length == 0 ? new RequestMethod[]{} : prefixes;
    }

    /**
     * 获取Controller上的RequestMapping中的produces值，即请求类型
     *
     * @param bean
     * @return
     */
    private static String[] getRequestProduces(Object bean) {
        String[] prefixes;
        if (AopProxyUtils.ultimateTargetClass(bean).isAnnotationPresent(RequestMapping.class)) {
            prefixes = AopProxyUtils.ultimateTargetClass(bean).getAnnotation(RequestMapping.class).produces();
        } else if (bean instanceof Method && ((Method) bean).isAnnotationPresent(RequestMapping.class)) {
            prefixes = ((Method) bean).getAnnotation(RequestMapping.class).produces();
        } else {
            prefixes = new String[]{""};
        }
        return prefixes.length == 0 ? new String[]{""} : prefixes;
    }

    /**
     * 获取Controller内的方法上的RequestMapping的value值，即url的后缀部分
     *
     * @param method
     * @return
     */
    private static String[] getLastUrl(Method method) {
        String[] lasts;
        if (method.isAnnotationPresent(RequestMapping.class)) {
            lasts = method.getAnnotation(RequestMapping.class).value();
        } else  if (method.isAnnotationPresent(GetMapping.class)) {
            lasts = method.getAnnotation(GetMapping.class).value();
        }else {
            lasts = new String[]{""};
        }
        return lasts.length == 0 ? new String[]{""} : lasts;
    }

    /**
     * 根据prefix和last获取完整url
     *
     * @param prefix
     * @param last
     * @return
     */
    private static String getCompleteUrl(String prefix, String last) {
        last = last.startsWith("/") ? last : "/".concat(last);
        return (prefix.startsWith("/") ? prefix : "/".concat(prefix)).concat("/".equals(last) ? "" : last).replaceAll("//", "/");
    }

    /**
     * 初始化网站角色信息以及把所有资源权限赋给网站管理员
     * 包含两中角色：管理员和访客
     * 访客需要去后台管理配置权限之后才能访问
     */
    private void setUpAuthority(boolean isRoleInitialed) {
        Role webmaster;
        //如果角色信息没有被初始化
        if (!isRoleInitialed) {
            //插入管理员角色信息
            Role webmasterRole = Role.builder().name("admin").title("网站管理员") .status(StatusEnum.OK.getCode()).menus(new HashSet<>()).build();
            webmaster = roleRepository.saveAndFlush(webmasterRole);
            context.setApplicationObj(ParamConst.WEBMASTER_ROLE_ID, webmaster.getId());
            User user =new User();
            user.setUsername("admin");
            user.setNickname("管理员");
            // 对密码进行加密
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt("123456", salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
            Set<Role> roles = user.getRoles();
            roles.add(webmasterRole);
            userService.save(user);
          //  context.setApplicationObj(ParamConst.WEBMASTER_ROLE_ID, webmaster.getId());
        } else {
            long webmasterRoleId = context.getApplicationObj(ParamConst.WEBMASTER_ROLE_ID);
            webmaster = roleRepository.findById(webmasterRoleId).get();
        }
        //获取扫描到的所有需要验证权限的资源
        List<Map<String, Object>> authResources = context.getApplicationObj("MASTER_RESOURCES");
        if (authResources != null) {
            Set<NBSysResource> menus = new HashSet<>();

            authResources.forEach(res -> {
                String url = res.get("url").toString();
                String name = res.get("remark").toString();
                String permission = res.get("permission").toString();
                NBSysResource.ResType type = (NBSysResource.ResType) res.get("type");
                String group = res.get("group").toString();

                //数据库已存在此url，做更新操作
                if (resourceRepository.countByUrl(url) == 1) {
                    resourceRepository.updateByUrl(name, permission, type, group, url);
                }
                //数据库不存在，做插入操作
                else {
                    NBSysResource resourceInsert = NBSysResource.builder()
                            .permission(permission).name(name).url(url).type(type).group(group).status(StatusEnum.OK.getCode())
                            .build();
                    NBSysResource newRes = resourceRepository.saveAndFlush(resourceInsert);
                    menus.add(newRes);

                }
            });
            webmaster.getMenus().addAll(menus);
            roleRepository.save(webmaster);
        }

        if (!isRoleInitialed) {
            //插入网站普通用户角色信息
            Role normalUser = Role.builder().name("ROLE_USER").title("网站访客") .status(StatusEnum.OK.getCode()).build();
            roleRepository.saveAndFlush(normalUser);
        }
    }

}
