package prewarner;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * User: hennings
 * Date: 06.07.2015
 * Time: 21:55
 */
@Component
public class RadioResults {

    @Autowired
    JdbcTemplate jt;

    final static Logger log = Logger.getLogger(TimeUtils.class);


    public Integer runnerCount() {
        return jt.queryForObject("select count(*) from name", Integer.class);
    }


    public Map<Integer, Runner> runnerMap() {
        final Map<Integer, Runner> runnerMap =
                Maps.uniqueIndex(allRunners(), new Function<Runner, Integer>() {
                    @Override
                    public Integer apply(Runner runner) {
                        return runner.getId();
                    }
                });

        return runnerMap;
    }


    public List<Runner> allRunners() {

        final List<Runner> runners =
                jt.query("SELECT name,ename,startno,team,class,id,starttime,intime,intime-starttime as mtime FROM name",
                        new RowMapper<Runner>() {
                            @Override
                            public Runner mapRow(ResultSet rs, int i) throws SQLException {
                                return new Runner(
                                        rs.getString(1),
                                        rs.getString(2),
                                        rs.getInt(3),
                                        rs.getString(4),
                                        rs.getString(5),
                                        rs.getInt(6),
                                        rs.getDouble(7),
                                        rs.getDouble(8),
                                        rs.getDouble(9)
                                );
                            }
                        });


        return runners;
    }


    public List<Runner> runnersBetween(Integer r1, Integer r2) {


        List<RunnerAndTime> l1 = getRunnersAtRadio(r1);
        List<RunnerAndTime> l2 = getRunnersAtRadio(r2);
        Map<Integer, RunnerAndTime> lr1 = new HashMap<Integer, RunnerAndTime>();
        for (RunnerAndTime r : l1) {
            lr1.put(r.id, r);
        }
        for (RunnerAndTime r: l2) {
            lr1.remove(r.id);
        }
        Map<Integer, Runner> map = runnerMap();
        List<Runner> res = new ArrayList<>();
        for (Integer rid : lr1.keySet()) {
            Runner runner = map.get(rid);
            if (runner!=null) {
                runner.setPassed(TimeUtils.msecToHMS(TimeUtils.excelToMsec(lr1.get(rid).time), false));
                runner.setRunningTime(TimeUtils.msecToHMS(TimeUtils.getNowInMillis() - TimeUtils.excelToMsec(lr1.get(rid).time), false));
                res.add(runner);
            }
        }
        return res;

    }

    private List<RunnerAndTime> getRunnersAtRadio(Integer r1) {
        return jt.query("select id, mintime FROM mellom WHERE iplace=?", new Object[] { r1 },
                new RowMapper<RunnerAndTime>() {

                    @Override
                    public RunnerAndTime mapRow(ResultSet resultSet, int i) throws SQLException {
                        int id = resultSet.getInt(1);
                        double time = resultSet.getDouble(2);
                        return new RunnerAndTime(id, time);
                    }
                }  );
    }

    private static class RunnerAndTime {
        private final double time;
        private final int id;

        public RunnerAndTime(int id, double time) {
            this.id = id;
            this.time = time;
        }
    }

}
