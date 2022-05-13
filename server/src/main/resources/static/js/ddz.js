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
			logoinToken = result.data;
			$('#r_logout').removeAttr('disabled');
			$('#j_join').removeAttr('disabled');
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
var connected = false;
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
			"cmd": 101003,
			"playKind": $('#j_playerKind').val(),
			"deskNo": $('#j_deskNo').val()
		}
		
		//TODO 需要发送心跳，使得socket连接不中断
		connected = true;
		

		//准备进入游戏		
		let sendMessage = JSON.stringify(reqJoinCmd);
		webSocket.send(sendMessage);
	}

	function onError(event) {
		alert(event.data);
	}
	function onClose(event) {
		connected = false;
		alert("你掉线了！");
	}
	
	webSocket.onerror = function(event) {
		onError(event);
	};
	webSocket.onclose=function (event) {
        onClose(event);
    };
	webSocket.onopen = function(event) {
		onOpen(event);
	};
	webSocket.onmessage = function(event) {
		onMessage(event);
	};	
}


function sitdown(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!$('#s_position').val()){
		alert("请先选择席位");
	}
	
	//选择座位坐下
	let reqSitdownCmd = {
		"cmd": 102001,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val()
	}
	
	//准备坐下	
	let sendMessage = JSON.stringify(reqSitdownCmd);
	webSocket.send(sendMessage);
}


function quickSitdown(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	//选择座位坐下
	let reqSitdownCmd = {
		"cmd": 102004,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val()
	}
	
	//准备快速坐下	
	let sendMessage = JSON.stringify(reqSitdownCmd);
	webSocket.send(sendMessage);
}

function standup(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!position){
		alert("你还未坐下");
		return;
	}
	
	//站起
	let reqStandUpCmd = {
		"cmd": 102007,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val()
	}
	
	//站起
	let sendMessage = JSON.stringify(reqStandUpCmd);
	webSocket.send(sendMessage);
}


function ready(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!position){
		alert("你还未坐下");
		return;
	}
	
	//选择座位坐下
	let reqReadyNextCmd = {
		"cmd": 151001,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val()
	}
	
	//准备下一局
	let sendMessage = JSON.stringify(reqReadyNextCmd);
	webSocket.send(sendMessage);
}

//心跳检测
setTimeout(sendHeartCheck(), 120000);
function sendHeartCheck(){
	if(connected){
		let heartCmd = {
			"cmd": 888888
		}
		let sendMessage = JSON.stringify(heartCmd);
		webSocket.send(sendMessage);
	}
	setTimeout(sendHeartCheck, 120000);
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
		"cmd": 101005,
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
		"cmd": 101004,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val()
	}
	
	let sendMessage = JSON.stringify(reqGetOnlineListCmd);
	webSocket.send(sendMessage);
}

function sendChat(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!webSocket){
		alert("请先进入游戏");
		return;
	}

	let reqChatCmd = {
		"cmd": 101006,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"chat":{
			"kind":"text",
			"content":$('#c_content').val()
		}
	}
	
	let sendMessage = JSON.stringify(reqChatCmd);
	webSocket.send(sendMessage);
}

function onDispather(rtnData){
	if(!rtnData.hasOwnProperty("cmd")){
		alert(JSON.stringify(rtnData));
		return;
	}
	
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
	    
	    case 101006:
	    onRtnChatCmd(rtnData);
	    break;
	    
	    case 1101006:
	    onPushChatCmd(rtnData);
	    break;
	    
	    case 2101003:
	    onPushSysChatCmd(rtnData);
	    break;
	    
	    
	    case 102001:
	    onRtnGameSeatInfoCmd(rtnData);
	    break;
	    
	    case 1102001:
	    onPushSitdownCmd(rtnData);
	    break;
	    
	    case 102007:
	    onReqStandUpCmd(rtnData);
	    break;
	    
	    case 151001:
	    showMsg("已准备好了！");
	    break;
	    
	    case 1151001:
	    onPushReadyNextCmd(rtnData);
	    break;
	    
	    
	}
}

