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