package epg.duna.twowayrecyclerview.model;

import genericepg.duna.project.model.BaseProgramModel;

/**
 * Created by Marius Duna on 10/3/2016.
 */

public class ProgramModel extends BaseProgramModel {
    private String title;
    private String description;
    private int colorTitle;
    private int colorDescription;
    private int programID;
    private int avs;

    public ProgramModel(long start, long end) {
        setStartTime(start);
        setEndTime(end);
    }

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

}