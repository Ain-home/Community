package com.study.community.service.impl;

import com.study.community.dao.elasticsearch.DiscussPostRepository;
import com.study.community.entity.DiscussPost;
import com.study.community.service.ElasticsearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName community ElasticsearchServiceImpl
 * @Author 陈必强
 * @Date 2021/1/6 21:45
 * @Description elasticsearch 搜索业务层
 **/
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    //高亮显示
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    @Override
    public Page<DiscussPost> searchDiscussPost(String keyWord, int current, int limit) {
        //分页条件(当前页，每页显示多少数据)
        PageRequest pageRequest = PageRequest.of(current,limit);
        //构造搜索条件，关键词条，是否排序，是否分页，是否搜索结果高亮显示【关键词前后添加标签（自己指定）】
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyWord,"title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageRequest)
                .withHighlightFields(
                        //高亮显示字段以及其前后添加的标签
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        //获取查询命中情况
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(query,DiscussPost.class);
        if(search.getTotalHits() <= 0){
            //如果没有命中数据
            return null;
        }
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

        //查询结果封装分页
        Page<DiscussPost> page = new PageImpl<DiscussPost>(discussPosts,pageRequest,search.getTotalHits());

        return page;
    }
}
