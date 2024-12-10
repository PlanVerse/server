package com.planverse.server.common.config

import com.planverse.server.common.config.data.RoutingDataSource
import jakarta.persistence.EntityManagerFactory
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import javax.sql.DataSource

@Configuration
@MapperScan(
    basePackages = [
        "com.planverse.server.*.mapper",
        "com.planverse.server.*.*.mapper"
    ], sqlSessionTemplateRef = "sqlSessionTemplate"
)
@EnableJpaRepositories(
    basePackages = [
        "com.planverse.server.*.repository",
        "com.planverse.server.*.*.repository"
    ]
)
class DataConfig {
    @Bean("writeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    fun writeDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("readDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    fun readDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("routingDataSource")
    @Throws(Exception::class)
    fun routingDataSource(@Qualifier("writeDataSource") writeDataSource: DataSource, @Qualifier("readDataSource") readDataSource: DataSource): DataSource {
        return RoutingDataSource(writeDataSource, listOf(readDataSource))
    }

    @Primary
    @Bean("dataSource")
    fun dataSource(@Qualifier("routingDataSource") routingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }

    @Primary
    @Bean("entityManagerFactory")
    fun entityManagerFactory(
        dataSource: DataSource,
        jpaProperties: JpaProperties,
    ): LocalContainerEntityManagerFactoryBean {
        val localContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        localContainerEntityManagerFactoryBean.dataSource = dataSource
        localContainerEntityManagerFactoryBean.setPackagesToScan(
            "com.planverse.server.*.entity",
            "com.planverse.server.*.*.entity"
        )
        localContainerEntityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(jpaProperties.properties)
        localContainerEntityManagerFactoryBean.afterPropertiesSet()

        return localContainerEntityManagerFactoryBean
    }

    @Primary
    @Bean("transactionManager")
    fun transactionManager(@Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.entityManagerFactory = entityManagerFactory

        return jpaTransactionManager
    }

    @Primary
    @Bean("sqlSessionFactory")
    @Throws(Exception::class)
    fun sqlSessionFactory(@Qualifier("dataSource") dataSource: DataSource, applicationContext: ApplicationContext): SqlSessionFactory? {
        val sqlSessionFactoryBean = SqlSessionFactoryBean()
        sqlSessionFactoryBean.setDataSource(dataSource)
        sqlSessionFactoryBean.setMapperLocations(*PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"))
        sqlSessionFactoryBean.setTypeAliasesPackage("com.planverse.server.*.dto;com.planverse.server.*.*.dto")
        sqlSessionFactoryBean.getObject()?.configuration?.isMapUnderscoreToCamelCase = true

        return sqlSessionFactoryBean.getObject()
    }

    @Primary
    @Bean("sqlSessionTemplate")
    fun sqlSessionTemplate(@Qualifier("sqlSessionFactory") sqlSessionFactory: SqlSessionFactory): SqlSessionTemplate {
        return SqlSessionTemplate(sqlSessionFactory)
    }
}