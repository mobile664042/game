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