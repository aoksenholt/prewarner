package prewarner;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class PrewarnerApplication {

    public static void main(String[] args) {
              /*
        String url  = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};dbq=o:/raceday/etime.mdb;SystemDB=o:/system.mdw;UID=admin";

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
        JdbcTemplate t = new JdbcTemplate(ds);

        System.out.println(t.queryForObject("select count(*) from name", Integer.class));
                */
        SpringApplication.run(PrewarnerApplication.class, args);
    }
    /*
    @PostConstruct
    @Autowired
    public void doit() {
        System.out.println("***** - doit");
        System.out.println(new JdbcTemplate(dataSource()).queryForObject("select count(*) from name", Integer.class));
    }

*/

    @Bean
    @Primary
    public DataSource dataSource() {
        final String db = "c:/usr/arr/2015/jwoc/sprint/etime-test.mdb";
        final String sysdb = "c:/usr/arr/data/system.mdw";

/*
        final String db = "o:/raceday/etime.mdb";
        final String sysdb = "o:/system.mdw";
        */

        return DataSourceBuilder
                .create()
                .driverClassName("sun.jdbc.odbc.JdbcOdbcDriver")
                .url("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};dbq="+db+";SystemDB="+sysdb+";UID=admin;PWD=nulltime")
                .build();
    }
}
