package prewarner;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {

    static Logger log = Logger.getLogger(TimeUtils.class);

    static boolean tenths = false;

    public static void enableTenths() {
        tenths = true;
    }

    public static void disableTenths() {
        tenths = false;
    }


    /*
    sub s_to_hms {
        my ($d) = @_;

        if (!defined $d) { return undef; }


        return sprintf("%02d:%02d:%02d", $d/3600, ($d/60 % 60), ($d % 60));
    }

    sub excel_time {
        my $a = shift;
        if ($a=~ /(\d+)(.\d+)/) {
        my $part = $2;
        return s_to_hms($part*60*60*24+0.1);
        }
    }
     */
    public static String secToHMS(long sec, boolean enablePlus) {
        String flag = "";
        if (enablePlus) { flag="+"; }
        if (sec < 0) {
            flag = "-";
            sec = Math.abs(sec);
        }
        long hours = sec / 3600;
        long minutes = sec / 60 % 60;
        long seconds = sec % 60;
        if (hours==0 && minutes==0) {
            return String.format("%s00.%02d", flag,  seconds);
        } else if (hours==0) {
            return String.format("%s%02d.%02d", flag, minutes, seconds);
        } else
            return String.format("%s%02d.%02d.%02d", flag, hours, minutes, seconds);
    }

    public static String msecToHMS(long msec, boolean enablePlus) {
        if (tenths) {
            return msecToHMST(msec, enablePlus);
        } else {
            return secToHMS(msec/1000, enablePlus);
        }
    }

    private static String msecToHMST(long msec, boolean enablePlus) {
        String flag = "";
        if (enablePlus) { flag="+"; }
        if (msec < 0) {
            flag = "-";
            msec = Math.abs(msec);
        }
        long sec = msec/1000;
        long hours = sec / 3600;
        long minutes = sec / 60 % 60;
        long seconds = sec % 60;
        long tenthsl = (msec%1000)/100;
        if (hours==0 && minutes==0) {
            return String.format("%s00.%02d,%d", flag,  seconds, tenthsl);
        } else if (hours==0) {
            return String.format("%s%02d.%02d,%d", flag, minutes, seconds, tenthsl);
        } else
            return String.format("%s%02d.%02d.%02d,%d", flag, hours, minutes, seconds, tenthsl);
    }

    private static String secToHMS(long sec) {
        return secToHMS(sec, false);
    }

    public static long excelToSec(double excel) {
        return (long) ((excel % 1) * 60 * 60 * 24 + 0.1);
    }

    public static long excelToMsec(double t) {
        return (long) ((t % 1) * 60 * 60 * 24 * 1000.0D + .5 );
    }


    public static List<Runner> calcPlaceAndTimeBehind(List<Runner> Runners) {
        double prev = 0.0;
        int place = 1;
        int n = 0;
        Double winner = 0.0;
        for (Runner runner : Runners) {
            if (runner.getMtime()>0) {
                n++;
                Double behind;
                if (n==1) {
                    winner = runner.getMtime();
                    behind = null;
                } else {
                    behind = (runner.getMtime() - winner);
                }

                if (n>1) {
                    double diff = Math.abs(runner.getMtime() - prev);
                    if ( diff  > 0.01/(60*60*24)) {
                        place = n;
                    }
                } else {
                    place = 1;
                }
                runner.setResult(place);
                runner.setBehind(behind);

                prev = runner.getMtime();
            }
        }
        return Runners;
    }


    public static List filterList(Integer from, Integer to, List runners) {
        if (to==0) return new ArrayList();
        if (from != null && to != null && to>0) {
            int adjFrom = from;
            int adjTo = to;
            int size = runners.size();
            if (adjFrom >= size) {
                return new ArrayList();
            }
            if (adjTo > size) {
                adjTo = size;
            }
            return runners.subList(adjFrom, adjTo);
        }
        return runners;
    }



    public static long getThisMorning() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long thisMorning = c.getTimeInMillis();
        return thisMorning;
    }

    public static long getNowInMillis() {
        Calendar c = Calendar.getInstance();
        long nowSinceEpoch = c.getTimeInMillis();
        return (nowSinceEpoch - getThisMorning());
    }

    public static long getNowInMillisWithoutOffset() {
        Calendar c = Calendar.getInstance();
        long nowSinceEpoch = c.getTimeInMillis();
        return (nowSinceEpoch - getThisMorning());
    }

    public static String niceDate() {
        DateFormat sdf = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
        return sdf.format(new Date());
    }

    public static Double msecToExcel(Long timeMs) {
//        return (long) ((excel % 1) * 60 * 60 * 24 + 0.1);
        return timeMs / (60*60*24 * 1000.0D);
    }


    public static Double stripDay(Double excelTime) {
        if (excelTime == null) { return 0D;}
        return (excelTime%1);
    }
}
