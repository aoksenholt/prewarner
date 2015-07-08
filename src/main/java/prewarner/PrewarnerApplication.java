package prewarner;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootApplication
public class PrewarnerApplication {

    final static Logger log = Logger.getLogger(PrewarnerApplication.class);


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

    @PostConstruct
    @Autowired
    public void doit() {
        new JdbcTemplate(dataSource()).query("select * from radiopost", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                log.info(String.format("%4d %4d %-15s", rs.getInt(1), rs.getInt(2), rs.getString(3)));
            }
        })  ;
    }



    @Bean
    @Primary
    public DataSource dataSource() {
        final String db = "c:/usr/arr/2015/jwoc/sprint/etime-test.mdb";
        final String sysdb = "c:/usr/arr/data/system.mdw";


        //final String db = "o:/raceday/etime.mdb";
      //  final String sysdb = "o:/system.mdw";

        return DataSourceBuilder
                .create()
                .driverClassName("sun.jdbc.odbc.JdbcOdbcDriver")
                .url("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};dbq="+db+";SystemDB="+sysdb+";UID=admin;PWD=nulltime")
                .build();
    }
}
