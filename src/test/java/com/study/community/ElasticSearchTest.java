package com.study.community;

import com.study.community.dao.DiscussPostMapper;
import com.study.community.dao.elasticsearch.DiscussPostRepository;
import com.study.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName community ElasticSearchTest
 * @Author 陈必强
 * @Date 2021/1/6 19:46
 * @Description ElasticSearch 持久层测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {

    //从mysql数据库中取出数据
    @Autowired
    private DiscussPostMapper discussPostMapper;

    //从es服务器中存数据，搜索数据
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //插入单条记录
    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    //插入多条记录
    @Test
    public void testInsertList(){
        //插入discuss_post表中的大部分数据
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
    }

    //修改数据 id=231
    @Test
    public void testUpdate(){
        //修改重新向该id插入新数据，es会自动覆盖原数据
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水！！！");
        discussPostRepository.save(post);
    }

    //删除数据
    @Test
    public void testDelete(){
        //discussPostRepository.deleteById(231);
        //删除该索引的所有数据(使用较少，风险较大)
        discussPostRepository.deleteAll();
    }

    //core 搜索功能
    @Test
    public void testSearchByRepository(){
        //构造搜索条件，关键词条，是否排序，是否分页，是否搜索结果高亮显示【关键词前后添加标签（自己指定）】
        //NativeSearchQueryBuilder 可以构建 NativeSearchQuery的实现类
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))  //第0页，显示10条数据
                .withHighlightFields(
                        //高亮显示字段以及其前后添加的标签
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        //Repository的底层调用了elasticTemplate.queryForPage(query,实体类.class,SearchResultMapper)
        //SearchResultMapper对查到的数据进行处理
        //底层获取到了高亮显示的值，但是有高亮显示的结果并没有经过SearchResultMapper处理，并没有一起返回
        //要高亮显示，需要使用 elasticTemplate
        Page<DiscussPost> page = discussPostRepository.search(query);

        //分页信息
        System.out.println(page.getTotalElements());  //一共有多少条数据匹配
        System.out.println(page.getTotalPages());   //一共有多少页
        System.out.println(page.getNumber());  //当前处在第几页
        System.out.println(page.getSize());    //每一页最多显示几条数据
        for (DiscussPost discussPost:page){
            //page可以直接遍历（可以看做集合）
            System.out.println(discussPost);
        }
    }

    //高亮显示
    @Test
    public void testSearchByTemplate(){
        PageRequest pageRequest = PageRequest.of(0,10);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageRequest)  //第0页，显示10条数据
                .withHighlightFields(
                        //高亮显示字段以及其前后添加的标签
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        //es 7.x 不存在 SearchResultMapper
        //Page<DiscussPost> page = elasticsearchRestTemplate.queryForPage(query,DiscussPost.class,new SearchResultMapper)
        //获取查询命中情况
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(query,DiscussPost.class);
        //获取查询命中结果的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        //定义一个保存返回查询结果实体类的集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        //遍历查询结果进行处理
        for (SearchHit<DiscussPost> searchHit:searchHits){
            //高亮内容
            Map<String,List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setTitle(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // 放到实体类中
            discussPosts.add(searchHit.getContent());
        }

        Page<DiscussPost> page = new PageImpl<DiscussPost>(discussPosts,pageRequest,search.getTotalHits());
        //分页信息
        System.out.println(page.getTotalElements());  //一共有多少条数据匹配
        System.out.println(page.getTotalPages());   //一共有多少页
        System.out.println(page.getNumber());  //当前处在第几页
        System.out.println(page.getSize());    //每一页最多显示几条数据
        for (DiscussPost discussPost:page){
            //page可以直接遍历（可以看做集合）
            System.out.println(discussPost);
        }

        System.out.println(discussPosts.size());
        for (DiscussPost discussPost:discussPosts){
            System.out.println(discussPost);
        }

    }


}
