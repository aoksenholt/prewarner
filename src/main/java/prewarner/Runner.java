package prewarner;

public class Runner {
    private final String name;
    private final String ename;
    private final int startno;
    private final String team;
    private final String classId;
    private final int id;
    private final double starttime;
    private final double intime;
    private final double mtime;
    private int result;
    private Double behind;
    private String passed;
    private String runningTime;
    private String currentTime;
    private int position;

    public Runner(String name, String ename, int startno, String team, String classId, int id, double start, double intime, double mtime) {
        this.name = name;
        this.ename = ename;
        this.startno = startno;
        this.team = team.toUpperCase();
        this.classId = classId;
        this.id = id;
        this.starttime = start;
        this.intime = intime;
        this.mtime = mtime;
    }

    public String getName() {
        return name;
    }

    public String getEname() {
        return ename;
    }

    public int getStartno() {
        return startno;
    }

    public String getTeam() {
        return team;
    }

    public String getClassId() {
        return classId;
    }

    public int getId() {
        return id;
    }

    public double getStarttime() {
        return starttime;
    }

    public double getIntime() {
        return intime;
    }

    public double getMtime() {
        return mtime;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setBehind(Double behind) {
        this.behind = behind;
    }

    public Double getBehind() {
        return behind;
    }


    public String getStartTimeFormatted() {
        return TimeUtils.msecToHMS(TimeUtils.excelToMsec(this.starttime), false);
    }

    public String getFinishTimeFormatted() {
        return TimeUtils.msecToHMS(TimeUtils.excelToMsec(this.intime), false);
    }

    public String getResultTimeFormatted() {
        return TimeUtils.msecToHMS(TimeUtils.excelToMsec(this.mtime), false);
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }

    public String getPassed() {
        return passed;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public String getRunningTimeFormatted() {
        return runningTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}