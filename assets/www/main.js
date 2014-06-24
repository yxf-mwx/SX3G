$(function(){
	// location.hash = 'main';
	document.addEventListener("deviceready", onDeviceReady, false); 
	// PhoneGap is loaded and it is now safe to make calls PhoneGap methods
	function onDeviceReady() {
		// 注册回退按钮事件监听器
		document.addEventListener("backbutton",Global.handleBack.onBackBtn,false);
	}
	/*==========================
	 * alert重载
	 * ========================*/
	(function(){
		document.addEventListener("deviceready", function(){
			window.alert = function(){
				navigator.notification.alert(arguments[0],null,'消息');
			}
		}, false); 
	})();
});
/*=====================================================
 * Global命名空间
 * 页面模块匿名函数都加入命名空间中，通信使用public变量
 * 例如around模块，Global.around = new （function(){})();
 * 记得开放通信接口，通信就用
 * Global.around.sendData（a，b，c）之类的
 * ====================================================*/
Global = {};

/*===================================
公共全局数据
==================================*/
Global.common = {};
Global.common.access_token = null;
Global.common.uid = 0;
Global.common.editData = {};
Global.common.search = {};
Global.common.url = "http://localhost/MyJournal_Servlet/api/";
//zj
// Global.common.access_token = "d33f6176154ca6c079be24623fe5f440";

// Global.common.longitude = "113.404773";
// Global.common.latitude = "23.059658";

Global.common.longitude = null;
Global.common.latitude = null;

//jh
// Global.common.access_token = "6b30de9f313ad68841929bdd93b08398";
// Global.common.access_token = null;

/*=====================================================
 * 本地存储
 * ===================================================*/
//================key-value本地存储====================
function kset( key , value )
{
	window.localStorage.setItem( key , value );
}

function kget( key )
{
	return window.localStorage.getItem( key );
}

function kremove( key )
{
	window.localStorage.removeItem( key );
}


//===================数据库本地存储=================
//构造数据库表
// function populateDB(tx) {
	// tx.executeSql('CREATE TABLE IF NOT EXISTS message (id unique, from_uid , date , content)');
// }

// function errorCB(err) {
	// console.log("Error processing SQL: "+err.code);
// }

// function successCB() {
	// console.log("success!");
// }

//====================初始化数据=====================
$(function(){
	//浏览器测试
	// onDeviceReady();
	Global.common.access_token = kget('access_token');
	//手机
	// document.addEventListener("deviceready", onDeviceReady, false);
})

// function onDeviceReady() {
	// Global.DB = window.openDatabase("DOUYADB", "1.0", "DYDB", 200000);
	// Global.DB.transaction(populateDB, errorCB, successCB);
	//读出kv本地存储数据
// }

/*=============================
 * 异步加载baidu API
 * ============================*/
var MapLoader = new(function(){
	this.isReady = false;
	_funcQueue = [];

	window.onload = loadScript;

	this.ready = function(func){
		if(this.isReady == true){
			func();
		}else{
			_funcQueue.push(func);
		}
	} 

	this.initialize = function() {
		this.isReady = true;
		while(_funcQueue.length >0 ){
			var func = _funcQueue.shift();
			func();
		}
	}
	 
	function loadScript() {
	  var script = document.createElement("script");
	  script.src = "http://api.map.baidu.com/api?v=1.3&callback=MapLoader.initialize";
	  document.body.appendChild(script);
	}
	
})


/*=========================
 * 定义后退按钮
 * ========================*/
Global.handleBack = new (function(){
	var fun = {};
	this.addBack = function(){
		var a = arguments[0];
		fun[a] = arguments[1];
	}
	this.onBackBtn = function(){
		var hash = location.hash.slice(1);
		if(fun[hash]){
			fun[hash]();
		}else{
			window.history.back(1);
		}
	}
})();

