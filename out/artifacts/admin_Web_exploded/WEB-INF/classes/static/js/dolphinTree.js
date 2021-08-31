// 树形数据展示
;(function($){
    var self;
    var TableTree=function(param){
        self=this;
        this.defaults={
            tree: $(".dolphin-tree"),
            treeTable: ".dolphin-tree-table",   // 表格类名
            treeIcon: "<i class='toggle-icon fa fa-chevron-right'></i>",
            treeFill: "<i class='toggle-fill'></i>",
            hideRank: 3,
            oldActive: null,
            oldButton: null,
            scrollTop: 90,
        }
        this.options=$.extend({},this.defaults,param);
    }

    TableTree.prototype={
        // 初始化
        init: function(){
            // 获取树形列表数据
            var tree = self.options.tree;
            $.get(tree.data('url'),function(result){
                if(result.data.length > 0){
                    // zTree传递列表数据
                  //  self.zTreeReady();

                    // 树形表格传递列表数据
                    self.tableTree(result.data);
                    self.zLayuiTree(result.data);
                    // 开启树形表格子级开关
                    self.toggleChild();
                }
            });
        },
        arrayToTree: function (arr, pId) {
        //  arr 是返回的数据  parendId 父id

        let temp = [];
        let treeArr = arr;
        treeArr.forEach((item, index) => {
            if (item.pid == pId) {
                item.spread = true;
                if (self.arrayToTree(treeArr, treeArr[index].id).length > 0) {
                    // 递归调用此函数
                    treeArr[index].children = self.arrayToTree(treeArr, treeArr[index].id);
                }
                temp.push(treeArr[index]);
            }
        });

        return temp;

        },

        zLayuiTree: function(listData){
            // 封装zTree数据
            var zNodes = self.arrayToTree(listData,'0');
            layui.use('tree', function(){
                var tree = layui.tree;

                //渲染
                var inst1 = tree.render({
                    elem: '#layuitree'  //绑定元素
                    ,data: zNodes
                  //  ,edit: ['update', 'del']
                });
            });
        },



        // 操作zTree组件
        zTreeReady: function(listData){
            var setting = {
                view: {
                    addHoverDom: addHoverDom,
                    removeHoverDom: removeHoverDom,
                },
                edit: {
                    enable: true,
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: onClick,
                    onExpand: onExpand,
                    onCollapse: onCollapse,
                    beforeEditName: beforeEditName,
                    beforeRemove: beforeRemove,
                }
            };

            function onClick(event, treeId, treeNode, clickFlag) {
                var tNode = $("[tree-id='"+treeNode.id+"']");
                if(self.options.treeActive != null){
                    self.options.treeActive.removeClass("tree-active");
                }
                self.options.treeActive = tNode;
                tNode.addClass("tree-active");
                $(document).scrollTop(tNode.offset().top-self.options.scrollTop);

            }

            function onExpand(event, treeId, treeNode) {
                var tNode = $("[tree-id='"+treeNode.id+"']");
                self.expandChild(tNode, true);
            }

            function onCollapse(event, treeId, treeNode) {
                var tNode = $("[tree-id='"+treeNode.id+"']");
                self.expandChild(tNode, false);
            }

            function addHoverDom(treeId, treeNode){
                var node = $("#" + treeNode.tId + "_span");
                if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
                var addNode = "<span class='button tree-add' id='addBtn_" + treeNode.tId +"'></span>";
                node.after(addNode);
                var btn = $("#addBtn_"+treeNode.tId);
                if (btn) btn.bind("click", function(){
                    var popup = $(".popup-add");
                    var url = popup.data('url');
                    popup.attr("data-url", url + "/" + treeNode.id);
                    popup.click();
                    popup.attr("data-url", url);
                    return false;
                });
            }

            function removeHoverDom(treeId, treeNode) {
                $("#addBtn_"+treeNode.tId).unbind().remove();
            }

            function beforeEditName(treeId, treeNode) {
                var trNode = $("[tree-id='"+ treeNode.id +"']");
                var edit = trNode.find(".popup-edit");
                edit.click();
                return false;
            }

            function beforeRemove(treeId, treeNode) {
                var trNode = $("[tree-id='"+ treeNode.id +"']");
                var del = trNode.find(".popup-delete");
                del.click();
                return false;
            }

            // 封装zTree数据
            var zNodes = [];
            listData.forEach(function (val) {
                var nav = {
                    id: val.id,
                    pId: val.pid,
                    name: val.title
                };
                if(nav.pId == 0){
                    nav.isParent = true;
                    nav.open = true;
                }
                zNodes.push(nav);
            });

            // zTree组件初始化
            $(document).ready(function(){
                $.fn.zTree.init(self.options.tree.find(".ztree"), setting, zNodes);
            });
        },

        // 封装树形表格
        tableTree: function(listData){
            // 封装树形结构数据
            var newList = [];
            var treeList = [];

            listData.forEach(function (item) {
                if(item .name) {
                    item.title = item.name;
                }
                newList[item.id] = item;
            });

            listData.forEach(function (item) {
                if(newList[item.pid] != undefined){
                    if(JSON.stringify(newList[item.pid].children) == "{}" || !newList[item.pid].children){
                        newList[item.pid].children = [];
                    }
                    newList[item.pid].children.push(item);
                }else{
                    treeList.push(item);
                }
            });
            console.log(treeList);
            // 获取表格展示模型
            var tbody = $(self.options.treeTable+" tbody");
            var template = tbody.html();
            tbody.empty();
            tbody.css("visibility", "visible");

            // 填充数据
            var regex=/\{\{([$A-Za-z]+?)\}\}/g;
            var rank = 1;
            self.expandTree(treeList, rank, function (item, rank) {
                var callback = template.replace(regex,function($1){
                    var point = $1.substring(2,$1.length-2);
                    if(point == "title" || point == "name"){
                        var icon = self.options.treeIcon;
                        if(item.children == undefined){
                            icon = self.options.treeFill;
                        }
                        var fill = "";
                            for(var i=0; i<rank-1; i++){
                                fill += self.options.treeFill;
                            }
                        return fill + icon + item[point];
                    }else if(point == '$hide'){
                        var isHide = (rank >= self.options.hideRank);
                        return isHide ? "tree-hidd" : "";
                    }else{
                        return item[point];
                    }
                });
                tbody.append(callback);
            });
        },

        // 展开树形数据
        expandTree: function (list, rank, callback) {
            list.forEach(function(item){
                callback(item, rank);
                if(JSON.stringify(item.children) != "{}" && item.children){
                    self.expandTree(item.children, ++rank, callback);
                    rank -= 1;
                }
            });
        },

        // 树形表格子级开关
        toggleChild: function() {
            $(".toggle-icon").click(function (){
                var trNode = $(this).parents("tr");
                var id = trNode.attr("tree-id");
                var childs = $("[tree-pid='"+id+"']");
                var isClose = childs.hasClass("tree-hidd");
                self.expandChild(trNode, isClose);
            });
        },

        // 递归所有子级开关
        expandChild :function (trNode, isClose){
            var id = trNode.attr("tree-id");
            var childs = $("[tree-pid='"+id+"']");
            if(!isClose){
                childs.addClass("tree-hidd");
                childs.each(function (key, item) {
                    self.expandChild($(item), isClose);
                });
            }else {
                childs.removeClass("tree-hidd");
            }
        }
    }

    $.fn.dolphinTree=function(param){
        var tableTree=new TableTree(param);
        return tableTree.init();
    }
})(jQuery);

