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
	let domain = getQueryVariable("domain");
	if(domain){
		websocketUrl = "ws://"+domain+"/websocket/ddz/" + logoinToken;
	}
	console.log("使用地址：" + websocketUrl);
	webSocket = new WebSocket(websocketUrl);

	function onMessage(event) {
		console.log("收到：" + event.data);
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
		
		//需要发送心跳，使得socket连接不中断
		connected = true;
		

		//准备进入游戏		
		let sendMessage = JSON.stringify(reqJoinCmd);
		console.log("发送：" + sendMessage);
		webSocket.send(sendMessage);
	}

	function onError(event) {
		alert("通讯异常:" + JSON.stringify(event));
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
	console.log("发送：" + sendMessage);
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
	console.log("发送：" + sendMessage);
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
	
	if(!extSeatInfo.currentPosition){
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
	console.log("发送：" + sendMessage);
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
	
	if(!extSeatInfo.currentPosition){
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
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
}

function playcard(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	let cardList = [];
	$("img[name=selectedCard").each(function(index, element) {
		let card = parseInt(element.alt); 
		cardList[index] = card;
	});
	
	if(cardList.length == 0){
		alert("请选择你要出的牌");
		return;
	}
	
	if(extGameInfo.currentProgress != 'robbedLandlord'){
		alert("还未到打牌阶段");
		return;
	}
	
	//出牌
	let reqPlayCardCmd = {
		"cmd": 151003,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val(),
		"cards": cardList
	}
	
	let sendMessage = JSON.stringify(reqPlayCardCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
	
	//记录刚打的牌
	extSeatInfo.willLeftCards = cardList;
}

function skipPlaycard(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	
	if(extGameInfo.currentProgress != 'robbedLandlord'){
		alert("还未到打牌阶段");
		return;
	}
	
	//出牌
	let reqPlayCardCmd = {
		"cmd": 151003,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val(),
		"cards": []
	}
	
	let sendMessage = JSON.stringify(reqPlayCardCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
	
	//记录刚打的牌
	extSeatInfo.willLeftCards = [];
}


function robLandlord(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	if(extGameInfo.currentProgress != 'sended'){
		alert("还未到抢地主阶段");
		return;
	}
	
	//抢地主
	let reqRobLandlordCmd = {
		"cmd": 151002,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val(),
		"score": 3
	}
	
	let sendMessage = JSON.stringify(reqRobLandlordCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
}

function surrender(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!connected){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	if(extGameInfo.currentProgress != 'robbedLandlord'){
		alert("还未到打牌阶段,不可以投降");
		return;
	}
	
	//投降
	let reqSurrenderCmd = {
		"cmd": 151004,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val()
	}
	
	let sendMessage = JSON.stringify(reqSurrenderCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
}

function getSeatPlayerList(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!webSocket){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	let reqGetSeatPlayerListCmd = {
		"cmd": 102002,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val(),
		"fromPage": 1
	}
	
	let sendMessage = JSON.stringify(reqGetSeatPlayerListCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
}

function getAssistantList(){
	if(!logoinToken){
		alert("请先登录");
		return;
	}
	if(!webSocket){
		alert("请先进入游戏");
		return;
	}
	
	if(!extSeatInfo.currentPosition){
		alert("你还未坐下");
		return;
	}
	
	let reqGetAssistantListCmd = {
		"cmd": 102002,
		"playKind": $('#j_playerKind').val(),
		"deskNo": $('#j_deskNo').val(),
		"position": $('#s_position').val()
	}
	
	let sendMessage = JSON.stringify(reqGetAssistantListCmd);
	console.log("发送：" + sendMessage);
	webSocket.send(sendMessage);
}

function selectCard(cardImg){
	let cardItem = $(cardImg);
	if(cardItem.attr('name')){
		cardItem.attr('name', '');
		cardItem.css("margin-bottom","0px");
	}
	else{
		cardItem.attr('name', 'selectedCard');
		cardItem.css("margin-bottom","12px");
	}
}

//心跳检测
setTimeout(sendHeartCheck(), 120000);
function sendHeartCheck(){
	if(connected){
		let heartCmd = {
			"cmd": 888888
		}
		let sendMessage = JSON.stringify(heartCmd);
		console.log("发送：" + sendMessage);
		webSocket.send(sendMessage);
	}
	setTimeout(sendHeartCheck, 120000);
}

var leftSecond = 100000;
var leftSecondMsg = "test";

setTimeout(calcCountdown(), 1000);
function calcCountdown(){
	if(leftSecond > 0){
		leftSecond = leftSecond -1;
		$('#p_leftSecond').html("剩余"+leftSecond+"秒, " + leftSecondMsg);
	}
	else{
		$('#p_leftSecond').html('');
	}
	setTimeout(calcCountdown, 1000);
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
	console.log("发送：" + sendMessage);
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
	console.log("发送：" + sendMessage);
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
	console.log("发送：" + sendMessage);
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
	    $('#j_join').removeAttr('disabled');
	    $('#j_left').attr('disabled', true);
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
	    case 1102007:
	    onPushStandUpCmd(rtnData);
	    break;
	    
	    case 151001:
	    onRtnReadyNextCmd(rtnData);
	    break;
	    case 1151001:
	    onPushReadyNextCmd(rtnData);
	    break;
	    
	    
	    case 151002:
	    onReqRobLandlordCmd(rtnData);
	    break;
	    case 1151002:
	    onPushRobLandlordCmd(rtnData);
	    break;
	    
	    
	    case 151003:
	    onReqPlayCardCmd(rtnData);
	    break;
	    case 1151003:
	    onPushPlayCardCmd(rtnData);
	    break;
	    
	    case 151004:
	    onReqSurrenderCmd(rtnData);
	    break;
	    case 1151004:
	    onPushSurrenderCmd(rtnData);
	    break;
	    
	    
	    case 102002:
	    onRtnGetSeatPlayerListCmd(rtnData);
	    break;
	    case 102003:
	    onRtnGetAssistantListCmd(rtnData);
	    break;
	    
	    case 2151003:
	    onNotifySendCardCmd(rtnData);
	    break;
	    case 2151004:
	    onNotifyGameOverCmd(rtnData);
	    break;
	    case 2151005:
	    onNotifyGameSkipCmd(rtnData);
	    break;
	    
	}
}

function showMsg(message){
	$('#s_show').html($('#s_show').html() + "<br/>" + message);

	let size = $('#s_show').html().length;
	if(size > 10000){
		let content = $('#s_show').html().substring($('#s_show').html().length-10000);
		$('#s_show').html(content);
	}
	//$("#s_show").scrollTop($("#s_show").scrollHeight));
	let s_show = document.getElementById('s_show');
	s_show.scrollTop = s_show.scrollHeight;
}
function chatMsg(message){
	$('#c_chatPanel').html($('#c_chatPanel').html() + "<br/>" + message);
	
	let size = $('#c_chatPanel').html().length;
	if(size > 10000){
		let content = $('#c_chatPanel').html().substring($('#c_chatPanel').html().length-10000);
		$('#c_chatPanel').html(content);
	}
	//$("#c_chatPanel").scrollTop($("#c_chatPanel").scrollHeight));
	let c_chatPanel = document.getElementById('c_chatPanel');
	c_chatPanel.scrollTop = c_chatPanel.scrollHeight;
}

function deskMsg(message){
	$('#p_deskPanel').html($('#p_deskPanel').html() + message);
	
	let size = $('#p_deskPanel').html().length;
	if(size > 10000){
		let content = $('#p_deskPanel').html().substring($('#p_deskPanel').html().length-10000);
		$('#p_deskPanel').html(content);
	}
	//$("#p_deskPanel").scrollTop($("#p_deskPanel").scrollHeight));
	let p_deskPanel = document.getElementById('p_deskPanel');
	p_deskPanel.scrollTop = p_deskPanel.scrollHeight;
}

/////////////---------具体游戏部分--------///////////////////////

//全局配置信息(后期改成从后台动态获取)(倒计时，需要比服务器小一秒)
var globalConfig = {};
/***最长等待抢地主时长****/
globalConfig.maxRobbedLandlordSecond = 19;
/***最长等待下一轮时长****/
globalConfig.maxReadyNextSecond = 299;
/***最长等待过牌时长(如果此时是自己出牌，自动选择最少的一张牌)****/
globalConfig.maxPlayCardSecond = 16;
/***一局游戏允许最大超时次数****/
globalConfig.maxPlayCardOuttimeCount = 2;
/***一局游戏允许最大跳过次数(断线后或超时跳过牌)****/
globalConfig.maxSkipCount = 3;
/***最长等待(主席位)掉线重连时长****/
globalConfig.maxMasterDisconnectSecond = 40;
/***最长等待(非主席位)掉线重连时长****/
globalConfig.maxDisconnectSecond = 15;
/***掉线时，等待重连后出时长****/
globalConfig.disconnectPlayCardSecond = 1;
/***跳过次数大于5将会收到逃跑处罚(处罚规则是,系统、每个席位赔一份农民输赢市)****/
globalConfig.escape2SkipCount = 5;
/***认输惩罚翻倍指数(快速结束游，队友不用赔钱)****/
globalConfig.punishSurrenderDoubleCount = 2;


//管理员的id
var playerId;
//管理员的id
var managerId;
//当前的席位

//当前的游戏信息
//extGameInfo.pauseMs//暂停时长(毫秒)
//extGameInfo.currentProgress
//extGameInfo.surrenderPosition
//extGameInfo.commonCards
//extGameInfo.landlordPosition
//extGameInfo.battlefield
//extGameInfo.doubleCount
//extGameInfo.landlordPlayCardCount
//extGameInfo.farmerPlayCardCount
//extGameInfo.seatPlayingMap
var extGameInfo = {};

//
//当前的席位信息拥有属性
//extSeatInfo.willLeftCards
//extSeatInfo.currentPosition
//extSeatInfo.seatReady
//extSeatInfo.skipCount
//extSeatInfo.timeoutCount
//extSeatInfo.cards;
var extSeatInfo = {};

function onRtnGameInfoCmd(rtnCmd){
	console.log("准备处理进入游戏 , " + JSON.stringify(rtnCmd));
	if(rtnCmd.pauseMs > 0){
		showMsg("游戏还需要暂停毫秒:" + rtnCmd.pauseMs);
	}
	$('#j_left').removeAttr('disabled');
	$('#s_sitdown').removeAttr('disabled');
	$('#s_quickSitdown').removeAttr('disabled');
	$('#j_join').attr('disabled', true);

	$('#j_join').attr('disabled', true);
	$('#r_save').attr('disabled', true);
	$('#r_login').attr('disabled', true);
	$('#r_logout').attr('disabled', true);
	$('#s_standup').attr('disabled', true);
	
	//@45@78@1
	var strList = rtnCmd.address.split("@");
	if(strList.length=4){
		extSeatInfo.currentPosition = strList[3];
		$('#s_position').val(extSeatInfo.currentPosition);
	}	
	managerId = rtnCmd.managerId;
	playerId = rtnCmd.playerId;
	
	extGameInfo.currentProgress = rtnCmd.currentProgress;
	extGameInfo.surrenderPosition = rtnCmd.surrenderPosition;
	extGameInfo.commonCards = rtnCmd.commonCards;
	extGameInfo.landlordPosition = rtnCmd.landlordPosition;
	extGameInfo.battlefield = rtnCmd.battlefield;
	extGameInfo.doubleCount = rtnCmd.doubleCount;
	extGameInfo.landlordPlayCardCount = rtnCmd.landlordPlayCardCount;
	extGameInfo.farmerPlayCardCount = rtnCmd.farmerPlayCardCount;
	extSeatInfo.currentPosition = rtnCmd.currentPosition;
	
	//把其他席位上的玩家也保留上。
	extGameInfo.seatPlayingMap = rtnCmd.seatPlayingMap;
	
	//清理初使值
	$("#p_deskPanel").html('');
	$('#p_commonCards').html('');
	$('#p_landlord_position').html('');
	$('#p_doubleCount').html('');
	
	//渲染
	if(extGameInfo.currentProgress == "ready"){
		$('#p_currentProgress').css("background","yellow");
		return;
	}
	
	//加载底牌
	extGameInfo.commonCards.forEach(function(element) {
		//console.log(element);
		let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
		$('#p_commonCards').html($('#p_commonCards').html() + imgHtml);
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
	
	$('#s_position').val(extSeatInfo.currentPosition);
	
	
	//设置已出过的牌
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
			
			let seatPlayer = extGameInfo.seatPlayingMap[element.positiont.toString()];
			let playerHtml = '<img class="headPic_item" src="/img/head/'+ seatPlayer.headPic +'.jpeg">' + seatPlayer.nickname;
			//地主;
			if(element.position == extGameInfo.landlordPosition){
//				let spanHtml = '<span class="landlord_item">' + element.position +': </span>';
				let spanHtml = '<span class="landlord_item">' + element.position;
				spanHtml += playerHtml + ': </span>';
				deskMsg(spanHtml);
			}
			else{
//				let spanHtml = '<span class="farmer_item">' + element.position +': </span>';
				let spanHtml = '<span class="farmer_item">' + element.position;
				spanHtml += playerHtml + ': </span>';
				
				deskMsg(spanHtml);
			}
			if(element.cards){
				element.cards.forEach(function(subElement) {
					let imgHtml = '<img alt="'+ subElement +'" class="pkPic_item" src="/img/pk/' + subElement + '.png">';
					//$('#p_deskPanel').html($('#p_deskPanel').html() + imgHtml);
					deskMsg(imgHtml);
				});
			}
			deskMsg('<hr/>');
		});
	}
	deskMsg('<hr/>');
	
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
	
	//需要判断是不是自己被踢下线了
	if(pushCmd.playerId == playerId){
		$('#p_currentProgress').css("background","black");	
		clearExtGameData();
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
	$('#p_ready').attr('disabled', true);
	
	extSeatInfo.currentPosition = rtnCmd.position;
	$('#s_position').val(extSeatInfo.currentPosition);
	
	extSeatInfo.seatReady = rtnCmd.ready;
	extSeatInfo.skipCount = rtnCmd.skipCount;
	extSeatInfo.timeoutCount = rtnCmd.timeoutCount;
	extSeatInfo.cards = rtnCmd.cards;
	
	//显示剩余手牌
	if(extSeatInfo.cards){
		extSeatInfo.cards.forEach(function(subElement) {
			let imgHtml = '<img id="p_mycard'+ subElement +'" alt="'+ subElement +'" onclick="selectCard(this)" class="pkPic_item" src="/img/pk/' + subElement + '.png">';
			$('#p_residue_cards').html($('#p_residue_cards').html() + imgHtml);
		});
	}
}

function onPushSitdownCmd(pushCmd){
	//如果是主席位，需要加入到参与玩家中
	if(pushCmd.player.seatPost == 'master'){
		extGameInfo.seatPlayingMap[pushCmd.player.position.toString()] = pushCmd.player;
	}
	
	let divHtml = '<div><img alt="'+ pushCmd.player.id +'" class="headPic_item" src="/img/head/' + pushCmd.player.headPic + '.jpeg">'+ pushCmd.player.nickname +' 在'+ pushCmd.position +'席位中坐下了</div>';
	showMsg(divHtml);
}


function onReqStandUpCmd(rtnCmd){
	clearExtSeatData();
	
	let divHtml = '<div>你在席位中站起来了</div>';
	$('#p_ready').attr('disabled', true);
	showMsg(divHtml);
}

function onPushStandUpCmd(pushCmd){
	//判断是否是主席位
	if(pushCmd.seatPost == 'master'){
		extGameInfo.seatPlayingMap[pushCmd.position.toString()] = null;
		
		//判断是不是自己被强制站起来了
		if(extSeatInfo.currentPosition && extSeatInfo.currentPosition == pushCmd.position){
			clearExtSeatData();
			let divHtml = '<div>你在席位中被强制站起来了</div>';
			$('#p_ready').attr('disabled', true);
			showMsg(divHtml);
		}
	}
	
	
	let divHtml = '<div><img alt="'+ pushCmd.playerId +'" class="headPic_item" src="/img/head/' + pushCmd.headPic + '.jpeg">'+ pushCmd.nickname +'在' + pushCmd.position +'席位站起来了！</div>';
	showMsg(divHtml);
}

function onRtnReadyNextCmd(pushCmd){
	//清理游状态，准备下一局
	clearExtGameData();
	clearExtSeatData();
	$('#p_ready').attr('disabled', true);
	showMsg("已准备好了！");
}

function clearExtGameData(){
	extGameInfo.currentProgress='ready';
	extGameInfo.surrenderPosition=0;
	extGameInfo.commonCards=[];
	extGameInfo.landlordPosition=0;
	extGameInfo.battlefield=[];
	extGameInfo.doubleCount=0;
	extGameInfo.landlordPlayCardCount=0;
	extGameInfo.farmerPlayCardCount=0;
	//清理界面
	$('#p_commonCards').html('');
	$('#p_landlord_position').html('');
	$('#p_doubleCount').html('');
}

function clearExtSeatData(){
	extSeatInfo.willLeftCards=[];
	//extSeatInfo.currentPosition
	extSeatInfo.seatReady=false;
	extSeatInfo.skipCount = 0;
	extSeatInfo.timeoutCount = 0;
	extSeatInfo.cards = [];
	
	if(extGameInfo.currentProgress='ready'){
		leftSecond = 0;
	}
	
	//清理手牌
	$('#p_residue_cards').html('');
	
	$('#s_nextMaster_img').attr('src','')
	$('#s_nextMaster_img').attr('alt', '');
	$('#s_nextMaster_label').html('');
}

function onPushReadyNextCmd(pushCmd){
	let divHtml = '<div>'+ pushCmd.position +'席位已准备好了</div>';
	showMsg(divHtml);
}

function onReqRobLandlordCmd(rtnCmd){
	extGameInfo.currentProgress = 'robbedLandlord';
	
	//你是地主了;
	$('#p_landlord_position').html(extSeatInfo.currentPosition);
	let divHtml = '<div>你是地主了!</div>';
	showMsg(divHtml);
	leftSecond=globalConfig.maxPlayCardSecond;
	leftSecondMsg="等待出牌";
	
	//显示底牌
	$('#p_commonCards').html('');
	extGameInfo.commonCards = rtnCmd.cards;
	if(extGameInfo.commonCards){
		//加载底牌
		extGameInfo.commonCards.forEach(function(element) {
			//console.log(element);
			let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
			$('#p_commonCards').html($('#p_commonCards').html() + imgHtml);
			
			//自己的手牌也要加上
			extSeatInfo.cards[extSeatInfo.cards.length] = element;
		});
		
		//重新加载排序
		extSeatInfo.cards.sort(mysort);
		$('#p_residue_cards').html('');
		extSeatInfo.cards.forEach(function(element) {
			imgHtml = '<img id="p_mycard'+ element +'" alt="'+ element +'" onclick="selectCard(this)" class="pkPic_item" src="/img/pk/' + element + '.png">';
			$('#p_residue_cards').html($('#p_residue_cards').html() + imgHtml);
		});
	}
}

function mysort(a,b){
	return a-b;
}
function getQueryVariable(variable){
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if(pair[0] == variable){
			return pair[1];
		}
	}
	return(false);
}

function onPushRobLandlordCmd(pushCmd){
	extGameInfo.currentProgress = 'robbedLandlord';
	
	$('#p_landlord_position').html(pushCmd.position);
	let divHtml = '<div>'+ pushCmd.position +'席位成为地主</div>';
	showMsg(divHtml);
	
	
	//显示底牌
	$('#p_commonCards').html('');
	extGameInfo.commonCards = pushCmd.cards;
	if(extGameInfo.commonCards){
		//加载底牌
		extGameInfo.commonCards.forEach(function(element) {
			//console.log(element);
			let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
			$('#p_commonCards').html($('#p_commonCards').html() + imgHtml);
		});
	}
}

function onReqPlayCardCmd(rtnCmd){
	//地主;
	if(extGameInfo.currentPosition == extGameInfo.landlordPosition){
		let spanHtml = '<span class="landlord_item">我: </span>';
		deskMsg(spanHtml);
	}
	else{
		let spanHtml = '<span class="farmer_item">我: </span>';
		deskMsg(spanHtml);
	}
	
	if(extSeatInfo.willLeftCards && extSeatInfo.willLeftCards.length > 0){
		//删除剩余的牌
		for (let i = extSeatInfo.cards.length - 1; i >= 0; i--) {
			for (let j = 0; j < extSeatInfo.willLeftCards.length; j++) {
			    if (extSeatInfo.cards[i] == extSeatInfo.willLeftCards[j] ) {
					//删除图片
					$('#p_mycard'+extSeatInfo.cards[i]).remove();
			        extSeatInfo.cards.splice(i, 1);
			        break;
			    }
			}
		}
		
		//将牌加入到已出的牌中
		extSeatInfo.willLeftCards.forEach(function(element) {
			let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
			deskMsg(imgHtml);
		});
	}
	else{
		deskMsg('不出');
	}
	deskMsg('<hr/>');
	
	extSeatInfo.willLeftCards = [];
	leftSecond=globalConfig.maxPlayCardSecond;
	leftSecondMsg="等待下家出牌";
}
function onPushPlayCardCmd(pushCmd){
	//判断是不是超时被强制出牌
	if(pushCmd.position == extSeatInfo.currentPosition){
		//删除剩余的牌
		if(pushCmd.cards && pushCmd.cards.length > 0){
			for (let i = extSeatInfo.cards.length - 1; i >= 0; i--) {
				for (let j = 0; j < pushCmd.cards.length; j++) {
				    if (extSeatInfo.cards[i] == pushCmd.cards[j] ) {
					
						let spanHtml = '<span class="landlord_item">超时强制出牌: </span>';
						deskMsg(spanHtml);
			
				        let imgHtml = '<img alt="'+ extSeatInfo.cards[i] +'" class="pkPic_item" src="/img/pk/' + extSeatInfo.cards[i] + '.png">';
						deskMsg(imgHtml);
						//删除图片
						$('#p_mycard'+extSeatInfo.cards[i]).remove();
				        extSeatInfo.cards.splice(i, 1);
				    }
				}
			}
			
			let divHtml = '<div>你超时被强制出牌！</div>';
			showMsg(divHtml);
		}
		else{
			let divHtml = '<div>你超时被强制不出牌！</div>';
			showMsg(divHtml);
			deskMsg(divHtml);
		}
	}
	else{
		let seatPlayer = extGameInfo.seatPlayingMap[pushCmd.position.toString()];
		let playerHtml = '<img class="headPic_item" src="/img/head/'+ seatPlayer.headPic +'.jpeg">' + seatPlayer.nickname;
				
		//地主;
		if(pushCmd.position == extGameInfo.landlordPosition){
			let spanHtml = '<span class="landlord_item">' + pushCmd.position;
			spanHtml += playerHtml + ': </span>';
			deskMsg(spanHtml);
		}
		else{
			let spanHtml = '<span class="farmer_item">' + pushCmd.position;
			spanHtml += playerHtml + ': </span>';
			deskMsg(spanHtml);
		}
		if(pushCmd.cards && pushCmd.cards.length > 0){
			pushCmd.cards.forEach(function(element) {
				let imgHtml = '<img alt="'+ element +'" class="pkPic_item" src="/img/pk/' + element + '.png">';
				deskMsg(imgHtml);
			});
		}
		else{
			deskMsg('要不起');
		}
	}
	deskMsg('<hr/>');
	
	leftSecond=globalConfig.maxPlayCardSecond;
	let nextPosition = pushCmd.position + 1;
	if(nextPosition >= 4){
		nextPosition = 1;
	}
	
	leftSecondMsg='等待'+ nextPosition +'席位出牌';
	if(extSeatInfo && extSeatInfo.currentPosition == nextPosition){
		leftSecondMsg="等待我出牌";
	}
	
}




function onReqSurrenderCmd(rtnCmd){
	$('#p_currentProgress').css("background","darkred");
	let divHtml = '<div>你投降了</div>';
	showMsg(divHtml);
}

function onPushSurrenderCmd(pushCmd){
	$('#p_currentProgress').css("background","darkred");
	let divHtml = '<div>'+ pushCmd.position +'席位投降了</div>';
	showMsg(divHtml);
}
function onRtnGetSeatPlayerListCmd(rtnCmd){
	rtnCmd.list.forEach(function(element) {
		let divHtml = '<div><img alt="'+ element.id +'" class="headPic_item" src="/img/head/' + element.headPic + '.jpeg">'+ element.nickname +' 在席位中, 角色:' + element.seatPost + '</div>';
		showMsg(divHtml);
	});
}
function onRtnGetAssistantListCmd(rtnCmd){
	rtnCmd.list.forEach(function(element) {
		let divHtml = '<div><img alt="'+ element.id +'" class="headPic_item" src="/img/head/' + element.headPic + '.jpeg">'+ element.nickname +' 在席位中, 角色:' + element.seatPost + '</div>';
		showMsg(divHtml);
	});
}

function onNotifySendCardCmd(rtnCmd){
	extSeatInfo.cards = rtnCmd.cards;
	extGameInfo.currentProgress = 'sended';
	
	//显示剩余手牌
	$('#p_residue_cards').html('');
	if(extSeatInfo.cards){
		extSeatInfo.cards.forEach(function(subElement) {
			let imgHtml = '<img id="p_mycard'+ subElement +'" alt="'+ subElement +'" onclick="selectCard(this)" class="pkPic_item" src="/img/pk/' + subElement + '.png">';
			$('#p_residue_cards').html($('#p_residue_cards').html() + imgHtml);
		});
	}
	leftSecond=globalConfig.maxRobbedLandlordSecond;
	leftSecondMsg="等待抢地主";
}

function onNotifyGameOverCmd(rtnCmd){
	extGameInfo.currentProgress = 'ready';
	
	let resultMessage = JSON.stringify(rtnCmd);
	
	let divHtml = '<div>游戏结束: '+resultMessage+'</div>';
	showMsg(divHtml);
	
	leftSecond=globalConfig.maxReadyNextSecond;
	leftSecondMsg="等待准备一局";
	$('#p_ready').removeAttr('disabled');
	$('#p_currentProgress').css("background","yellow");
	
	//显示结果
	let finded = false;
	rtnCmd.list.forEach(function(element) {
		if(element.position == extSeatInfo.currentPosition){
			let divHtml = '<div>游戏结束: 底注' + rtnCmd.unitPrice;
			if(element.changeCoin > 0){
				divHtml += ", 你赢了 " + element.changeCoin + " !";
			}
			else if(element.changeCoin < 0){
				divHtml += ", 你输了 " + (-element.changeCoin) + " !";
			}
			else {
				divHtml += ", 历害了，不输不赢";
			}
			finded = true;
			divHtml += '</div><hr/>';
			deskMsg(divHtml);
		}
	});
	if(!finded){
		let divHtml = '<div>有人投降了，你不受影响</div>';
		deskMsg(divHtml + "<hr/>");
		showMsg(divHtml);
	}
	
}

function onNotifyGameSkipCmd(rtnCmd){
	extGameInfo.currentProgress = 'ready';
	
	let divHtml = '<div>跳过上一局</div>';
	showMsg(divHtml);
	deskMsg(divHtml);
	
	leftSecond=globalConfig.maxReadyNextSecond;
	leftSecondMsg="等待准备一局";
	$('#p_ready').removeAttr('disabled');
	
}