/*======================
 * login模块
 * ====================*/
 Global.login = new(function(){
	$("#login").live('pageinit',function(){
	});

	$("#login").live('pageshow',function(){
		
	});
	$("#login .login_submit").live("tap",function(){
		var name = $("#username").val();
		var pwd = $("#password").val();
		var data = {}
		data.username = name;
		data.password = pwd;
		
		var url = Global.common.url + "user_login.php?username=" + name + "&password=" + pwd + "&jsoncallback=?";
		if(vaildate(name,pwd)){
			$.getJSON(url,function(data){
			//data = JSON.parse(data);
			if(!data || data < 0){
				alert('登录失败！');
			}else{
				//Global.common.access_token = data;
				Global.common.uid = parseInt(data);
				window.location.hash = "p_log";
			}
			});
		}
	});
	function vaildate(name,pwd){
		if(!name){
			alert('请输入用户名，即手机号码');
			return false;
		}else if(!pwd){
			alert('请输入密码');
			return false;
		}
		return true;
	}
 })();
 
 /*======================
 * register模块
 * ====================*/
 Global.register = new(function(){
	var name,pwd,cf,nickname;
	$("#register").live('pageinit',function(){
	});

	$("#register").live('pageshow',function(){
		
	});
	$("#register .register_submit").live("tap",function(){
		name = $("#r_username").val();
		pwd = $("#r_password").val();
		cf = $("#r_confirm").val();
		nickname = $("#r_nickname").val();
		var data = {};
		data.username = name;
		data.password = pwd;
		data.nickname = nickname;
		var url = Global.common.url + "user_register.php";
		if(vaildate(name,pwd,cf,nickname)){
			$.post(url,data,function(data){
			//data = JSON.parse(data);
			if(!data || data < 0){
				alert('注册失败！');
			}else{
				Global.common.access_token = data;
				location.hash = "p_log";
			}
			});
		}
	});
	function vaildate(name,pwd,cf,nickname){
		if(!name){
			alert('请输入用户名，即手机号码');
			return false;
		}else if(!pwd){
			alert('请输入密码');
			return false;
		}else if(!cf || (cf != pwd)){
			alert('请再次确认密码');
			return false;
		}else if(!nickname){
			alert('请输入昵称');
			return false;
		}
		return true;
	}
 })();
 
 /*======================
 * p_log模块
 * ====================*/
 Global.p_log = new(function(){
	var scrollListJDom;
	var result;
	$("#p_log").live('pageinit',function(){
		init();
	});

	$("#p_log").live('pageshow',function(){
		refresh();
	});
	$()
	//添加日志
	$("#p_log .add").live('tap',function(){
		Global.common.editData = {};
		Global.common.editData.type = 1;
	});
	//查看日志
	$("#p_log_ul .log").live('tap',function(){
		var id = $(this).attr('l_id');
		Global.common.editData.id = id;
		Global.common.editData.title = $(this).attr('l_title');
		Global.common.editData.content = $(this).attr('l_content');
		Global.common.editData.create_time = $(this).attr('l_create_time');
		/* var index = 0;
		while(result.data[index].id != id){
			index ++;
		}
		Global.common.editData = result.data[index]; */
		Global.common.editData.type = 2;
		window.location.hash = "log";
	});
	function init(){
		scrollListJDom = $("#p_log_ul").first();
		//编译模板
		$.template("PLogTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "private_log_get.php";
		$.post(url,{uid:Global.common.uid},function(result){
			var result = $.parseJSON(result);
			if(result.code != undefined && result.code == '1'){
				scrollListJDom.empty();
				$.tmpl("PLogTpml",result.data).appendTo(scrollListJDom);
				
			}
		});
		
	}
 })();

 /*======================
 * worklog模块
 * ====================*/
 Global.worklog = new(function(){
	var scrollListJDom;
	$("#worklog").live('pageinit',function(){
		init();
	});

	$("#worklog").live('pageshow',function(){
		refresh();
	});
	$()
	//查看日志
	$("#w_log_ul .log").live('tap',function(){
		var id = $(this).attr('l_id');
		Global.common.editData.id = id;
		Global.common.editData.title = $(this).attr('l_title');
		Global.common.editData.content = $(this).attr('l_content');
		Global.common.editData.create_time = $(this).attr('l_create_time');
		/* var index = 0;
		while(result.data[index].id != id){
			index ++;
		}
		Global.common.editData = result.data[index]; */
		Global.common.editData.type = 4;
		window.location.hash = "log";
	});
	function init(){
		scrollListJDom = $("#w_log_ul").first();
		//编译模板
		$.template("WLogTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "worklog_get.php";
		$.post(url,{uid:Global.common.uid},function(result){
			var result = $.parseJSON(result);
			if(result.code != undefined && result.code == '1'){
				scrollListJDom.empty();
				$.tmpl("WLogTpml",result.data).appendTo(scrollListJDom);
				
			}
		});
		
	}
 })();
 
  /*======================
 * search模块
 * ====================*/
 Global.search = new(function(){
	var scrollListJDom;
	$("#search_tag").live('pageinit',function(){
		init();
	});

	$("#search_tag").live('pageshow',function(){
		refresh();
	});
	//查看日志
				$("#s_log_ul .tag_li").live('tap',function(){
					var tag = $(this).find(".tag").attr("tag");
					if(tag == "所有"){
						Global.common.search.type = 1;
					}else{
						Global.common.search.type = 2;
					}
					Global.common.search.title = tag;
					window.location.hash = "s_log";
				});
	function init(){
		scrollListJDom = $("#s_log_ul").first();
		//编译模板
		$.template("SLogTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "tag_get.php";
		$.post(url,{uid:Global.common.uid},function(result){
			var result = $.parseJSON(result);
			if(result.code != undefined && result.code == '1'){
				result.data.unshift({"tag":"所有"});
				scrollListJDom.empty();
				$.tmpl("SLogTpml",result.data).appendTo(scrollListJDom);
				
			}
		});
	}
 })();
 
  /*======================
 * XX类型日志模块
 * ====================*/
Global.s_log = new(function(){
	var scrollListJDom;
	$("#s_log").live('pageinit',function(){
		init();
	});

	$("#s_log").live('pageshow',function(){
		$("#s_log .s_title").html(Global.common.search.title);
		refresh();
	});
	//查看日志
				$("#x_log_ul .log").live('tap',function(){
					var id = $(this).attr('l_id');
					Global.common.editData.id = id;
		Global.common.editData.title = $(this).attr('l_title');
		Global.common.editData.content = $(this).attr('l_content');
		Global.common.editData.create_time = $(this).attr('l_create_time');
		/* var index = 0;
		while(result.data[index].id != id){
			index ++;
		}
		Global.common.editData = result.data[index]; */
					Global.common.editData.type = 4;
					window.location.hash = "log";
				});
	function init(){
		scrollListJDom = $("#x_log_ul").first();
		//编译模板
		$.template("XLogTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "search.php";
		var data = {};
		data.uid = Global.common.uid;
		switch(Global.common.search.type){
			case 1:
				break;
			case 2:
				data.tag = Global.common.search.title;
				break;
		}
		$.post(url,data,function(result){
			var result = $.parseJSON(result);
				if(result.code != undefined && result.code == '1'){
				scrollListJDom.empty();
				$.tmpl("XLogTpml",result.data).appendTo(scrollListJDom);
				
			}
			
		});
		
	}
 })();
  /*======================
 * 工作群日志模块
 * ====================*/
Global.t_log = new(function(){
	var scrollListJDom;
	$("#t_log").live('pageinit',function(){
		init();
	});

	$("#t_log").live('pageshow',function(){
		$("#t_log .s_title").html(Global.common.search.team);
		refresh();
	});
	$("#t_log .add").live('tap',function(){
		Global.common.editData.type = 3;
		window.location.hash = "edit";
	})
	//查看日志
	$("#t_log_ul .log").live('tap',function(){
		var id = $(this).attr('l_id');
		Global.common.editData.id = id;
		Global.common.editData.title = $(this).attr('l_title');
		Global.common.editData.content = $(this).attr('l_content');
		Global.common.editData.create_time = $(this).attr('l_create_time');
		/* var index = 0;
		while(result.data[index].id != id){
			index ++;
		}
		Global.common.editData = result.data[index]; */
		Global.common.editData.type = 4;
		window.location.hash = "log";
	});
	function init(){
		scrollListJDom = $("#t_log_ul").first();
		//编译模板
		$.template("TLogTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "worklog_get.php";
		$.post(url,{uid:Global.common.uid,tid:Global.common.editData.tid},function(result){
			var result = $.parseJSON(result);
			if(result.code != undefined && result.code == '1'){
				scrollListJDom.empty();
				$.tmpl("TLogTpml",result.data).appendTo(scrollListJDom);
				
			}
			
		});
		
	}
 })();
 /*======================
 * 某一篇日志模块
 * ====================*/
Global.log = new(function(){
	var type;
	$("#log").live('pageinit',function(){
		init();
	});

	$("#log").live('pageshow',function(){
		refresh();
	});
	$()
	//删除日志
	$("#log .delete").live('tap',function(){
		/* var conf = navigator.notification.confirm("确定要删除此日志吗？",function(button){
				if(button == true){
					if(type == 1 || type == 2)
						window.location.hash = "p_log";
					else
						window.location.hash = "worklog";
				}
			},'提示'); */
		var conf = confirm("确定要删除此日志吗？");
		if(conf){
			var data = {};
			data.uid = Global.common.uid;
			data.id = Global.common.editData.id;
			var url = Global.common.url + "log_delete.php";
			$.post(url,data,function(result){
				if(result == 1){
					alert('删除成功');
					if(type == 1 || type == 2)
						window.location.hash = "p_log";
					else
						window.location.hash = "worklog";
				}else{
					alert("操作失败");
				}
			});
			
		}
	});

	function init(){
		type = Global.common.editData.type;
		$("#log .s_title,#log .date,#log .date,#log .content,#log .location,#log .img").html('');
	}
	function refresh(){
		var data = Global.common.editData;
		//检验是否有编辑和删除操作
		if(data.uid && data.uid != Global.common.uid){
			$("#log .edit,#log .delete").css("display","none");
		}
		$("#log .s_title").html(data.title);
		$("#log .date").html(data.create_time);
		$("#log .location").html(data.location);
		$("#log .content").html(data.content);
	}
 })(); 

  /*======================
 * 编辑日志模块
 * ====================*/
Global.edit = new(function(){
	var type;
	$("#edit").live('pageinit',function(){
		init();
	});

	$("#edit").live('pageshow',function(){console.log(Global.common.editData.tid);
		refresh();
	});
	//提交日志
	$("#edit .submit").live('tap',function(){
		var data = {};
		var url = Global.common.url + "log_add.php";
		var url2 = Global.common.url + "log_modigy.php";
		data = Global.common.editData;
		data.uid = Global.common.uid;
		data.title = $("#e_title").val();
		data.content = $("#e_content").val();
		data.tag = $("#e_tag").val();
		switch(type){
			case 1:
				data.tid = -1;
				$.post(url,data,function(result){
					if(result == 1)
						window.location.hash = "p_log";
				});
				break;
			case 2:
				data.tid = -1;
				$.post(url2,data,function(result){
					if(result == 1)
						window.location.hash = "p_log";
				});
				break;
			case 3:
				data.tid = Global.common.editData.tid;
				$.post(url,data,function(result){
					if(result == 1)
						window.location.hash = "worklog";
				});
				break;
			case 4:
				data.tid = Global.common.editData.tid;
				$.post(url2,data,function(result){
					if(result == 1)
						window.location.hash = "worklog";
				});
				break;
		}
	});
	
	$("#edit .tag_btn").live('tap',function(){
		$("#edit .add_tag").show();
	});

	function init(){
		
		$("#edit .s_title,#edit .date,#edit .content,#edit .tag").val('');
	}
	function refresh(){
		type = Global.common.editData.type;
		var data = Global.common.editData;
		if(data.type == 2 || data.type == 4){
			$("#edit .title").val(data.title);
			$("#edit .content").val(data.content);
			if(data.tag){
				$("#edit .tag").val(data.tag);
				$("#edit .add_tag").show();
			}else{
				$("#edit .add_tag").hide();
			}
		}
	}
 })(); 

 /*======================
 * 工作群模块
 * ====================*/
Global.team = new(function(){
	var scrollListJDom;
	$("#team").live('pageinit',function(){
		init();
	});

	$("#team").live('pageshow',function(){
		refresh();
	});
	$()
	
	$("#teamnames li").live('tap',function(){
		Global.common.search.team = $(this).html();
		Global.common.editData.tid = $(this).attr('tid');
		window.location.hash = "t_log";
	});

	function init(){
		scrollListJDom = $("#teamnames").first();
		//编译模板
		$.template("TeamTpml",scrollListJDom);
		scrollListJDom.empty();
	}
	function refresh(){
		var url = Global.common.url + "team_get.php";
		$.post(url,{uid:Global.common.uid},function(result){
			var result = $.parseJSON(result);
			if(result.code != undefined && result.code == '1'){
				scrollListJDom.empty();
				$.tmpl("TeamTpml",result.data).appendTo(scrollListJDom);
			}
		});
		
	}
 })();
 
  /*======================
 * 创建工作群模块
 * ====================*/
Global.create = new(function(){
	var type = Global.common.editData.type;
	var mem;
	$("#create").live('pageinit',function(){
		init();
	});

	$("#create").live('pageshow',function(){
		//refresh();
	});
	$()
	
	$("#create .add_btn").live("tap",function(){
		mem = $("#create .add_m").val();
		if(!mem){
			alert("请输入添加成员账号");
			return;
		}
		//var html = '<div class="div"><div data-role="fieldcontain"><input class="added" placeholder="成员用户账号" value="' + mem + '" type="text"></div><a data-role="button" data-inline="true" href="#page1" data-icon="delete" class="delete_btn"data-iconpos="left">删除</a></div>';
		var html = $("#create .div").first().clone().show();
		html.find(".added").val(mem);
		$("#create .add_area").before(html);
		$("#create .add_m").val('');
	});
	
	$("#create .delete_btn").live("tap",function(){
		$(this).parent().remove();
	});
	
	//提交日志
	$("#create .submit").live('tap',function(){
		var data = {};
		data.uid = Global.common.uid;
		var memArr = $("#create .added");
		data.member = "";
		memArr.each(function(){
			if(data.member){
				data.member += ",";
			}
			data.member +=  $(this).val();
		});
		data.admin = Global.common.uid;
		data.teamname = $("#create .name").val().toString();
		var url = Global.common.url + "team_create.php";
		$.post(url,data,function(result){
			if(result > 0)
				window.location.hash = "worklog";
			else
				alert("创建失败");
		});
		
	});

	function init(){
		$("#create .name,#create .add_m").val('');
	}
 })(); 