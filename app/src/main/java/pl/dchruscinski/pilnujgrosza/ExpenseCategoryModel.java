package pl.dchruscinski.pilnujgrosza;

public class ExpenseCategoryModel {

    // expense category attributes
    private int expcatID;
    private String expcatName;

    // expense category constructors
    public ExpenseCategoryModel(int expcatID, String expcatName) {
        this.expcatID = expcatID;
        this.expcatName = expcatName;
    }

    public ExpenseCategoryModel() {
    }

    @Override
    public String toString() {
        return "ExpenseCategoryModel{" +
                "expcatID=" + expcatID +
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

    public String getExpcatName() {
        return expcatName;
    }

    public void setExpcatName(String expcatName) {
        this.expcatName = expcatName;
    }
}