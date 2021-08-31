function uaredirect(f) {
    try {
        if (document.getElementById("bdmark") != null) {
            return
        }
        var b = false;
        if (arguments[1]) {
            var e = window.location.host;
            var a = window.location.href;
            if (isSubdomain(arguments[1], e) == 1) {
                f = f + "/#m/" + a;
                b = true
            } else {
                if (isSubdomain(arguments[1], e) == 2) {
                    f = f + "/#m/" + a;
                    b = true
                } else {
                    f = a;
                    b = false
                }
            }
        } else {
            b = true
        }
        if (b) {
            var c = window.location.hash;
            if (!c.match("fromapp")) {
                if ((navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i))) {
                    location.replace(f)
                }
            }
        }
    } catch(d) {}
}
function isSubdomain(c, d) {
    this.getdomain = function(f) {
        var e = f.indexOf("://");
        if (e > 0) {
            var h = f.substr(e + 3)
        } else {
            var h = f
        }
        var g = /^www\./;
        if (g.test(h)) {
            h = h.substr(4)
        }
        return h
    };
    if (c == d) {
        return 1
    } else {
        var c = this.getdomain(c);
        var b = this.getdomain(d);
        if (c == b) {
            return 1
        } else {
            c = c.replace(".", "\\.");
            var a = new RegExp("\\." + c + "$");
            if (b.match(a)) {
                return 2
            } else {
                return 0
            }
        }
    }
};

