/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.tests;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;

@Configuration
@Component
class TestConfig{
	@Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
	
	@Bean
	@Autowired
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
		
	}
	
}