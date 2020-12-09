package com.study.community.vo;

/**
 * @ClassName community Page
 * @Author 陈必强
 * @Date 2020/12/8 19:48
 * @Description 封装分页相关的信息
 **/
public class Page {

    //当前页码
    private int current = 1;
    //一页显示数据条数（上限）
    private int limit = 10;
    //数据总条数(用于计算总页数)
    private int rows;
    //查询路径（可以在前端拼接，在实体类中定义也可）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        //当前页不能为0或负数
        if(current>=1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        //limit带上限制
        if(limit >=5 && limit <=20){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行（记录数，数据库查询limit需要的数）
     * @return
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal(){
        return rows % limit == 0 ? rows/limit : rows/limit+1;
    }

    /**
     * 当前前端页面显示的起始页码
     * @return
     */
    public int getFrom(){
        return current-2 > 0 ? current-2 : 1;
    }

    /**
     * 当前前端页面显示的结束页码
     * @return
     */
    public int getTo(){
        return current+2 > getTotal() ? getTotal() : current+2;
    }
}
