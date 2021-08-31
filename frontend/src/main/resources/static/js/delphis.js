// 监听滚动条
$(function(){
    var navbar = $('.navbar');
    $(window).scroll(function(event){
        var top = $(document).scrollTop();
        if(top > 52) {
            navbar.addClass('navbar-solid');
        }
        if(top <= 52) {
            navbar.removeClass('navbar-solid');
        }
    });
    $(window).scroll();
});