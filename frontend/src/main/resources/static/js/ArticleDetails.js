// JavaScript Document

function iniParam() {
    var form = layui.form,laypage = layui.laypage,layedit = layui.layedit,tree=layui.tree;
	
 	layer.photos({
		photos: '#details-content',
		anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
	});


	//评论和留言的编辑器
	for(var i=1;i<9;i++){
		layedit.build('demo-'+i.toString(), {
			height: 150,
			tool: ['face', '|', 'link'],
		});
	}

    var _this;
    $(".btn-reply").click(function(){
        $('#replyBtn').data("parentId",$(this).attr('data-id'));
        $('#replyBtn').data("levelId",$(this).attr('data-levelId'));
        _this = this;
        if ($(this).text() == '回复') {
            layer.open({
                title: '回复 '+$(this).attr('data-name'),
                type: 1,
                area: ['500px', '300px'],
                content: $('.replycontainer') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
            });
        }
    });
	
	laypage.render({
		elem: 'page',
		count: 10, //数据总数通过服务端得到
		limit: 5, //每页显示的条数。laypage将会借助 count 和 limit 计算出分页数。
		curr: 1,
		first: '首页',
		last: '尾页',
		layout: ['prev', 'page', 'next', 'skip'],
		//theme: "page",
		jump: function (obj, first) {
			if (!first) { //首次不执行
				layer.msg("第"+obj.curr+"页");

			}
		}
	});

	var data = new Array();

	 $.get("/article/cateMenu?articleId="+$('#pCateId').val(),function (cateMap) {
        //方法二：
        for(var key in cateMap) {
            var dataParent ={};
            dataParent.title = key;
            var datachi = new Array();
            var spread = false;
            for(var i in cateMap[key]) {
                var value = cateMap[key][i];
                var datachildren = {};
                if (value.xtitle) {
                   datachildren.title = value.xtitle;
                }else{
                    datachildren.title = value.title;
                }
                if(value.id == $('#articleId').val()){
                    spread =true;
                    datachildren.selected = true;
                }
                datachildren.href = '/article/'+value.id;
                datachi.push(datachildren)   ;
            }
            dataParent.children = datachi;
            dataParent.spread =spread;
            data.push(dataParent);
        }
         //渲染
         var inst1 = tree.render({
             elem: '#test2'  //绑定元素
             ,data: data
             ,isJump:true

         });
     });


	//我用的百度编辑器，按照你们自己需求改
	CodeHighlighting(); //代码高亮
    function CodeHighlighting() {
        //添加code标签
        var allPre = document.getElementsByTagName("pre");
        for (i = 0; i < allPre.length; i++) {
            var onePre = document.getElementsByTagName("pre")[i];
            var myCode = document.getElementsByTagName("pre")[i].innerHTML;
            onePre.innerHTML = '<div class="pre-title">Code</div><code class="' + onePre.className.substring((onePre.className.indexOf(":") + 1), onePre.className.indexOf(";")) + '">' + myCode + '</code>';
        }
        //添加行号
        $("code").each(function () {
            $(this).html("<ol><li>" + $(this).html().replace(/\n/g, "\n</li><li>") + "\n</li></ol>");
        });
		hljs.initHighlighting(); //对页面上的所有块应用突出显示
        //hljs.initHighlightingOnLoad(); //页面加载时执行代码高亮
    }


    var index= layedit.build('comment-input', {
        tool: ['face', '|', 'left', 'center', 'right', 'link', 'unlink']
        , height: 150
    });

    $('#commentBtn').click(function () {
        var obj = new Object();
        var article = {id:$('#articleId').val()};
        obj.comment= layedit.getContent(index);
        if(!obj.comment){
            layer.msg('不能为空');
            return false;
        }
        var indexc = layer.load();
        obj.article = article;
        obj.parentId=0;
        obj.levelId = 0;

        $.ajax({
            url:"/frontend/comment/sub",
            type:"post",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=UTF-8",
			success : function (resp) {
				layer.close(indexc);
                layer.msg(resp.msg);
                var u = $("#comment_count_text").text();
                u = u.substring(0, u.length - 1);
                var s = parseInt(u) + 1;
                $("#comment_count_text").text(s + "条")

                var item = resp.data;
                var Mozilla = item.userAgent;
                Mozilla =  Mozilla.substring(0,Mozilla.indexOf("/"));
                var li= '<li>'+
                    '      <div class="comment-parent">'+
                    '       <a href="javaScript:void(0)"><img src=\"'+$("#picture").val()+'\" alt="" /></a>'+
                    '      <div class="info">'+
                    '     <span class="username"><a href="javaScript:void(0)">'+$("#nickname").val()+'</a></span>'+
                    '      </div>'+
                    '      <div class="content comment-text">'+
                    item.comment                +
                    '    </div>'+
                    '    <p class="info info-footer">'+
                    '     <span class="aux-word">'+
                    '     <i class="layui-icon layui-icon-log"></i>'+item.createDate+
                    '    </span>'+
                    '    <span class="aux-word">'+
                    '    <i class="layui-icon layui-icon-location"></i>'+item.ipCnAddr+
                    '    </span>'+
                    '    <span class="aux-word">'+
                    '   <i class="layui-icon layui-extend-liulanqi"></i>'+Mozilla+
                    '     </span>'+
                    '    </p> '+
                    '    </div>'+
                    ' </li>';

                $(".blog-comment").prepend(li);
			}
         });
    });

    $('#beforeLogin').click(function () {

        var url=window.location.href;
        window.open('/api/qq?url='+url,'_self');

    });

    var reindex= layedit.build('replay-input', {
        tool: ['face', '|', 'left', 'center', 'right', 'link', 'unlink']
        , height: 150
    });
    $('#replyBtn').click(function () {
        var obj = new Object();
        var article = {id:$('#articleId').val()};
        obj.comment= layedit.getContent(reindex);
        if(!obj.comment){
            layer.msg('不能为空');
            return false;
        }
        var indexc = layer.load();
        obj.article = article;
        obj.parentId=$('#replyBtn').data("parentId");
        obj.levelId = $('#replyBtn').data("levelId");
        $.ajax({
            url:"/frontend/comment/sub",
            type:"post",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=UTF-8",
            success : function (resp) {
                layer.close(indexc);
                layer.msg(resp.msg);
                layer.closeAll('page'); //关闭所有页面层
                var u = $("#comment_count_text").text();
                u = u.substring(0, u.length - 1);
                var s = parseInt(u) + 1;
                $("#comment_count_text").text(s + "条")

                var item = resp.data;
                var Mozilla = item.userAgent;
                Mozilla =  Mozilla.substring(0,Mozilla.indexOf("/"));
                var li=
                    '      <div class="comment-child">'+
                    '       <a href="javaScript:void(0)"><img src=\"'+$("#picture").val()+'\" alt="" /></a>'+
                    '      <div class="info">'+
                    ' <span class="username"><a href="javaScript:void(0)">'+$("#nickname").val()+'</a></span>'+
                    ' <span>回复<a href="javaScript:void(0)" class="to-username">'+$(_this).attr('data-name')+'</a>：</span>'+
                    '<span class="comment-text">'+ item.comment   +'</span>'+
                    ' </div>'+
                    '    <p class="info info-footer">'+
                    '     <span class="aux-word">'+
                    '     <i class="layui-icon layui-icon-log"></i>'+item.createDate+
                    '    </span>'+
                    '    <span class="aux-word">'+
                    '    <i class="layui-icon layui-icon-location"></i>'+item.ipCnAddr+
                    '    </span>'+
                    '    <span class="aux-word">'+
                    '   <i class="layui-icon layui-extend-liulanqi"></i>'+Mozilla+
                    '     </span>'+
                    '    </p> '+
                    '    </div>';

                $(_this).parent().parent().after(li);
            }
        });
    });

   //在线代码编辑器
   // layer.open({
   //     type: 2,
   //     closeBtn:0,
   //
   //     shade:0,
   //     title: 'Java在线编程',
   //     area: ['600px', '60%'],
   //     maxmin:true,
   //     content: '/frontend/compiler/index' //iframe的url
   // });
    try {
        new Function(codeLayer)();
    }catch (e) {
        console.log(e.message);
    }
   // $(".layui-layer-min").trigger("click");
  //  $(".layui-layer-max").hide();
   // $(".layui-layer-maxmin").show();

}

function setCookie(name, value) {
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
};
 function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
};