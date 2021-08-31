var tabs;
layui.use(['element', 'form', 'layer', 'upload','layedit'], function () {
    var $ = layui.jquery;
    window.jQuery = layui.jquery;
    var element = layui.element; //加载element模块
    var layer = layui.layer; //加载layer模块
    var upload = layui.upload;  //加载upload模块
    var layedit = layui.layedit;
    /* 侧边栏开关 */
    $(".side-toggle").on("click", function (e) {
        e.preventDefault();
        var to = $(".layui-layout-admin");
        to.toggleClass("layui-side-shrink");
        to.attr("toggle") === 'on' ? to.attr("toggle", "off") : to.attr("toggle", "on");
    });
    $(".layui-side").on("click", function () {
        var to = $(".layui-layout-admin");
        if (to.attr("toggle") === 'on') {
            to.attr("toggle", "off");
            to.removeClass("layui-side-shrink");
        }
    });

    /* 最大化窗口 */
    $(".dolphin-screen-full").on("click", function (e) {
        e.preventDefault();
        if (!$(this).hasClass("full-on")) {
            var docElm = document.documentElement;
            var full = docElm.requestFullScreen || docElm.webkitRequestFullScreen ||
                docElm.mozRequestFullScreen || docElm.msRequestFullscreen;
            "undefined" !== typeof full && full && full.call(docElm);
        } else {
            document.exitFullscreen ? document.exitFullscreen()
                : document.mozCancelFullScreen ? document.mozCancelFullScreen()
                : document.webkitCancelFullScreen ? document.webkitCancelFullScreen()
                    : document.msExitFullscreen && document.msExitFullscreen()
        }
        $(this).toggleClass("full-on");
    });

    /* 新建或切换标签栏 */
    tabs = function (url) {
        var item = $('[lay-url="' + url + '"]');
        if (url !== undefined && url !== '#' && item.length > 0) {
            var bootLay = $('[lay-id="' + url + '"]');
            if (bootLay.length === 0) {
                var title = item.attr("lay-icon") === 'true' ? item.html()
                    : item.children(".layui-nav-title").text();
                var iframeUrl = (window.location.pathname + url).replace('//','/');
                element.tabAdd('iframe-tabs', {
                    title: title
                    , content: '<iframe src="' + url + '" frameborder="0" class="layui-layout-iframe"></iframe>'
                    , id: url
                });
            }
            element.tabChange('iframe-tabs', url);
        }
    };

    /* 监听导航栏事件，实现标签页的切换 */
    element.on("nav(layui-nav-side)", function ($this) {
        var url = $this.attr('lay-url');
        tabs(url);
    });

    /* 监听标签栏事件，实现导航栏高亮显示 */
    element.on("tab(iframe-tabs)", function () {
        var layId = $(this).attr("lay-id");
        $(".layui-side .layui-this").removeClass("layui-this");
        $('[lay-url="' + layId + '"]').parent().addClass("layui-this");
        // 改变地址hash值
        location.hash = this.getAttribute('lay-id');
    });

    /* 监听hash来切换选项卡*/
    window.onhashchange = function (e) {
        var url = location.hash.replace(/^#/, '');
        var index = $(".layui-layout-admin .layui-side .layui-nav-item")[0];
        $(index).children("a").attr("lay-icon", "true");
        if (url === "" || url === undefined) {
            url = $(index).children("[lay-url]").attr("lay-url");
        }
        tabs(url);
    };
    window.onhashchange();

    /* 初始化时展开子菜单 */
    $("dd.layui-this").parents(".layui-nav-child").parent()
        .addClass("layui-nav-itemed");

    /* 刷新iframe页面 */
    $(".refresh-btn").click(function () {
        location.reload();
    });

    /* AJAX请求默认选项，处理连接超时问题 */
    $.ajaxSetup({
        beforeSend: function () {
            layer.load(0);
        },
        complete: function (xhr) {
            layer.closeAll('loading');
            if (xhr.status === 401) {
                layer.confirm('session连接超时，是否重新登录？', {
                    btn: ['是', '否']
                }, function () {
                    if (window.parent.window !== window) {
                        window.top.location = window.location.pathname + '/login';
                    }
                });
            }
        }
    });

    /*  漂浮消息 */
    $.fn.Messager = function (result) {
        console.log(result);
        if (result.code === 200) {

            if (result.msg == null){
                result.msg = '保存成功';
            }
            layer.msg(result.msg, {offset: '15px', time: 3000, icon: 1});

            setTimeout(function () {

                if (result.data === 'submit[refresh]') {

                    parent.location.reload();
                    return;
                }
                if (result.data == null) {
                    window.location.reload();
                } else {
                    window.location.href = result.data
                }
            }, 2000);

            layui.data('article', {
                key: 'content'
                , value: ''
            });

        } else {
            layer.msg(result.msg, {offset: '15px', time: 3000, icon: 2});
        }
    };

    /* 提交表单数据 */
    $(document).on("click", ".ajax-submit", function (e) {
        e.preventDefault();
        var form = $(this).parents("form");
        var url = form.attr("action");
        var serializeArray = form.serializeArray();

            $.post(url, serializeArray, function (result) {
            if (result.data == null) {
                result.data = 'submit[refresh]';
            }
            $.fn.Messager(result);
        });

    });

    /* get方式异步 */
    $(document).on("click", ".ajax-get", function (e) {
        e.preventDefault();
        var msg = $(this).data("msg");
        if (msg !== undefined) {
            layer.confirm(msg + '？', {
                title: '提示',
                btn: ['确认', '取消']
            }, function () {
                $.get(e.target.href, function (result) {
                    $.fn.Messager(result);
                });
            });
        } else {
            $.get(e.target.href, function (result) {
                $.fn.Messager(result);
            });
        }
    });

    // post方式异步-操作状态
    $(".ajax-status").on("click", function (e) {
        e.preventDefault();
        var checked = [];
        var tdcheckbox = $(".dolphin-table td .dolphin-checkbox :checkbox:checked");
        if (tdcheckbox.length > 0) {
            tdcheckbox.each(function (key, val) {
                checked.push("ids=" + $(val).attr("value"));
            });
            $.post(e.target.href, checked.join("&"), function (result) {
                $.fn.Messager(result);
            });
        } else {
            layer.msg('请选择一条记录');
        }
    });

    /* 添加/修改弹出层 */
    $(document).on("click", ".open-popup, .open-popup-param", function () {
        var title = $(this).data("title");
        var url = $(this).attr("data-url");
        if ($(this).hasClass("open-popup-param")) {
            var tdcheckbox = $(".dolphin-table td .dolphin-checkbox :checkbox:checked");
            var param = '';
            if (tdcheckbox.length === 0) {
                layer.msg('请选择一条记录');
                return;
            }
            if (tdcheckbox.length > 1 && $(this).data("type") === 'radio') {
                layer.msg('只允许选中一个');
                return;
            }
            tdcheckbox.each(function (key, val) {
                param += "ids=" + $(val).attr("value") + "&";
            });
            param = param.substr(0, param.length - 1);
            url += "?" + param;
        }
        var size = $(this).attr("data-size"), layerArea;
        if (size === undefined || size === "auto" ) {
            layerArea = ['50%', '80%'];
        }else if ( size === "max") {
            layerArea = ['100%', '100%'];
        }else if (size.indexOf(',') !== -1) {
            var split = size.split(",");
            layerArea = [split[0] + 'px', split[1] + 'px'];
        }
        window.layerIndex = layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            maxmin: true,
            area: layerArea,
            content: [url, 'on']
        });
        if (size === "max") {
            layer.full(layerIndex);
        }
    });

    /* 关闭弹出层 */
    $(".close-popup").click(function (e) {
        e.preventDefault();
        parent.layer.close(window.parent.layerIndex);
    });

    /* 下拉按钮组 */
    $(".btn-group").click(function (e) {
        e.stopPropagation();
        $this = $(this);
        $this.toggleClass("show");
        $(document).one("click", function () {
            if ($this.hasClass("show")) {
                $this.removeClass("show");
            }
        });
    });

    // 展示数据列表-多选框
    var thcheckbox = $(".dolphin-table th .dolphin-checkbox :checkbox");
    thcheckbox.on("change", function () {
        var tdcheckbox = $(".dolphin-table td .dolphin-checkbox :checkbox");
        if (thcheckbox.is(':checked')) {
            tdcheckbox.prop("checked", true);
        } else {
            tdcheckbox.prop("checked", false);
        }
    });

    // 检测列表数据是否为空
    var dolphinTable = $(".dolphin-table tbody");
    if (dolphinTable.length > 0) {
        var children = dolphinTable.children();
        if (children.length === 0) {
            var length = $(".dolphin-table thead th").length;
            var trNullInfo = "<tr><td class='dolphin-table-null' colspan='"
                + length + "'>没有找到匹配的记录</td></tr>";
            dolphinTable.append(trNullInfo);
        }
    }

    // 携带参数跳转
    var paramSkip = function () {
        var getSearch = "";
        // 搜索框参数
        $('.dolphin-search-box [name]').each(function (key, val) {
            if ($(val).val() !== "" && $(val).val() !== undefined) {
                getSearch += $(val).attr("name") + "=" + $(val).val() + "&";
            }
        });

        // 页数参数
        var pageSize = $(".page-number").val();
        if (pageSize !== undefined && pageSize !== "") {
            getSearch += "size=" + pageSize + "&";
        }

        // 排序参数
        var asc = $(".sortable.asc").data("field");
        if(asc !== undefined){
            getSearch += "orderByColumn=" + asc + "&isAsc=asc&";
        }
        var desc = $(".sortable.desc").data("field");
        if(desc !== undefined){
            getSearch += "orderByColumn=" + desc + "&isAsc=desc&";
        }

        if (getSearch !== "") {
            getSearch = "?" + getSearch.substr(0, getSearch.length - 1);
        }
        window.location.href = window.location.pathname + getSearch;
    };

    /* 展示列表数据搜索 */
    $(document).on("click", ".dolphin-search-btn", function () {
        paramSkip();
    });
    /* 改变显示页数 */
    $(document).on("change", ".page-number", function () {
        paramSkip();
    });
    /* 触发字段排序 */
    $(document).on("click", ".sortable", function () {
        $(".sortable").not(this).removeClass("asc").removeClass("desc");
        if($(this).hasClass("asc")){
            $(this).removeClass("asc").addClass("desc");
        }else {
            $(this).removeClass("desc").addClass("asc");
        }
        paramSkip();
    });

    /* 参数化字段排序 */
    var getSearch = function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    };
    var field = getSearch("orderByColumn");
    var isAsc = getSearch("isAsc");
    if(field != null){
        $("[data-field='"+ field +"']").addClass(isAsc);
    }

    /** 上传图片操作 */
    upload.render({
        elem: '.upload-image' //绑定元素
        ,url: $('.upload-image').attr('up-url') //上传接口
        ,field: 'image' //文件域的字段名
        ,acceptMime: 'image/*' //选择文件类型
        ,exts: 'jpg|jpeg|png|gif' //支持的图片格式
        ,multiple: true //开启多文件选择
        ,choose: function (obj) {
            obj.preview(function (index, file, result) {
                var upload = $('.upload-image');
                var name = upload.attr('name');
                var show = upload.parents('.layui-form-item').children('.upload-show');
                show.append("<div class='upload-item'><img src='"+ result +"'/>" +
                    "<input id='"+ index +"' type='hidden' name='"+name+"'/>" +
                    "<i class='upload-item-close layui-icon layui-icon-close'></i></div>");
            });
        }
        ,done: function(res, index, upload){
            var field = $('.upload-image').attr('up-field') || 'id';
            // 解决节点渲染和异步上传不同步问题
            var interval = window.setInterval(function(){
                var hide = $("#"+index);
                if(hide.length > 0){
                    var item = hide.parent('.upload-item');
                    if (res.code === 200) {
                        hide.val(res.data[field]);
                        item.addClass('succeed');
                    }else {
                        hide.remove();
                        item.addClass('error');
                    }
                    clearInterval(interval);
                }
            }, 100);
        }
    });

    // 删除上传图片展示项
    $(document).on("click", ".upload-item-close", function () {
        $(this).parent('.upload-item').remove();
    });

    // layui.use(['layedit', 'layer', 'jquery'], function () {
    //     var $ = layui.jquery
    //         , layer = layui.layer
    //         , layedit = layui.layedit;
    //     layedit.set({
    //         //暴露layupload参数设置接口 --详细查看layupload参数说明
    //         uploadImage: {
    //             url: $('#content').attr('img-url') ,//上传接口,
    //             accept: 'image',
    //             acceptMime: 'image/*',
    //             exts: 'jpg|png|gif|bmp|jpeg',
    //             size: 1024 * 10,
    //             data: {
    //                 name: "测试参数",
    //                 age:99
    //             }
    //             ,done: function (data) {
    //                 console.log(data);
    //             }
    //         },
    //         uploadVideo: {
    //             url: $('#content').attr('vi-url'),
    //             accept: 'video',
    //             acceptMime: 'video/*',
    //             exts: 'mp4|flv|avi|rm|rmvb',
    //             size: 1024 * 10 * 2,
    //             done: function (data) {
    //                 console.log(data);
    //             }
    //         }
    //         , uploadFiles: {
    //             url: $('#content').attr('file-url'),
    //             accept: 'file',
    //             acceptMime: 'file/*',
    //             size: '20480',
    //             autoInsert: true , //自动插入编辑器设置
    //             done: function (data) {
    //                 console.log(data);
    //             }
    //         }
    //         //右键删除图片/视频时的回调参数，post到后台删除服务器文件等操作，
    //         //传递参数：
    //         //图片： imgpath --图片路径
    //         //视频： filepath --视频路径 imgpath --封面路径
    //         //附件： filepath --附件路径
    //         , calldel: {
    //             url: 'your url',
    //             done: function (data) {
    //                 console.log(data);
    //             }
    //         }
    //         , rightBtn: {
    //             type: "layBtn",//default|layBtn|custom  浏览器默认/layedit右键面板/自定义菜单 default和layBtn无需配置customEvent
    //             customEvent: function (targetName, event) {
    //                 //根据tagName分类型设置
    //                 switch (targetName) {
    //                     case "img":
    //                         alert("this is img");
    //                         break;
    //                     default:
    //                         alert("hello world");
    //                         break;
    //                 };
    //                 //或者直接统一设定
    //                 //alert("all in one");
    //             }
    //         }
    //         //测试参数
    //         , backDelImg: true
    //         //开发者模式 --默认为false
    //         , devmode: true
    //         //是否自动同步到textarea
    //         , autoSync: true
    //         //内容改变监听事件
    //         , onchange: function (content) {
    //             //console.log(content);
    //         }
    //         //插入代码设置 --hide:false 等同于不配置codeConfig
    //         , codeConfig: {
    //             hide: true,  //是否隐藏编码语言选择框
    //             default: 'javascript', //hide为true时的默认语言格式
    //             encode: true //是否转义
    //             ,class:'layui-code' //默认样式
    //         }
    //         //新增iframe外置样式和js
    //         , quote:{
    //         //    style: ['Content/css.css'],
    //             //js: ['/Content/Layui-KnifeZ/lay/modules/jquery.js']
    //         }
    //         //自定义样式-暂只支持video添加
    //         //, customTheme: {
    //         //    video: {
    //         //        title: ['原版', 'custom_1', 'custom_2']
    //         //        , content: ['', 'theme1', 'theme2']
    //         //        , preview: ['', '/images/prive.jpg', '/images/prive2.jpg']
    //         //    }
    //         //}
    //         //插入自定义链接
    //         // , customlink:{
    //         //     title: '插入layui官网'
    //         //     , href: 'https://www.layui.com'
    //         //     ,onmouseup:''
    //         // }
    //         , facePath: 'http://knifez.gitee.io/kz.layedit/Content/Layui-KnifeZ/'
    //         , devmode: true
    //         , videoAttr: ' preload="none" '
    //         //预览样式设置，等同layer的offset和area规则，暂时只支持offset ,area两个参数
    //         //默认为 offset:['r'],area:['50%','100%']
    //         //, previewAttr: {
    //         //    offset: 'r'
    //         //    ,area:['50%','100%']
    //         //}
    //         , tool: [
    //             'html', 'undo', 'redo', 'code', 'strong', 'italic', 'underline', 'del', 'addhr', '|','removeformat', 'fontFomatt', 'fontfamily','fontSize', 'fontBackColor', 'colorpicker', 'face'
    //             , '|', 'left', 'center', 'right', '|', 'link', 'unlink', 'images', 'image_alt', 'video','attachment', 'anchors'
    //             , '|'
    //             , 'table','customlink'
    //             , 'fullScreen','preview'
    //         ]
    //         , height: '300px'
    //     });
    //     var ieditor = layedit.build('content');
    //     //设置编辑器内容
    //     layedit.setContent(ieditor, $('#content').val(), false);
    //     $("#openlayer").click(function () {
    //         layer.open({
    //             type: 2,
    //             area: ['700px', '500px'],
    //             fix: false, //不固定
    //             maxmin: true,
    //             shadeClose: true,
    //             shade: 0.5,
    //             title: "title",
    //             content: 'add.html'
    //         });
    //     })
    // })

    $("#seeIcons").on("click",function(){
        layer.open({
            type: 2,
            title: "字体图标列表",
            area: ['480px', '230px'],
            fixed: false, //不固定
            content: '/font/list'
        });

    });

});
function createKindEditor() {
   window.editor= KindEditor.create('#content', {
        cssData: 'body {font-family: "Helvetica Neue", Helvetica, "PingFang SC", 微软雅黑, Tahoma, Arial, sans-serif; font-size: 14px}',
        width: "auto",
        height: "300px",
        items: [
            'source', 'preview', 'undo', 'redo', 'code', 'cut', 'copy', 'paste',
            'plainpaste', 'wordpaste', 'justifyleft', 'justifycenter', 'justifyright',
            'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
            'superscript', 'clearhtml', 'quickformat', 'selectall', 'fullscreen', '/',
            'formatblock', 'fontname', 'fontsize', 'forecolor', 'hilitecolor', 'bold',
            'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', 'image', 'graft',
            'insertfile', 'table', 'hr', 'emoticons', 'pagebreak',
            'link', 'unlink', 'about'
        ],
        uploadJson: '/upload?reqType=nk',
        dialogOffset: 0, //对话框距离页面顶部的位置，默认为0居中，
        allowImageUpload: true,
        allowMediaUpload: true,
        themeType: 'black',
        fixToolBar: true,
        autoHeightMode: true,
        filePostName: 'file',//指定上传文件form名称，默认imgFile
        resizeType: 1,//可以改变高度
        afterCreate: function () {
            var self = this;
            KindEditor.ctrl(document, 13, function () {
                self.sync();
                K('form[name=example]')[0].submit();
            });
            KindEditor.ctrl(self.edit.doc, 13, function () {
                self.sync();
                KindEditor('form[name=example]')[0].submit();
            });
        }
    });
   return  window.editor;
}
function createEditormd() {

    $("#content-editor").append("<div id='editormd'></div>");
    $.getScript("/lib/editormd/editormd.min.js", function () {
        window.editorMd = editormd("editormd", {
            height: 640,
            watch: true,
            codeFold: true,
            toolbarIcons: function () {
                return [
                    "undo", "redo", "|",
                    "bold", "del", "italic", "quote", "ucwords", "uppercase", "lowercase", "|",
                    "h1", "h2", "h3", "h4", "h5", "h6", "|",
                    "list-ul", "list-ol", "hr", "|",
                    "link", "reference-link", "image", "code", "preformatted-text", "code-block", "table", "datetime","emoji", "html-entities", "pagebreak", "|",
                    "goto-line", "watch", "preview", "fullscreen", "clear", "search", "|",
                    "help", "info","|","runcode"
                ]
            },
            pluginPath: '/lib/editormd/plugins/',
            markdown: mdContents,
            path: '/lib/editormd/lib/',
            placeholder: '请在此书写你的内容',
            saveHTMLToTextarea: true,
            searchReplace: true,
            taskList: true,
            tex: true,// 开启科学公式TeX语言支持，默认关闭
            flowChart: true,//开启流程图支持，默认关闭
            sequenceDiagram: true,//开启时序/序列图支持，默认关闭,
            imageUpload: true,
            imageFormats: ["jpg", "jpeg", "gif", "png", "bmp"],
            imageUploadURL:  "/upload/editorMD?reqType=lay",
            htmlDecode : true,
            emoji : true,
            onfullscreen: function () {
                $(".layui-header").css("z-index", "-1");
                $("#left-menu").css("z-index", "-1");
                $(".layui-form-item>label,.layui-form-item>div:not(#content-editor)").css("z-index", -1);
                $(".layui-card").css("z-index", "-1");
            },
            onfullscreenExit: function () {
                $(".layui-header").css("z-index", "999");
                $("#left-menu").css("z-index", "999");
                $(".layui-form-item>label,.layui-form-item>div:not(#content-editor)").css("z-index", "");
                $(".layui-card").css("z-index", "999");
            },
            toolbarIconsClass : {
                runcode : "fa fa-file-o"
            },
            lang : {
                toolbar : {
                    runcode : "运行实例"
                }
            },
            // 自定义工具栏按钮的事件处理
            toolbarHandlers : {
                /**
                 * @param {Object}      cm         CodeMirror对象
                 * @param {Object}      icon       图标按钮jQuery元素对象
                 * @param {Object}      cursor     CodeMirror的光标对象，可获取光标所在行和位置
                 * @param {String}      selection  编辑器选中的文本
                 */
                runcode : function(cm, icon, cursor, selection) {
                    layer.prompt({title: '输入文件名称', formType: 2}, function(test, index){
                        var a = '<a target="_blank" href="/frontend/compiler/index?path='+ test+'" class="layui-btn" rel="noopener noreferrer">运行实例 »</a>';
                        cm.replaceSelection(a);
                        layer.close(index);
                    });

                }
            }
        });
    });
}
