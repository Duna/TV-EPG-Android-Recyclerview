package genericepg.duna.project.model;

/**
 * Created by Marius Duna on 9/30/2016.
 */

//Extend your program model from this base class
public class BaseProgramModel {
    private long startTime;
    private long endTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
