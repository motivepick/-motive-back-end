package org.motivepick

import com.github.springtestdbunit.bean.DatabaseConfigBean
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
internal class DbUnitConfig {
    @Bean
    fun configBean(): DatabaseConfigBean {
        val configBean = DatabaseConfigBean()
        configBean.datatypeFactory = H2DataTypeFactory()
        return configBean
    }

    @Bean(name = ["dbUnitDatabaseConnection"])
    fun databaseDataSourceConnectionFactoryBean(configBean: DatabaseConfigBean?, dataSource: DataSource?): DatabaseDataSourceConnectionFactoryBean {
        val factoryBean = DatabaseDataSourceConnectionFactoryBean()
        factoryBean.setDatabaseConfig(configBean)
        factoryBean.setDataSource(dataSource)
        return factoryBean
    }
}
