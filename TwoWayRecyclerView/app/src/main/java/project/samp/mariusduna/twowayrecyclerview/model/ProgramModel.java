package project.samp.mariusduna.twowayrecyclerview.model;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class ProgramModel {
    private String title;
    private String description;
    private int colorTitle;
    private int colorDescription;
    private int programID;
    private int avs;
    private long startTime;
    private long endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(int colorTitle) {
        this.colorTitle = colorTitle;
    }

    public int getColorDescription() {
        return colorDescription;
    }

    public void setColorDescription(int colorDescription) {
        this.colorDescription = colorDescription;
    }

    public int getProgramID() {
        return programID;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public int getAvs() {
        return avs;
    }

    public void setAvs(int avs) {
        this.avs = avs;
    }

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
