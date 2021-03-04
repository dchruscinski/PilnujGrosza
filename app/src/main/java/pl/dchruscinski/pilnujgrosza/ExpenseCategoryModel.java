package pl.dchruscinski.pilnujgrosza;

public class ExpenseCategoryModel {

    // expense category attributes
    private int expcatID;
    private int expcatProfID;
    private String expcatName;

    // expense category constructors
    public ExpenseCategoryModel(int expcatID, int expcatProfID, String expcatName) {
        this.expcatID = expcatID;
        this.expcatProfID = expcatProfID;
        this.expcatName = expcatName;
    }

    public ExpenseCategoryModel() {
    }

    @Override
    public String toString() {
        return "ExpenseCategoryModel{" +
                "expcatID=" + expcatID +
                "expcatProfID=" + expcatProfID +
                ", expcatName='" + expcatName + '\'' +
                '}';
    }

    // expense category getters and setters
    public int getExpcatID() {
        return expcatID;
    }

    public void setExpcatID(int expcatID) {
        this.expcatID = expcatID;
    }

    public int getExpcatProfID() {
        return expcatProfID;
    }

    public void setExpcatProfID(int expcatProfID) {
        this.expcatProfID = expcatProfID;
    }

    public String getExpcatName() {
        return expcatName;
    }

    public void setExpcatName(String expcatName) {
        this.expcatName = expcatName;
    }
}