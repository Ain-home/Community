$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 点击发布后隐藏编辑帖子的弹出框
	$("#publishModal").modal("hide");

	// 点击发布后将帖子标题和内容异步发送到服务器，返回提示信息
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求(POST请求，提交数据)
	$.post(
		//访问路径
		CONTEXT_PATH + "/discuss/add",
		//发送内容
		{"title":title,"content":content},
		//回调函数(匿名函数，将返回的JSON字符串转化为JS对象)
		function (data) {
			data = $.parseJSON(data);
			//将提示信息显示到提示框
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//延期两秒后隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果发布帖子成功，则刷新页面
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}