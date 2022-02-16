package com.hospital.manage.config;



import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 *
 * @author qy
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.hospital.admin.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // paginationInterceptor.setLimit(你的最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制);
        return new PaginationInterceptor();
    }

    /**
     * 若mp的版本高于3.3.1的就无需在此处注入bean对象了
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }


    /**
     * 乐观锁配置
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }


    // 乐观锁和分页(新版：3.4.0)
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor(){
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁插件
//        // DbType：数据库类型(根据类型获取应使用的分页方言)
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 分页插件
//        return interceptor;
//    }


/**
 * SQL执行效率插件
 */
//    @Bean
//    @Profile({"dev","test"})// 设置 dev test 环境开启
//    public PerformanceInterceptor performanceInterceptor() {
//        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
//        performanceInterceptor.setMaxTime(2000);
//        performanceInterceptor.setFormat(true);
//        return performanceInterceptor;
//    }
}
