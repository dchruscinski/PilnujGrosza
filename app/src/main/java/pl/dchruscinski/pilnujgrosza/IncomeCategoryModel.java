package pl.dchruscinski.pilnujgrosza;

public class IncomeCategoryModel {

    // income category attributes
    private int inccatID;
    private int inccatProfID;
    private String inccatName;

    // income category constructors
    public IncomeCategoryModel(int inccatID, int inccatProfID, String inccatName) {
        this.inccatID = inccatID;
        this.inccatProfID = inccatID;
        this.inccatName = inccatName;
    }

    public IncomeCategoryModel() {
    }

    @Override
    public String toString() {
        return "IncomeCategoryModel{" +
                "inccatID=" + inccatID +
                "inccatProfID=" + inccatProfID +
                ", inccatName='" + inccatName + '\'' +
                '}';
    }

    // expense category getters and setters
    public int getInccatID() {
        return inccatID;
    }

    public void setInccatID(int inccatID) {
        this.inccatID = inccatID;
    }

    public int getInccatProfID() {
        return inccatProfID;
    }

    public void setInccatProfID(int inccatProfID) {
        this.inccatProfID = inccatProfID;
    }

    public String getInccatName() {
        return inccatName;
    }

    public void setInccatName(String inccatName) {
        this.inccatName = inccatName;
    }
}