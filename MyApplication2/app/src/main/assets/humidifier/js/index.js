$(function(){
	var init = {
		/*基础*/
		base : function(){
			//背景渐变
			var $W     = $(window).width(),
			    $H     = $(window).height(),
			    canvas = document.getElementById('page-bg');
			if ( ! canvas || ! canvas.getContext ) { return false; }
			var ctx = canvas.getContext('2d');
			ctx.beginPath();
			/* 指定渐变区域 */
			var grad  = ctx.createLinearGradient(0,0, 140,140);
			/* 指定几个颜色 */
			grad.addColorStop(0,'#fff');    // 红
			grad.addColorStop(0.3,'#66b4ec'); // 绿
			grad.addColorStop(1,'#1a5a82');  // 紫
			/* 将这个渐变设置为fillStyle */
			ctx.fillStyle = grad;
			/* 绘制矩形 */
			ctx.rect(0,0, $W,$H);
			ctx.fill();
			//ctx.fillRect(0,0, 140,140);


			//温度动画
			$('#thermometer-animate').circleProgress({
			    value: 0.67,
			    size: 280,
			    startAngle : 1.5,
			    reverse : false,
			    emptyFill : "rgba(0, 0, 0, .7)",
			    lineCap : "round",
			    fill: {
			        gradient: ["#fcfc58","#fff", "#83fdfd"]
			    }
			});	
			init.defulatH();
			$(window).resize(function(){
				init.defulatH();
			});
			//触摸点击
			clickStyle({box:".nav-tools li:not(.forbidden),.time-bg",class:"opacity8"});
			init.event();
		},
		/*事件*/
		event : function(){
			//开关事件
			$("body").on("click",".nav-tools li.switch",function(){
				if($(this).hasClass("off")){
					$(".nav-tools").find("li").removeClass("off");
				}else{
					$(".nav-tools").find(".underway").click();
					$(".nav-tools").find("li").addClass("off");
				}
			});
			//滑动功能
			$(".slide").bind("click",function(){
				//电源关闭功能禁止
				if($(this).closest(".nav-tools").length!=0&&$(".switch").hasClass("off")){
					return;
				}else if($(this).closest(".appointment").length!=0&&$(".time-bg").hasClass("off")){
					return;
				}
				var Slide  = new slideF(),
					$li     = $(this).find("li"),
					$active = $(this).find(".active"),
					index	= $active.index(),
					$height = $active.height();
				if($(this).hasClass("underway")){
					$(this).removeClass("underway");
					$li.css({display:"none",top:0});
					$active.css({display:"block"});
					$(this).siblings(".caption").css({visibility:"visible"});
					//取消绑定滑动功能事件
					Slide.endSlideF({box:$(".slide").find("ul")});
				}else{//增加滑动功能
					$(this).addClass("underway");
					$li.css({display:"block",top:-index*($height/2)});
					$(this).siblings(".caption").css({visibility:"hidden"});
					Slide.startSlideF({box:$(this).find("ul"),li:$li,height:$height});
				}
			});
			//时间事件
			$("body").on("click",".time-bg",function(){
				if($(this).hasClass("off")){
					$(this).removeClass("off")
				}else{
					$(".appointment").find(".underway").click();
					$(this).addClass("off");
				}
			});
		},
		defulatH : function(){
			var $H 		= $(window).height(),
				$height = $(".appointment-box").height();
			$("main").css({minHeight:$H-$height});
		}
	};
	init.base();
	
})
/*触摸点击事件*/
function clickStyle(info){
    $(info.box).on("touchstart",function(){
		$(this).addClass(info.class);
	}
    );
    $(info.box).on("touchend",function(){
    	var self = $(this);
	    setTimeout(function(){
	    	self.removeClass(info.class);
	    },100);
    });
}
/*滑过方法*/
function slideF(info){};
//启动滑动事件
slideF.prototype.startSlideF = function(info){
	//选择时间
	$(info.box).bind("touchstart",function(e){
		$(info.box).closest(".opacity8").removeClass("opacity8");
        window.defaultY = window.endY = window.startY = event.touches[0].pageY;
        $("body").css({overflow:"hidden"});
	}).bind("touchmove",function(e){
		var $top = parseInt(info.li.css("top"));
        window.endY = event.touches[0].pageY;
        //获取值
        $top+=(endY-startY);
        //判断top值，阻止溢出
        if($top>0){
    		$top = 0;
        }else if($top<(-info.li.length+1)*(info.height/2)){
        	$top = (-info.li.length+1)*(info.height/2);
        }
        //设置top值
        info.li.css({top:$top});
        //切换当前选中
        var index = -($top+info.height/4)/(info.height/2);
        if(index-parseInt(index)>0){
        	info.box.find("li").eq(parseInt(index)+1).addClass("active").siblings("li").removeClass("active");
		}else{
			info.box.find("li").eq(parseInt(index)).addClass("active").siblings("li").removeClass("active");
		}
        startY = endY;
	}).bind("touchend",function(e){
	    $("body").css({overflow:"auto"});
		//若是没有移动，则退出
		if(defaultY==endY){
			return;
		}
		var $top = parseInt(info.li.css("top")),
			index = -($top+info.height/4)/(info.height/2);
		$top = -parseInt(index+1)*(info.height/2);
		//赋值
		info.li.animate({top:$top},500,function(){
			if($(this).closest(".underway").length==0){
				$(this).css({top:0});
			}
		});
	});
};
//取消滑动事件
slideF.prototype.endSlideF = function(info){
	$(info.box).unbind("touchstart");
	$(info.box).unbind("touchmove");
	$(info.box).unbind("touchend");
	if(!!info.callback){
		info.callback();
	}
};