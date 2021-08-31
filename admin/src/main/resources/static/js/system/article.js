$(function () {
    var formSelects;
    layui.use(['element', 'form', 'layer', 'upload', 'formSelects'], function () {
        var form = layui.form;
        var element = layui.element;
        var $ = layui.$;
        var upload = layui.upload;
        formSelects = layui.formSelects;
        element.render();
        form.render();
        window._laySelect = formSelects;


        formSelects.config('tags', {
            keyName: 'name'
            , keyVal: 'name'
        });
         if(articleId == null){
             articleId = '';
         }
        formSelects.data('tags', 'server', {
            url: '/system/tag/article/tags?id=' + articleId
        });

        formSelects.maxTips('tags', function () {
            layer.msg("最多只能选择4个");
        });


        form.on("switch(summary)", function (data) {
            if (data.elem.checked) {
                $("#article-summary").show();
            } else {
                $("#article-summary").hide();
            }
        });

        form.on("switch(customUrl)", function (data) {
            if (data.elem.checked) {
                $("#urlSequence").show();
            } else {
                $("#urlSequence").hide();
            }
        });

        upload.render({
            elem: '#coverImg' //绑定元素
            , url: '/upload?reqType=lay' //上传接口
            , done: function (res) {
                if (res.code === 200) {
                    $("#coverImg").html('<p><img style="width: 144px;height: 90px;" src="' + res.data.url + '"></p>');
                    $("#cover").val(res.data.url);
                }
                layer.msg(res.msg || res.message);
            }
            , error: function () {
                layer.msg("上传失败！");
            }
        });

        //   setInterval(function() {
        //     if($('#content').val()) {
        //         layui.data('article', {
        //             key: 'content'
        //             , value: $('#content').val()
        //         });
        //         layer.msg("文章内容自动保存成功！");
        //     }
        // }, 1000*30);
        // //配置一个透明的询问框
        // layer.msg('是否打开上次自动保存的内容', {
        //     time: 1000*60, //20s后自动关闭
        //     btn: ['打开', '取消']
        //
        // } ,function(){
        //     $('#content').val(layui.data('article').content);
        // });


       //询问框

        // layer.confirm('是否打开上次自动保存的内容？', {
        //     btn: ['打开','取消'] //按钮
        // }, function(){
        //     $('#content').val(layui.data('article').content);
        //     createKindEditor()
        //     layer.msg('打开成功！');
        // }, function(){
        //     createEditormd()
        // });
        form.on('radio(editor)', function (data) {
            if (data.value === "markdown") {
                editor.remove();
                createEditormd();
            }
            else if (data.value === "html") {
                editorMd.editor.remove();
                createKindEditor();
        }
        });

        var post = function (data) {
            console.log(data.field);
            // data.field.draft = draft;
            // data.field.cate = data.field.cateId;
            data.field.tagNames = $("div.xm-input.xm-select").attr("title");
            // alert(data.field.tagNames);
            if (data.field.editor === 'html') {
                data.field.mdContent = "";
              //  data.field.content = editor.text();
            }
            if (data.field.editor === 'markdown') {
                data.field.mdContent = editorMd.getMarkdown();
                // data.field.content = editorMd.getHTML();
                data.field.content = editorMd.getPreviewedHTML()///.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&").replace(/&quot;/g, '"').replace(/&apos;/g, "'");
            }
            // data.field.cover = $("#coverImg").find("img").attr("src");
            console.log(data.field);
            $.post("/system/article/save", data.field, function (result) {
                if (result.data == null) {
                    result.data = 'submit[refresh]';
                }
                $.fn.Messager(result);
            });
            // $.ajax({
            //     type: 'post'
            //     , dataType: 'json'
            //     , url:"/system/article/save"
            //     ,contentType :'application/json'
            //     , data: data.field
            //     , success: function () {
            //
            //     }
            //     , error: function (err) {
            //         var msg = err.responseJSON.message || "发生未知错误！";
            //         layer.msg(msg);
            //     }
            // })
        };

        //监听提交
        form.on('submit(postSubmit)', function (data) {
            post(data);
            return false;
        });
    });

    if (!isMd) {
        try {
            editorMd.editor.remove();
        } catch (e) {
        }
        createKindEditor()
    } else {
        createEditormd();

    }







});







