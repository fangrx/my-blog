package com.nonelonely.component.shiro;

import com.nonelonely.common.constant.AdminConst;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.domain.permission.NBSysResource;
import com.nonelonely.modules.system.repository.UserRepository;
import com.nonelonely.modules.system.repository.permission.ResourceRepository;
import com.nonelonely.modules.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;


import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;


/**
 * @author nonelonely
 * @date 2018/8/14
 */
public class AuthRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 获取用户Principal对象
        User user = (User) principal.getPrimaryPrincipal();

        // 管理员拥有所有权限
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            info.addRole(AdminConst.ADMIN_ROLE_NAME);
            info.addStringPermission("*:*:*");
            return info;
        }

        // 赋予角色和资源授权
        Set<Role> roles = ShiroUtil.getSubjectRoles();
        roles.forEach(role -> {
            info.addRole(role.getName());

            role.getMenus().forEach(resource -> {
                    info.addStringPermission(resource.getPermission());

            });
        });

        return info;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UserPassOpenIdToken token = (UserPassOpenIdToken) authenticationToken;

        String type = token.getLoginType();
        User user =null;
        if ("1".equals(type)) {
            user = userRepository.findByQqOpenId(token.getUsername());
        }else{
            user = userService.getByName(token.getUsername());
        }
            // 判断用户名是否存在
            if (user == null) {
                throw new UnknownAccountException();
            } else if (user.getStatus().equals(StatusEnum.FREEZED.getCode())) {
                throw new LockedAccountException();
            }
            // 对盐进行加密处理
            ByteSource salt = ByteSource.Util.bytes(user.getSalt());

            /* 传入密码自动判断是否正确
             * 参数1：传入对象给Principal
             * 参数2：正确的用户密码
             * 参数3：加盐处理
             * 参数4：固定写法
             */
            return new SimpleAuthenticationInfo(user, user.getPassword(), salt, getName());

    }

    /**
     * 自定义密码验证匹配器
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        setCredentialsMatcher(new SimpleCredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
                UserPassOpenIdToken token = (UserPassOpenIdToken) authenticationToken;
                SimpleAuthenticationInfo info = (SimpleAuthenticationInfo) authenticationInfo;
                // 获取明文密码及密码盐
                String password = String.valueOf(token.getPassword());
                String salt = CodecSupport.toString(info.getCredentialsSalt().getBytes());
                if ("1".equals(token.getLoginType())) {
                  return true;
                }

                return equals(ShiroUtil.encrypt(password, salt), info.getCredentials());
            }
        });
    }
}