function showMsg(message){
	$('#s_show').html($('#s_show').html() + "<br/>" + message);
}
function chatMsg(message){
	$('#c_chatPanel').html($('#c_chatPanel').html() + "<br/>" + message);
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
	$('#j_left').removeAttr('disabled');
	$('#s_sitdown').removeAttr('disabled');
	$('#s_quickSitdown').removeAttr('disabled');
	
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
	$('#p_commonCards').html('');
	extGameInfo.commonCards.forEach(function(element) {
		//console.log(element);
		let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
		$('#p_commonCards').html($('#p_currentProgress').html() + imgHtml);
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
	
	//TODO 需要判断是不是自己被踢下线了
	if(pushCmd.playerId == playerId){
		$('#p_currentProgress').css("background","black");	
	}
}
function onRtnGetOnlineListCmd(rtnCmd){
	rtnCmd.list.forEach(function(element) {
		let divHtml = '<div><img alt="'+ element.id +'" class="headPic_item" src="/img/head/' + element.headPic + '.jpeg">'+ element.nickname +' 在游戏中</div>';
		showMsg(divHtml);
	});
}

function onRtnChatCmd(rtnCmd){
	let divHtml = '<div style="text-align: right;">我说：' + $('#c_content').val() + '</div>';
	chatMsg(divHtml);
	$('#c_content').val('');	
}

function onPushChatCmd(pushCmd){
	let divHtml = '<div><img alt="'+ pushCmd.playerId +'" class="headPic_item" src="/img/head/' + pushCmd.headPic + '.jpeg">'+ pushCmd.nickname +'说：'+ pushCmd.chat.content +'</div>';
	chatMsg(divHtml);
}

function onPushSysChatCmd(pushCmd){
	let divHtml = '<div>系统管理对你说：'+ pushCmd.chat.content +'</div>';
	chatMsg(divHtml);
}



var seatReady = true;
/***当前轮的跳过次数****/
var skipCount;
/***当前轮的跳过次数****/
var timeoutCount;
/***剩余的手牌****/
var cards;

function onRtnGameSeatInfoCmd(rtnCmd){
	if(rtnCmd.nextMaster){
		$('#s_nextMaster_img').attr('src','/img/head/' + rtnCmd.nextMaster.headPic + '.jpeg')
		$('#s_nextMaster_img').attr('alt', '' + rtnCmd.nextMaster.id);
		$('#s_nextMaster_label').html(rtnCmd.nextMaster.nickname);
	}
	$('#s_stopAssistant').html(rtnCmd.stopAssistant);
	$('#s_broadcasting').html(rtnCmd.broadcasting);
	$('#s_applyBroadcasted').html(rtnCmd.applyBroadcasted);
	
	$('#s_sitdown').attr('disabled', true);
	$('#s_quickSitdown').attr('disabled', true);
	$('#s_standup').removeAttr('disabled');
	
	currentPosition = pushCmd.position;
	$('#s_position').val(currentPosition);
	
	seatReady = rtnCmd.ready;
	skipCount = rtnCmd.skipCount;
	timeoutCount = rtnCmd.timeoutCount;
	cards = rtnCmd.cards;
	
	//显示剩余手牌
	if(cards){
		cards.forEach(function(subElement) {
			let imgHtml = '<img alt="'+ subElement +'" class="pkPic_item" src="/img/pk/' + subElement + '.png">';
			$('#p_residue_cards').html($('#p_residue_cards').html() + imgHtml);
		});
	}
}

function onPushSitdownCmd(pushCmd){
	let divHtml = '<div><img alt="'+ pushCmd.player.id +'" class="headPic_item" src="/img/head/' + pushCmd.player.headPic + '.jpeg">'+ pushCmd.player.nickname +' 在'+ pushCmd.position +'席位中坐下了</div>';
	showMsg(divHtml);
}


function onReqStandUpCmd(rtnCmd){
	$('#s_sitdown').removeAttr('disabled');
	$('#s_quickSitdown').removeAttr('disabled');
	$('#s_standup').attr('disabled', true);
	
	$('#s_nextMaster_img').attr('src','')
	$('#s_nextMaster_img').attr('alt', '');
	$('#s_nextMaster_label').html('');
	
	let divHtml = '<div>你已席位中站起来了</div>';
	showMsg(divHtml);
}

function onPushReadyNextCmd(pushCmd){
	let divHtml = '<div>'+ pushCmd.position +'席位已准备好了</div>';
	showMsg(divHtml);
}