$(function(){


	//优化数据选项卡
	$('.flinksbtn').find('em').mouseover(function(){
		$(this).attr('class','current').siblings('em').attr('class','');
		$('.flinks').find('ul').eq( $(this).index() ).css('display','block').siblings('ul').css('display','none');
	});
	//end
    if($('.conundrum .left-bottom').length > 0){

        var cookieXhy = getCookie('conundrum');
        if(cookieXhy != '' ){
            $('.conundrum .left-bottom').parents(".conundrum").addClass('on');
        }else{
            $('.conundrum .left-bottom').parents(".conundrum").removeClass('on');
        }
    }

    if($('.answer .left-bottom').length > 0){

        var cookieAnswer = getCookie('answer');
        if(cookieAnswer != '' ){
            $('.answer .left-bottom').parents(".answer").addClass('on');
        }else{
            $('.answer .left-bottom').parents(".answer").removeClass('on');
        }
    }

    for(var j=0;j<$('.carousel_img li').length;j++){
        $('.carousel .num').append('<li><i></i></li>');
    }
    $('.carousel .num li').first().addClass('active');
     var i = 0;
     var index = 0;
    $('.carousel .num li').mouseover(function(){
        $(this).addClass('active').siblings().removeClass('active');
        var index = $(this).index();
        $('.carousel_img li').eq(index).stop(true,true).fadeIn(300).siblings().fadeOut(300);
        var txt = $('.carousel_img li').eq(index).find('img').attr('alt');
        $('.carousel .txt').html(txt);
        return i=index;
    })
    $('.carousel .right').click(function() {
        startMOve()
    });
     $('.carousel .left').click(function() {
        startMOve2()
    });

    function startMOve(){
        i++;
        if(i == $('.carousel_img li').length){
            i=0
        }
        $('.carousel_img li').eq(i).stop(true,true).fadeIn(300).siblings().fadeOut(300);
        $('.carousel .num li').eq(i).addClass('active').siblings().removeClass('active');
        var txt = $('.carousel_img li').eq(i).find('img').attr('alt');
        $('.carousel .txt').html(txt);
    }
    function startMOve2(){
        i--;
        if(i < 0){
            i=$('.carousel_img li').length;
        }
        $('.carousel_img li').eq(i).stop(true,true).fadeIn(300).siblings().fadeOut(300);
        $('.carousel .num li').eq(i).addClass('active').siblings().removeClass('active');
        var txt = $('.carousel_img li').eq(i).find('img').attr('alt');
        $('.carousel .txt').html(txt);
    }
    var timer = setInterval(startMOve,3000)
    $('.carousel').hover(function(){
        clearInterval(timer)
    },function(){
        timer = setInterval(startMOve,3000)
    });

    $('.nav_list li').mouseover(function(){
        $(this).addClass('active').siblings().removeClass('active');
        var navid = $(this).data('id');
        $('#'+ navid).show().siblings().hide();
    })

    $('.items7 .col5 .row').last().css('margin-bottom','0');
    $('.current-location li').eq(0).css('margin-right','5px');
    //$('.wrap .nav li').eq(0).find('a').css('padding','0 30px');

    if($('.gfixed').length > 0){
        var topTohere = $('.gfixed').position().top;
        $(window).scroll(function() {
            if($(window).scrollTop() > topTohere && ($('.section-left').height() > (topTohere - 200))){
                $('.gfixed').addClass('fixed');
            }else{
                $('.gfixed').removeClass('fixed');
            }
        });
    }
    if($('.ad-fixed').length > 0){
        var topTohere2 = $('.ad-fixed').position().top;
        $(window).scroll(function() {
            if($(window).scrollTop() > topTohere2 && ($('.section-left').height() > (topTohere2 - 200))){
                $('.ad-fixed').addClass('fixed2');
            }else{
                $('.ad-fixed').removeClass('fixed2');
            }
        });
    }
    for(var i= 0;i<$('.message-con .items-two .con').size();i++){
        $('.message-con .items-two .con').eq(i).find('ul').find('.icon').eq(0).css({'background-position':'-335px -175px','color':'#fff'});
        $('.message-con .items-two .con').eq(i).find('ul').find('.icon').eq(1).css({'background-position':'-306px -175px','color':'#fff'});
        $('.message-con .items-two .con').eq(i).find('ul').find('.icon').eq(2).css({'background-position':'-247px -175px','color':'#fff'});
        $('.message-con .items-two .con').eq(i).find('li').eq(4).css({'border':'none'});
        // for(var i= 0;i<$('#items-two .con li').size();i++){
        //    $('#items-two .con li').eq(i).find().eq(i).find('.icon').html(i);
        // }
    }
    // 滑动开始
   var messageSize = $('.carousel-box li').size();
   $('.carousel-message .carousel-box').width(messageSize*460)
   for(var j=0;j<$('.carousel-box li').length;j++){
        $('.carousel-message .num').append('<li><i></i></li>');
    }
    $('.carousel-message .num li').first().addClass('active');
     var a = 0;
     var b = 0;
    $('.carousel-message .num li').mouseover(function(){
        $(this).addClass('active').siblings().removeClass('active');
        a = $(this).index();
        $('.carousel-box').stop(true,true).animate({'left':-(a*460)});
        var txt = $('.carousel-box li').eq(index).find('img').attr('alt');
        $('.carousel-message .txt').html(txt);
        b=a;
    })
    $('.carousel-message .btn-right').click(function() {
        startMOve3()
    });

     $('.carousel-message .btn-left').click(function() {
        startMOve4()
    });

    function startMOve3(){
        b++;
        if(b > $('.carousel-box li').length-1){
            b=0
        }
        $('.carousel-box').stop(true,true).animate({'left':-(b*460)});
        $('.carousel-message .num li').eq(b).addClass('active').siblings().removeClass('active');
        var txt = $('.carousel-box li').eq(b).find('img').attr('alt');
        $('.carousel-message .txt').html(txt);
    }
    function startMOve4(){
        b--;
        if(b < 0){
            b=$('.carousel-box li').length-1;
        }
        $('.carousel-box').stop(true,true).animate({'left':-(b*460)});
        $('.carousel-message .num li').eq(b).addClass('active').siblings().removeClass('active');
        var txt = $('.carousel-box li').eq(b).find('img').attr('alt');
        $('.carousel-message .txt').html(txt);
    }
    var timer = setInterval(startMOve3,3000)
    $('.carousel-message .carousel-box').hover(function(){
        clearInterval(timer)
    },function(){
        timer = setInterval(startMOve3,3000)
    });

    //滑动结束
    var c = 0;
    $('.qh-right').click(function() {
        c++;
        if(c > $('.qh-box span').length-1){
            c = 0;
        }
        $('.news-message').eq(c).show().siblings('.news-message').hide();
        $('.qh-box span').eq(c).addClass('cur').siblings('span').removeClass('cur');

    });
    $('.qh-left').click(function() {
        c--;
        if(c < 0){
            c = $('.qh-box span').length-1;
        }
        $('.news-message').eq(c).show().siblings('.news-message').hide();
        $('.qh-box span').eq(c).addClass('cur').siblings('span').removeClass('cur');

    });

    for (var i = 0; i < $('.news-message').length; i++) {
           $('.news-message').eq(i).find('li').last().css('border','none');
    };

    for (var i = 0; i < $('.message-all li').length; i++) {
           $('.message-all li').not('.intro').eq(i).find('.message-type-box').css({'width':$('.message-all li').width()-($('.message-all li h3').eq(i).width())-40})
    };

    for (var i = 0; i < $('.message-type-box').length; i++) {
           if($('.message-type-box').eq(i).height()>28){
                $('.message-type-box').eq(i).parent().append('<span class="btn-more">更多<i class="icon"></i></span>');
           }

            $('.message-type-box').eq(i).siblings('span').toggle(function(){
                $(this).parent().css('height','auto');
                $(this).html('收起<i class="icon2"></i>');
            },function(){
                $(this).parent().css('height','28');
                $(this).html('更多<i class="icon"></i>');
            });
       };
	$('.message-all li').eq(1).css({'width':'50%','float':'left'});
	$('.message-all li').eq(1).find('.message-type-box').css('width','auto');
	$('.message-all li').eq(2).css('width','50%');
	$('.message-all li').eq(2).find('.message-type-box').css('width','auto');
    $('.message-all .intro').first().css('width','550px');

    $('.xhy-con .taba li').mouseover(function() {
        var index = $(this).index();
        $(this).addClass('active').siblings().removeClass('active');
        $('.tablistbox .content').eq(index).show().siblings('.content').hide();
    });

     // for (var i = 0; i < $('.xhy-ul .left').length; i++) {
     //      $('.xhy-ul .tonow').eq(i).width(622-$('.xhy-ul .now').eq(i).width()-42);
     //      $('.xhy-ul .now').eq(i).width(622-$('.xhy-ul .tonow').eq(i).width()-42);
     // };

     $('.xhy-bottom-nav li').click(function() {
         var index = $(this).index();
         $(this).addClass('active').siblings().removeClass('active');
         $('.xhy-bottom-list').eq(index).show().siblings('.xhy-bottom-list').hide();
     });

     // 显示隐藏拼音
       $('.conundrum .left-bottom').click(function(){
             if($('.conundrum').hasClass('on')){
                  setCookie('conundrum','');
                  $(this).parents(".conundrum").removeClass('on');
                  $(this).text('隐藏拼音');
				  $(".right-bottom .an rb").css("height",90)
             }else{
                setCookie('conundrum','conundrum_true');
                $(this).parents(".conundrum").addClass('on');
                $(this).text('显示拼音');
				$(".right-bottom .an rb").css("height",70)
             }
        })

    //显示隐藏谜底

        $('.answer .left-bottom').click(function(){
             if($('.answer').hasClass('on')){
                  setCookie('answer','');
                  $(this).parents(".answer").removeClass('on');
                  $(this).text('隐藏谜底');
             }else{
                 setCookie('answer','answer_true');
                $(this).parents(".answer").addClass('on');
                $(this).text('显示谜底');
             }
        })

      $('.riddle-bottom-nav li').mouseover(function(){
         var index = $(this).index();
         $(this).addClass('active').siblings().removeClass('active');
         $('.riddle-bottom-list').eq(index).show().siblings('.riddle-bottom-list').hide();
     });


    function setCookie(name,value){

        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        document.cookie = name+"="+escape(value)+";expires="+exp.toGMTString();
    }

    function getCookie(c_name){

        if(document.cookie.length>0){
            c_start=document.cookie.indexOf(c_name + "=");
            if (c_start != -1){

                c_start=c_start + c_name.length+1;
                c_end=document.cookie.indexOf(";",c_start);
                if (c_end==-1) c_end=document.cookie.length;
                return unescape(document.cookie.substring(c_start,c_end));
            }
        }
        return "";
    }
});
