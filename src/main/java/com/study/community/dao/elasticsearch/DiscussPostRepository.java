package com.study.community.dao.elasticsearch;

import com.study.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @ClassName community DiscussPostRepository
 * @Author 陈必强
 * @Date 2021/1/6 19:42
 * @Description es服务器访问数据持久层
 * 继承ElasticsearchRepository类，并注明实体类型和主键类型即可
 **/
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
