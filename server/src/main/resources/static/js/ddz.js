function changeHeadPicShow(){
	let imgAddr = "/img/head/" + $('#r_headPic').val() + ".jpeg"
	$('#r_headPicShow').prop("src", imgAddr);
}

function registerUser(){
	if($('#r_username').val().length < 2 || $('#r_username').val().length > 20 ){
		$('#r_username').focus();
		alert("请认真填写你的用户名");
		return;
	}
	if($('#r_password').val().length < 2 || $('#r_password').val().length > 20 ){
		$('#r_password').focus();
		alert("请认真填写你的密码");
		return;
	}
	if($('#r_nickname').val().length < 2 || $('#r_nickname').val().length > 20 ){
		$('#r_nickname').focus();
		alert("请认真填写你的昵称");
		return;
	}
	if($('#r_telphone').val().length < 2 || $('#r_telphone').val().length > 20 ){
		$('#r_telphone').focus();
		alert("请认真填写你的手机号码");
		return;
	}
	

	let data = {
		"username": $('#r_username').val(),
		"password": $('#r_password').val(),
		"nickname": $('#r_nickname').val(),
		"telphone": $('#r_telphone').val(),
		"sex": $('#r_sex').val(),
		"headPic": $('#r_headPic').val()
	}
	
	
	$.ajax({
      	type: "post",
      	url: "/client/user/register",
		dataType : "json",
        contentType : "application/json",
        data: JSON.stringify(data),
		success: function (result) {
        	if(result.code != 0){
		        alert(result.msg);
				return ;
			}
	        alert("注册成功，你可以登录游戏啦!");
      	},
      	error: function (jqXHR, textStatus, errorThrown) {
            alert(textStatus);
        }
    });
}

var logoinToken;
function login(){
	if($('#r_username').val().length < 2 || $('#r_username').val().length > 20 ){
		$('#r_username').focus();
		alert("请认真填写你的用户名");
		return;
	}
	if($('#r_password').val().length < 2 || $('#r_password').val().length > 20 ){
		$('#r_password').focus();
		alert("请认真填写你的密码");
		return;
	}

	let data = {
		"username": $('#r_username').val(),
		"password": $('#r_password').val()
	}
	
	
	$.ajax({
      	type: "post",
      	url: "/client/user/login",
		dataType : "json",
        contentType : "application/json",
        data: JSON.stringify(data),
		success: function (result) {
        	if(result.code != 0){
		        alert(result.msg);
				return ;
			}
			logoinToken = result.data
	        alert("登录成功，你可以进入游戏啦!");
      	},
      	error: function (jqXHR, textStatus, errorThrown) {
            alert(textStatus);
        }
    });
}


function logout(){
	$.ajax({
      	type: "post",
      	url: "/client/user/logout",
		dataType : "json",
		success: function (result) {
        	if(result.code != 0){
		        alert(result.msg);
				return ;
			}
	        alert("登出成功，你可以走啦!");
      	},
      	error: function (jqXHR, textStatus, errorThrown) {
            alert(textStatus);
        }
    });
}




var webSocket;
function joinGameDesk(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	
	let websocketUrl = "ws://127.0.0.1:2022/websocket/ddz/" + logoinToken;
	webSocket = new WebSocket(websocketUrl);

	function onMessage(event) {
		console.log(event.data);
		let rtnData = JSON.parse(event.data);
		onDispather(rtnData);
	}

	function onOpen(event) {
		//一旦链接开始，尝试发出进入游戏
		let reqJoinCmd = {
			"code": 101003,
			"playKind": $('#j_playerKind').val(),
			"deskNo": $('#j_deskNo').val()
		}
		
		let sendMessage = JSON.stringify(reqJoinCmd);
		webSocket.send(sendMessage);
	}

	function onError(event) {
		alert(event.data);
	}
	
	webSocket.onerror = function(event) {
		onError(event)
	};
	webSocket.onopen = function(event) {
		onOpen(event)
	};
	webSocket.onmessage = function(event) {
		onMessage(event)
	};	
}

function leftGameDesk(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!webSocket){
		alert("请先进入游戏");
		return;
	}

	let reqLeftCmd = {
		"code": 101005,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val()
	}
	
	let sendMessage = JSON.stringify(reqLeftCmd);
	webSocket.send(sendMessage);
}

function getOnlineList(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!webSocket){
		alert("请先进入游戏");
		return;
	}

	let reqGetOnlineListCmd = {
		"code": 101004,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val()
	}
	
	let sendMessage = JSON.stringify(reqGetOnlineListCmd);
	webSocket.send(sendMessage);
}

function onDispather(rtnData){
	if(rtnData.hasOwnProperty("code") && rtnData.code != 0){
		alert(rtnData.message);
		return;
	}
	
	//FIXME 完成后需要删除
	//showMsg(JSON.stringify(rtnData));
	
	//分发处理响应字段
	switch(rtnData.cmd){
		case 101003:
	    onRtnGameInfoCmd(rtnData);
	    break;
	    
		case 1101003:
	    onPushJoinCmd(rtnData);
	    break;
	    
	    case 101005:
	    alert("你已离开游戏了！");
	    break;
	    
		case 1101005:
	    onPushLeftCmd(rtnData);
	    break;
	    
		case 101004:
	    onRtnGetOnlineListCmd(rtnData);
	    break;
	    
	    
	}
}

