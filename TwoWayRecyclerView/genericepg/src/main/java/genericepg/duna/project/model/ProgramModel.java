package genericepg.duna.project.model;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class ProgramModel extends BaseProgramModel {
    private String title;
    private String description;
    private int colorTitle;
    private int colorDescription;
    private int programID;
    private int avs;

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
