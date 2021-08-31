// JavaScript Document

layui.use(['element', 'laypage', 'form', 'util', 'layer', 'flow','table','layedit','carousel','tree'], function () {
    try {
        var util = layui.util, carousel = layui.carousel,layer = layui.layer;
	   //console.log("小海豚博客：https://www.nonelonely.com");
        $(document).ready(function () { //DOM树加载完毕执行，不必等到页面中图片或其他外部文件都加载完毕
            //页面加载完成后，速度太快会导到loading层闪烁，影响体验，所以在此加上500毫秒延迟
            setTimeout(function () { $("#loading").hide(); }, 500);
        });

        //初始化WOW.js
        new WOW().init();

        //导航点击效果
        $('header nav > ul > li a').click(function () {
            $('header nav > ul > li').removeClass("nav-select-this").find("a").removeClass("nav-a-click");
            $(this).addClass("nav-a-click").parent().addClass("nav-select-this");
        });
        //常规轮播
        carousel.render({
            elem: '#test1'
            ,arrow: 'always'
            ,width: '100%'
            ,height:'200px'
        });
        // //固定块
        // util.fixbar({
        //     css: { right: 10, bottom: 40, },
        //     bar1: "&#xe602e;",
        //     click: function (type) {
        //         if (type === 'bar1') {
        //             layer.tab({
        //                 area: ['300', '290px'],
        //                 resize: false,
        //                 shadeClose: true,
        //                 scrollbar: false,
        //                 anim: 4,
        //                 tab: [{
        //                     title: '微信',
        //                     content: '<img src="images/zsm.jpg" style="width:255px;" oncontextmenu="return false;" ondragstart="return false;" />',
        //                 },
        //                 {
        //                     title: '支付宝',
        //                     content: '<img src="images/zfb.jpg" style="width:255px;" oncontextmenu="return false;" ondragstart="return false;" />',
        //                 }],
        //                 success: function (layero, index) {
        //                     $("#" + layero[0].id + " .layui-layer-content").css("overflow", "hidden");
        //                     $("#" + layero[0].id + " .layui-layer-title span").css("padding", "0px");
        //                     layer.tips('本站收获的所有打赏费用均只用于服务器日常维护以及本站开发用途，感谢您的支持！', "#" + layero[0].id, {
        //                         tips: [1, '#FFB800'],
        //                         time: 0,
        //                     });
        //                 },
        //                 end: function () {
        //                     layer.closeAll('tips');
        //                 }
        //             });
        //         }
        //     }
        // });

        //使刷新页面后，此页面导航仍然选中高亮显示，自己根据你实际情况修改
        var pathnameArr = window.location.pathname.split("/");
        var pathname = pathnameArr[pathnameArr.length - 1];
            if (pathname.indexOf(".html") < 0)
                pathname += ".html";
        if (!!pathname) {
            $('header nav > ul > li').removeClass("nav-select-this").find("a").removeClass("nav-a-click");
            $('header nav > ul > li').each(function () {
                var hrefArr = $(this).find("a").attr('href').split("/");
                var href = hrefArr[hrefArr.length - 1];
                if (pathname.toLowerCase() === href.toLowerCase()) {
                    $(this).addClass("nav-select-this").find("a").addClass("nav-a-click");
                    return false;
                }
            });
        }

        iniParam();
        var userId = $("#userId").val();
        //登录图标
        var anim = setInterval(function () {
            if ($(".animated-circles").hasClass("animated")) {
                $(".animated-circles").removeClass("animated");
            } else {
                $(".animated-circles").addClass('animated');
            }
        }, 3000);
        var wait = setInterval(function () {
            $(".livechat-hint").removeClass("show_hint").addClass("hide_hint");
            clearInterval(wait);
        }, 4500);
        $(".livechat-girl").hover(function () {
            clearInterval(wait);
            $(".livechat-hint").removeClass("hide_hint").addClass("show_hint");
        }, function () {
            $(".livechat-hint").removeClass("show_hint").addClass("hide_hint");
        }).click(function () {
            if (!userId) {
                login();
            }else {
                layer.open({
                    type: 2,
                    title:'个人信息',
                    shadeClose: true,
                    maxmin: true,
                    area: ['680px','464px'],
                    content: ['/frontend/userInfo', 'on']
                });

            }
        });

        //设置样式

        if (!userId) { //未登录
                $('.girl').css("border-radius", "0px");
                $('.livechat-girl').css({ right: "-10px", bottom: "75px" }).removeClass("red-dot");
            }else {
            clearInterval(anim);
            $('.girl').css("border-radius", "50px");
            $('.livechat-girl').css({right: "0px", bottom: "80px"});
        }

        //登录事件
        function login() {
            var url=window.location.href;
            window.open('/api/qq?url='+url,'_self');
        }

    }
    catch (e) {
        layui.hint().error(e);
    }
});

//百度统计
var _hmt = _hmt || [];
(function() {
    var hm = document.createElement("script");
    hm.src = "https://hm.baidu.com/hm.js?c07ddd5b98c69450ebd8e255627f70b0";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s);
})();

var uuId = '';
function uvUpdate(uuid) {//uv写法
        var data ={};
        var nowurl = document.URL;
        var fromurl = document.referrer;
        data.uuid = uuid;
        data.fromUrl = fromurl;
        data.nowUrl = nowurl;
        $.post("/frontend/putUv",data,function (data) {
            sessionStorage.setItem("uvId",data);
            uuId = data;
        });


}
 function getUuid(len, radix){
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
        // rfc4122, version 4 form
        var r;

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';

        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random()*16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }

    return uuid.join('');
};
(function() {


    var expdate = new Date();
    var visits; //以下设置COOKIES时间为1年24 * 60 * 60 * 1000 * 365,自己随便设置该时间..
    expdate.setTime(expdate.getTime() + (24 * 60 * 60 * 1000));
    if (!(visits = localStorage.getItem("visits"))) {
        var uuid = getUuid(32, 16);
        visits = uuid;
        localStorage.setItem("visits", visits);
    }
    uvUpdate(visits);
    window.onbeforeunload=function (e) {
        if (uuId == ''){
            uuId = sessionStorage.getItem("uvId");
        }
        $.post("/frontend/putUvEnd", {"id": uuId}, function (data) {

        });
    };
})();