// 树形选择器
(function($){
    var self;
    var SelectTree=function(param){

        self=this;
        this.defaults={
            tree: $(".select-tree"),
            rootTree: null,
            initVal:'',
            onSelected: function () {}
        }
        this.options=$.extend({},this.defaults,param);
    }

    SelectTree.prototype={
        // 初始化
        init: function(){
            // 获取树形列表数据

            var tree = self.options.tree;
            // 构造悬浮选择器
            self.selector();
            // 重构选择框
            self.resetSelect(tree);
            // 点击时显示悬浮选择器
            tree.click(function(){
                var node = $(this);
                $.get(node.data('url'),function(result){
                    //if(result.data.length > 0){
                        // 显示定位悬浮选择器
                        self.position(node);
                        // zTree传递列表数据
                     //   self.zTreeReady(result.data, node)
                        var  listData = result.data;

                       self.zLayuiTree(listData, node);


                    //}
                });
            });
        },

        arrayToTree: function (arr, pId) {
            //  arr 是返回的数据  parendId 父id
            var temp = [];
            var treeArr = arr;
            treeArr.forEach((item, index) => {
                if(item.name){
                item.title= item.name;
                 }
                if (item.pid == pId) {
                    item.spread = true;
                    if (self.arrayToTree(treeArr, treeArr[index].id).length > 0) {
                        // 递归调用此函数
                        treeArr[index].children = self.arrayToTree(treeArr, treeArr[index].id);
                    }
                    temp.push(treeArr[index]);
                }
            });
            return temp;
        },
        zLayuiTree: function(listData,node){
            // 封装zTree数据
            var zNodes = self.arrayToTree(listData,'0');
         //   console.log(zNodes);
            var zLayuiNodes = {id:0,title:'顶级菜单',spread:true};
            zLayuiNodes.children=zNodes;


            var temp = [];
            temp.push(zLayuiNodes);
           // console.log(zLayuiNodes);
            layui.use('tree', function(){
                var tree = layui.tree;
                //渲染
                var inst1 = tree.render({
                    elem: '#layuitree'  //绑定元素
                    ,data: temp
                    //  ,edit: ['update', 'del']
                    ,click: function(obj){
                       var  treeNode =obj.data;
                        // console.log(obj.data); //得到当前点击的节点数据
                        // console.log(obj.state); //得到当前节点的展开状态：open、close、normal
                        // console.log(obj.elem); //得到当前节点元素
                        //
                        // console.log(obj.data.children); //当前节点下是否有子节点

                        node.val(treeNode.title);
                        node.siblings("[type='hidden']").val(treeNode.id);
                        $(".selectContent").hide();
                        self.options.onSelected(treeNode);
                    }
                });
            });
        },



        // 操作zTree组件
        zTreeReady: function(listData, node){
            var setting = {
                view: {
                    dblClickExpand: false
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: function(event, treeId, treeNode){
                        node.val(treeNode.name);
                        node.siblings("[type='hidden']").val(treeNode.id);
                        $(".selectContent").hide();
                        self.options.onSelected(treeNode);
                    }
                }
            };

            // 封装zTree数据
            var zNodes = [];
            if(self.options.rootTree != null){
                zNodes.push({id: 0, name: self.options.rootTree, open: true});
            }
            listData.forEach(function (val) {
                var nav = {
                    id: val.id,
                    pId: val.pid,
                    name: val.title
                };
                if(nav.pId == 0){
                    nav.isParent = true;
                    nav.open = true;
                }
                zNodes.push(nav);
            });

            $(document).ready(function(){
                $.fn.zTree.init($(".selectContent>.ztree"), setting, zNodes);
            });
        },

        // 构造悬浮选择器
        selector: function () {
            $("body").append("\n" +
                "<div class='selectContent'>" +
                "    <ul class='ztree' id='layuitree'></ul>" +
                "</div>");
        },

        // 重构选择框
        resetSelect: function(tree){
            tree.each(function (key, item) {
                var name = $(item).attr("name");
                var value = $(item).data("value");
                var name2 = $(item).data("name");
                if (name2) {
                    $(item).attr("readonly", true);
                    var input = $("<input name='"+name2+"' type='hidden'>");
                    if(value != undefined) input.val(value);
                    $(item).after(input);
                    $(item).after("<i class='layui-edge'></i>");
                }else {
                    $(item).removeAttr("name");
                    $(item).attr("readonly", true);
                    var input = $("<input name='"+name+"' type='hidden'>");
                    if (value != undefined) input.val(value);
                    $(item).after(input);
                    $(item).after("<i class='layui-edge'></i>");
                }
            });
        },

        // 显示定位悬浮选择器
        position: function (tree) {
            var source = self.options.tree;
            var offset = tree.offset();
            $(".selectContent").css({
                top: offset.top + tree.outerHeight() + 'px',
                left: offset.left + 'px',
                width: source.innerWidth()
            }).show();

            $("body").bind("click", function (e) {
                var target = $(e.target).parents(".selectContent");
                if(!target.length > 0){
                    $(".selectContent").hide();
                }
            });
        },
    }

    $.fn.selectTree=function(param){
        var selectTree=new SelectTree(param);
        return selectTree.init();
    }
})(jQuery);