function showMsg(message){
	$('#s_show').html($('#s_show').html() + "<br/>" + message);
}

/////////////---------具体游戏部分--------///////////////////////
//管理员的id
var playerId;
//暂停时长(毫秒)
var pauseMs;
//管理员的id
var managerId;
//当前的席位
var currentPosition;

//当前的游戏信息
var extGameInfo = {};

function onRtnGameInfoCmd(rtnCmd){
	console.log("准备处理进入游戏 , " + JSON.stringify(rtnCmd));
	if(rtnCmd.pauseMs > 0){
		showMsg("游戏还需要暂停毫秒:" + rtnCmd.pauseMs);
	}
	
	//@45@78@1
	var strList = rtnCmd.address.split("@");
	if(strList.length=4){
		currentPosition = strList[3];
		$('#s_position').val(currentPosition);
	}	
	managerId = rtnCmd.managerId;
	playerId = rtnCmd.playerId;
	
	extGameInfo.currentProgress = rtnCmd.currentProgress;
	extGameInfo.surrenderPosition = rtnCmd.surrenderPosition;
	extGameInfo.commonCards = rtnCmd.commonCards;
	extGameInfo.landlordPosition = rtnCmd.landlordPosition;
	extGameInfo.currentPosition = rtnCmd.currentPosition;
	extGameInfo.battlefield = rtnCmd.battlefield;
	extGameInfo.doubleCount = rtnCmd.doubleCount;
	extGameInfo.landlordPlayCardCount = rtnCmd.landlordPlayCardCount;
	extGameInfo.farmerPlayCardCount = rtnCmd.farmerPlayCardCount;
	
	//渲染
	if(extGameInfo.currentProgress == "ready"){
		$('#p_currentProgress').css("background","yellow");
		return;
	}
	
	//加载底牌
	$('#p_currentProgress').html('');
	extGameInfo.commonCards.forEach(function(element) {
		//console.log(element);
		let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
		$('#p_currentProgress').html($('#p_currentProgress').html() + imgHtml);
	});
	if(extGameInfo.currentProgress == "sended"){
		$('#p_currentProgress').css("background","blue");
		return;
	}
	
	$('#p_landlord_position').html(extGameInfo.landlordPosition);
	$('#p_doubleCount').html(extGameInfo.doubleCount);
	if(extGameInfo.currentProgress == "robbedLandlord"){
		$('#p_currentProgress').css("background","orange");
		return;
	}
	
	currentPosition = extGameInfo.currentPosition;
	$('#s_position').val(currentPosition);
	
	
	//设置已出过的牌
	$("#p_deskPanel").html('');
	if(extGameInfo.currentProgress == "gameover"){
		$('#p_currentProgress').css("background","red");
		
//		<span class="farmer_item">
//			<img alt="1" class="headPic_item" src="/img/head/1.jpeg">张三 
//		</span>
//		<img alt="3" class="pkPic_item" src="/img/pk/30.png">
//		<img alt="4" class="pkPic_item" src="/img/pk/31.png">
//		<img alt="5" class="pkPic_item" src="/img/pk/33.png">
//		<hr/>
		extGameInfo.battlefield.forEach(function(element) {
			//console.log(element);
			//let imgHtml = '<img alt="'+ element +'" class="headPic_item" src="/img/head/' + element + '.jpeg">';
			
			//地主;
			if(element.position == extGameInfo.landlordPosition){
				let spanHtml = '<span class="landlord_item">' + element.position +': </span>';
				$("#p_deskPanel").html($("#p_deskPanel").html() + spanHtml);
			}
			else{
				let spanHtml = '<span class="farmer_item">' + element.position +': </span>';
				$("#p_deskPanel").html($("#p_deskPanel").html() + spanHtml);
			}
			if(element.cards){
				element.cards.forEach(function(subElement) {
					let imgHtml = '<img alt="'+ subElement +'" class="pkPic_item" src="/img/pk/' + subElement + '.png">';
					$('#p_currentProgress').html($('#p_currentProgress').html() + imgHtml);
				});
			}
			$('#p_currentProgress').html($('#p_currentProgress').html() + '<hr/>');
		});
	}
	$('#p_currentProgress').html($('#p_currentProgress').html() + '<hr/>');
	
	if(extGameInfo.currentProgress == "surrender"){
		$('#p_currentProgress').css("background","darkred")
	}
}

function onPushJoinCmd(pushCmd){
	let divHtml = '<div><img alt="'+ pushCmd.player.id +'" class="headPic_item" src="/img/head/' + pushCmd.player.headPic + '.jpeg">'+ pushCmd.player.nickname +' 进入游戏了！</div>';
	showMsg(divHtml);
}
function onPushLeftCmd(pushCmd){
	let divHtml = '<div><img alt="'+ pushCmd.playerId +'" class="headPic_item" src="/img/head/' + pushCmd.headPic + '.jpeg">'+ pushCmd.nickname +' 离开游戏了！</div>';
	showMsg(divHtml);
}
function onRtnGetOnlineListCmd(rtnCmd){
	rtnCmd.list.forEach(function(element) {
		let divHtml = '<div><img alt="'+ element.id +'" class="headPic_item" src="/img/head/' + element.headPic + '.jpeg">'+ element.nickname +' 在游戏中</div>';
		showMsg(divHtml);
	});
				
}



	