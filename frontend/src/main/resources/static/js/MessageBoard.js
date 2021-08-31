// JavaScript Document

function iniParam() {
    var form = layui.form,laypage = layui.laypage,layedit = layui.layedit;
	
    //评论和留言的编辑器
	for(var i=1;i<9;i++){
		layedit.build('demo-'+i.toString(), {
			height: 150,
			tool: ['face', '|', 'link'],
		});
	}
	

	// $(".btn-reply").click(function(){
	// 	 if ($(this).text() == '回复') {
    //    		$(this).parent().next().removeClass("layui-hide");
    //     	$('.btn-reply').text('回复');
	// 	    $(this).text('收起');
	// 	}
	// 	else {
	// 		$(this).parent().next().addClass("layui-hide");
	// 		$(this).text('回复');
	// 	}
	// });
	
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

    var index= layedit.build('comment-input', {
        tool: ['face', '|', 'left', 'center', 'right', 'link', 'unlink']
        , height: 150
    });

    $('#commentBtn').click(function () {
        var obj = new Object();
     //   var article = {id:$('#articleId').val()};
        obj.comment= layedit.getContent(index);
        if(!obj.comment){
            layer.msg('不能为空');
            return false;
        }
        var indexc = layer.load();
      //  obj.article = article;
        obj.parentId=0;
        obj.levelId = 0;
        obj.type = 1;
        $.ajax({
            url:"/frontend/message/sub",
            type:"post",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=UTF-8",
            success : function (resp) {
                layer.close(indexc);
                layer.msg(resp.msg);
                // var u = $("#comment_count_text").text();
                // u = u.substring(0, u.length - 1);
                // var s = parseInt(u) + 1;
                // $("#comment_count_text").text(s + "条")
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
    //    var article = {id:$('#articleId').val()};
        obj.comment= layedit.getContent(reindex);
        if(!obj.comment){
            layer.msg('不能为空');
            return false;
        }
        var indexc = layer.load();
       // obj.article = article;
        obj.parentId=$('#replyBtn').data("parentId");
        obj.levelId = $('#replyBtn').data("levelId");
        obj.type = 1;
        $.ajax({
            url:"/frontend/message/sub",
            type:"post",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=UTF-8",
            success : function (resp) {
                layer.close(indexc);
                layer.msg(resp.msg);
                layer.closeAll('page'); //关闭所有页面层
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




}



