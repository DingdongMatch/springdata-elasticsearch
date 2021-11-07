package com.match.es;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringdataElasticsearchApplicationTests {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ProductDao productDao;

    @Test
    void contextLoads() {
        // 创建索引，系统初始化会自动创建索引
        System.out.println("创建索引");
    }

    @Test
    void deleteIndex() {
        boolean flg = elasticsearchRestTemplate.indexOps(Product.class).delete();
        System.out.println("删除索引 = " + flg);
    }

    @Test
    void save() {
        Product product = new Product();
        product.setId(2L);
        product.setTitle("华为手机");
        product.setCategory("手机");
        product.setPrice(2999.0);
        product.setImages("http://www.tupian.com/hw.jpg");
        productDao.save(product);
    }

    @Test
    void update() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("小米手机");
        product.setCategory("手机");
        product.setPrice(2999.0);
        product.setImages("http://www.tupian.com/hw.jpg");
        productDao.save(product);
    }

    @Test
    void findById() {
        Product product = productDao.findById(1L).get();
        System.out.println(product);
    }

    @Test
    void findAll() {
        Iterable<Product> products = productDao.findAll();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    @Test
    void delete() {
        Product product = new Product();
        product.setId(1L);
        productDao.delete(product);
    }

    @Test
    void saveAll() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setId((long) i);
            product.setTitle("["+ i +"] 小米手机");
            product.setCategory("手机");
            product.setPrice(1999.0+i);
            product.setImages("http://www.tupian.com/xm.jpg");
            productList.add(product);
        }
        productDao.saveAll(productList);
    }

    @Test
    void findByPageable() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product product : productPage.getContent()) {
            System.out.println(product);
        }
    }

    @Test
    void termQuery() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termsQuery("title", "小米"));
        
        NativeSearchQuery query = queryBuilder.build();
        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(query, Product.class);
        for (SearchHit<Product> searchHit : searchHits) {
            System.out.println(searchHit.getContent());
        }
    }

    @Test
    void termQueryByPage() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termsQuery("title", "小米"));
        // 分页
        queryBuilder.withPageable(pageRequest);

        NativeSearchQuery query = queryBuilder.build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(query, Product.class);
        for (SearchHit<Product> searchHit : searchHits) {
            System.out.println(searchHit.getContent());
        }
    }
}
