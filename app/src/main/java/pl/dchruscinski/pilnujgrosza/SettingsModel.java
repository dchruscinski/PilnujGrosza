package pl.dchruscinski.pilnujgrosza;

public class SettingsModel {

    private int setID;
    private int setProfID;
    private String setName;
    private String setValue;

    public SettingsModel(int setID, int setProfID, String setName, String setValue) {
        this.setID = setID;
        this.setProfID = setProfID;
        this.setName = setName;
        this.setValue = setValue;
    }

    public SettingsModel() {

    }

    @Override
    public String toString() {
        return "SettingsModel{" +
                "setID=" + setID +
                ", setProfID=" + setProfID +
                ", setName='" + setName + '\'' +
                ", setValue='" + setValue + '\'' +
                '}';
    }

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
    }

    public int getSetProfID() {
        return setProfID;
    }

    public void setSetProfID(int setProfID) {
        this.setProfID = setProfID;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetValue() {
        return setValue;
    }

    public void setSetValue(String setValue) {
        this.setValue = setValue;
    }
}