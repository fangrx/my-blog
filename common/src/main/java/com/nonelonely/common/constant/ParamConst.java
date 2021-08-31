package com.nonelonely.common.constant;
/**
 * 参数表中的一些key
 * 提供程序调用
 */
public class ParamConst {

        /**
         * 网站管理员的角色id，通过initListener初始化之后，会全局设置到NBContext对象中
         */
        public static String WEBMASTER_ROLE_ID = "webmaster_role_id";

        /**
         * 普通用户角色id，通过initListener初始化之后，会全局设置到NBContext对象中
         */
        public static String WEBUSER_ROLE_ID = "webuser_role_id";
        /**
         * 是否已经初始化
         * 0：否，1：是
         */
        public static String INIT_STATUS = "init_status";

        public static  String INIT_NOT = "0";
        public static String INIT_SURE = "1";

        /**
         * 网站标题的文字
         */
        public static String WEBSITE_TITLE = "website_title";

        /**
         * 页脚的文字
         */
        public static String FOOTER_WORDS = "footer_words";

        /**
         * 首页置顶文字
         */
        public static String INDEX_TOP_WORDS = "index_top_words";

        public static   String MENU_SEARCH_SHOW = "menu_search_show";

        /**
         * 信息板内容
         */
        public static String INFO_LABEL = "info_label";

        /**
         * 网站logo的文字
         */
        public static String WEBSITE_LOGO_WORDS = "website_logo_words";

        /**
         * 网站logo的文字旁的小字
         */
        public static String WEBSITE_LOGO_SMALL_WORDS = "website_logo_small_words";

        /**
         * 评论置顶公告
         */
        public static  String COMMENT_NOTICE = "comment_notice";

        /**
         * 项目置顶公告
         */
        public static String PROJECT_TOP_NOTICE="project_top_notice";

        /**
         * 留言板的提示信息文字
         */
        public static String MESSAGE_PANEL_WORDS = "message_panel_words";


        /**
         * 是否全局开放评论
         */
        public static String ALL_COMMENT_OPEN = "all_comment_open";

        /**
         * 是否展示额外连接
         */
        public static String MENU_LINK_SHOW = "menu_link_show";

        /**
         * 支付宝付款码
         */
        public static String ALIPAY = "alipay";

        /**
         * 微信付款码
         */
        public static String WECHAT_PAY = "wechat_pay";

        /**
         * qq登录API的app_id，请自行去qq互联(https://connect.qq.com)申请
         */
        public static String APP_ID = "app_id";

        /**
         * qq登录API的app_key，请自行去qq互联(https://connect.qq.com)申请
         */
        public static String APP_KEY = "app_key";

        /**
         * 是否开放qq登录
         */
        public static String QQ_LOGIN = "qq_login";

        /**
         * 是否设置了网站管理员
         */
        public static  String IS_SET_MASTER = "is_set_master";

        /**
         * 是否开启留言功能
         */
        public static String IS_OPEN_MESSAGE = "is_open_message";

        /**
         * 网站信息和会员中心显示顺序，1表示网站信息显示在首要位置
         */
        public static String INFO_PANEL_ORDER = "info_panel_order";


        /**
         * 是否开启云服务器上传
         */
        public static  String IS_OPEN_OSS_UPLOAD = "is_open_oss_upload";

        /**
         * 标识上传的类型
         * 分别有本地服务器上传、七牛云服务器上传
         */
        public static String UPLOAD_TYPE = "upload_type";

        /**
         * 七牛云AccessKey
         */
        public static  String QINIU_ACCESS_KEY = "qiniu_accessKey";

        /**
         * 七牛云SecretKey
         */
        public static String QINIU_SECRET_KEY = "qiniu_secretKey";

        /**
         * 七牛云Bucket
         */
        public static String QINIU_BUCKET = "qiniu_bucket";

        /**
         * 访问七牛云服务器的域名
         */
        public static  String QINIU_DOMAIN = "qiniu_domain";

        /**
         * 设置博客的分页形式
         * 0：默认模式（流式下拉加载），1：显示分页按钮类型的加载（采用单页模式）
         */
        String PAGE_MODERN = "page_modern";

        /**
         * 首页风格：简约/普通（simple/normal）
         */
        String BLOG_STYLE = "index_style";

        /**
         * 博文首页分页的pageSize大小
         */
        String BLOG_INDEX_PAGE_SIZE = "blog_index_page_size";

        /**
         * 是否开启访问统计
         * 因为是频繁的插入数据库，所以默认是不开启此项的
         */
        String STATISTIC_ANALYSIS = "statistic_analysis";

        /**
         * 文章摘要文字长度
         */
        public static String ARTICLE_SUMMARY_WORDS_LENGTH = "article_summary_words_length";

        /**
         * 下面是邮件相关的参数
         */
        public static String MAIL_SMPT_SERVER_ADDR = "mail_smpt_server_addr";
        public static String MAIL_SMPT_SERVER_PORT = "mail_smpt_server_port";
        public static String MAIL_SERVER_ACCOUNT = "mail_server_account";
        public static String MAIL_SENDER_NAME = "mail_sender_name";
        public static String MAIL_SERVER_PASSWORD = "mail_server_password";


        /**
         * 评论关键字过滤
         */
        public static  String COMMENT_KEYWORD = "comment_keyword";
    }

