$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA（用户）
		// 异步请求
		$.post(
			CONTEXT_PATH + "/follow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if(data.code == 0){
					//成功，修改css样式，修改页面上关注人数的数量（为了方便，直接刷新页面）
					window.location.reload();
				}else {
					//失败,打印返回的提示信息
					alert(data.msg);
				}
			}
		);
		// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		// 异步请求
		$.post(
			CONTEXT_PATH + "/unfollow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if(data.code == 0){
					//成功，修改css样式，修改页面上关注人数的数量（为了方便，直接刷新页面）
					window.location.reload();
				}else {
					//失败,打印返回的提示信息
					alert(data.msg);
				}
			}
		);
		// $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}