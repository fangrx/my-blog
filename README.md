它来了它来了，博客3.0版本来了。目前更新的重点在于后台管理功能而且后台主题增加了win10主题,**接下来这几天会对博客管理进行大改**

# 项目介绍
小海豚博客的理念就是零配置，零安装，即部署及用 ，采用分模块的方式便于开发和维护，功能有：权限管理、人员管理，角色管理，路径资源管理，部门管理、字典管理、各种日志记录、文件管理、系统参数管理，邮件管理(支持多个邮箱账号登录，收邮件，发邮件，支持评论发送邮件，邮箱验证功能)，webSSH工具，百度站长推送，系统监控，任务调度等基础功能。博客方面有，博客编辑（支持markdown和富文本2种编辑器），类别管理，友链管理，评论管理，还增加游客浏览记录日志(用于分析，类似于百度统计),java在线运行等各种功能当然还有二次开发的功能，代码生成功能以及表单功能。
支持HTTPS和HTTP2种协议
QQ群 刚建： 321225959(已取消，等人多起来再创建群)
# 技术选型
后端技术：SpringBoot + Spring Data Jpa + Thymeleaf + Shiro + Jwt + EhCache +WebSocket +Jsch

前端技术：Layui + Font-awesome + nkeditor +JQ

# 安装教程
## 环境及插件要求
Jdk8+
Mysql5.5+
Maven
Lombok（重要）
## 导入项目
IntelliJ IDEA：Import Project -> Import Project from external model -> Maven
Eclipse：Import -> Exising Mavne Project
## 运行项目
创建一个数据库test就行，表不用创建
数据库配置：数据库名称test 用户xxxxx 密码xxxxx
通过Java应用方式运行admin模块下的com.nonelonely.BootApplication.java文件

### 后台默认帐号密码：admin/123456
### 启动地址：http://127.0.0.1:4430/ /
### 后台登录地址： http://127.0.0.1:4430/login


# 全新的项目结构

![项目结构图](https://images.gitee.com/uploads/images/2020/0225/132322_7c0689b9_1165306.png)

# 博客预览

## PC效果
![](https://nonelonely.com/upload/images/20210213/c5da9a9997ba4b5982047bbc13e64382.png)
![](https://nonelonely.com/upload/images/20210213/add6b8bcb5e24e41b3391bb193036cdb微信图片_20210214114040.png)

![](https://nonelonely.com/upload/images/20210213/1ba6f5e9a5c248349a6e7c7cefe28475.png)
![](https://nonelonely.com/upload/images/20210213/9ccdc5ba8e094af58d96d121324cfcd4微信图片_20210214114450.png)


## 手机效果
![](https://nonelonely.com/upload/images/20210213/878d7cd4a5fe4eee98cf34e6de729a65.jpg)

## 在线预览
[前端预览](https://www.nonelonely.com)
[后台预览](https://www.nonelonely.com/login)  （账号/密码  test/test）

# 未来
## 1.解决bug
## 2.重点在于编辑文章这块，接下来会增加加密文章，部分加密等功能
## 3.前端模板可以后台切换功能

# 遇到问题反馈

提问题时请优先选择Gitee Issues（方便问题追踪和一对一解决），其次我的博客-留言板。





