package prewarner;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * User: hennings
 * Date: 06.07.2015
 * Time: 21:55
 */
public class RadioResults {

    private final List<Runner> allRunners;
    private final Map<Integer, Runner> runnerMap;
    JdbcTemplate jt;

    final static Logger log = Logger.getLogger(TimeUtils.class);

    public RadioResults(JdbcTemplate jt) {
        this.jt = jt;
        this.allRunners = allRunners();
        this.runnerMap = runnerMap();
    }

    public Integer runnerCount() {
        return jt.queryForObject("select count(*) from name", Integer.class);
    }


    private Map<Integer, Runner> runnerMap() {
        final Map<Integer, Runner> runnerMap =
                Maps.uniqueIndex(allRunners, new Function<Runner, Integer>() {
                    @Override
                    public Integer apply(Runner runner) {
                        return runner.getId();
                    }
                });

        return runnerMap;
    }


    private List<Runner> allRunners() {

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
        Map<Integer, RunnerAndTime> lr1 = new HashMap<>();
        for (RunnerAndTime r : l1) {
            lr1.put(r.id, r);
        }
        for (RunnerAndTime r: l2) {
            lr1.remove(r.id);
        }
        Map<Integer, Runner> map = runnerMap;

        Standings s = new Standings(runnerMap, l1);

        List<Runner> res = new ArrayList<>();
        for (Integer rid : lr1.keySet()) {
            Runner runner = map.get(rid);
            if (runner != null && acceptableRunner(runner)) {
                populateRunner(lr1.get(rid), runner, s);
                res.add(runner);
            }
        }
        Collections.sort(res, new Comparator<Runner>() {
            @Override
            public int compare(Runner o1, Runner o2) {
                return o1.getCurrentTime().compareTo(o2.getCurrentTime());
            }
        });
        return res;

    }

    private boolean acceptableRunner(Runner runner) {
        return true; //runner.getIntime() == 0 && !runner.getTeam().equals("NOTEAM");
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

    public List<Runner> lastAt(Integer radio, int number, int maxSec) {
        List<RunnerAndTime> lastList = getRunnersAtRadio(radio);
        Collections.sort(lastList, new Comparator<RunnerAndTime>() {
                    @Override
                    public int compare(RunnerAndTime o1, RunnerAndTime o2) {
                        if (o1.time > o2.time) return -1;
                        else if (o1.time < o2.time) return 1;
                        else return 0;
                    }
                }
        );

        Standings s = new Standings(runnerMap, lastList);

        Map<Integer, Runner> map = runnerMap();
        List<Runner> resList = new ArrayList<>();
        for (RunnerAndTime r  : (lastList.subList(0, number > lastList.size()? lastList.size() : number))) {
            Runner runner = map.get(r.id);
            if (runner!=null && acceptableRunner(runner)) {
                populateRunner(r, runner, s);
                resList.add(runner);
            }
        }
        return resList;
    }

    private void populateRunner(RunnerAndTime r, Runner runner, Standings s) {
        runner.setPassed(TimeUtils.msecToHMS(TimeUtils.excelToMsec(r.time), false));
        runner.setRunningTime(TimeUtils.msecToHMS(TimeUtils.getNowInMillis() - TimeUtils.excelToMsec(r.time), false));
        runner.setCurrentTime(TimeUtils.msecToHMS(TimeUtils.getNowInMillis() - TimeUtils.excelToMsec(runner.getStarttime()), false));
        runner.setPosition(s.getPosition(runner.getClassId(), r.time));
    }

    private static class RunnerAndTime {
        private final double time;
        private final int id;

        public RunnerAndTime(int id, double time) {
            this.id = id;
            this.time = time;
        }
    }

    private static class Standings {
        final Map<String,List<Double>> trs = new HashMap<>();

        public Standings(Map<Integer, Runner> runnerMap, List<RunnerAndTime> l1) {
            for (RunnerAndTime rt : l1) {
                try {
                    String cl = runnerMap.get(rt.id).getClassId();
                    List<Double> cresList = trs.get(cl);
                    if (cresList == null) {
                        cresList = new ArrayList<>();
                        trs.put(cl, cresList);
                    }
                    cresList.add(rt.time);
                } catch (NullPointerException npe) {
                    log.debug("NPE? for " + rt.id + " /"  + runnerMap.get(rt.id));
                }
            }
            for (List<Double> l : trs.values()) {
                Collections.sort(l);
            }
        }

        public int getPosition(String classId, double time) {
            List<Double> reslist = trs.get(classId);
            int i = 1;
            for (Double d: reslist ) {
                if (time>d) {
                    i++;
                }
            }
            return i;

        }
    }
}
