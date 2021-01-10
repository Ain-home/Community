// 页面加载完后绑定事件
$(function () {
    //给三个按钮绑定单击事件  提交异步请求
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

//置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#DiscussPostId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                //成功，修改按钮属性
                $("#topBtn").attr("disabled","disabled");
            }else {
                //失败
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#DiscussPostId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                //成功，修改按钮属性
                $("#wonderfulBtn").attr("disabled","disabled");
            }else {
                //失败
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#DiscussPostId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                //成功，跳转回首页
                location.href = CONTEXT_PATH + "/index";
            }else {
                //失败
                alert(data.msg);
            }
        }
    );
}

//点赞
function like(btn,entityType,entityId,entityUserId,discussPostId) {
    //发送异步请求，提交数据
    $.post(
        //访问路径
        CONTEXT_PATH + "/like",
        //提交的数据
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"discussPostId":discussPostId},
        //回调函数
        function (data) {
            //将返回的JSON数据转换为JS对象
            data = $.parseJSON(data);
            if(data.code == 0){
                //成功,根据点赞状态更改为 "已赞" ,更新点赞数
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : '赞');
                $(btn).children("i").text(data.likeCount);
            }else {
                //失败
                alert(data.msg);
            }
        }
    );

}