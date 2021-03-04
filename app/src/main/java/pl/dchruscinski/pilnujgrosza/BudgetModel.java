package pl.dchruscinski.pilnujgrosza;

public class BudgetModel {

    private int budID;
    private int budProfID;
    private int budInitialAmount;
    private int budAmount;
    private String budDescription;
    private String budStartDate;
    private String budEndDate;

    public BudgetModel(int budID, int budProfID, int budInitialAmount, int budAmount, String budDescription, String budStartDate, String budEndDate) {
        this.budID = budID;
        this.budProfID = budProfID;
        this.budInitialAmount = budInitialAmount;
        this.budAmount = budAmount;
        this.budDescription = budDescription;
        this.budStartDate = budStartDate;
        this.budEndDate = budEndDate;
    }

    public BudgetModel() {

    }

    @Override
    public String toString() {
        return "BudgetModel{" +
                "budID=" + budID +
                ", budProfID=" + budProfID +
                ", budgetInitialAmount=" + budInitialAmount +
                ", budAmount=" + budAmount +
                ", budDescription='" + budDescription + '\'' +
                ", budStartDate='" + budStartDate + '\'' +
                ", budEndDate='" + budEndDate + '\'' +
                '}';
    }

    public int getBudID() {
        return budID;
    }

    public void setBudID(int budID) {
        this.budID = budID;
    }

    public int getBudProfID() {
        return budProfID;
    }

    public void setBudProfID(int budProfID) {
        this.budProfID = budProfID;
    }

    public int getBudInitialAmount() {
        return budInitialAmount;
    }

    public void setBudInitialAmount(int budInitialAmount) {
        this.budInitialAmount = budInitialAmount;
    }

    public int getBudAmount() {
        return budAmount;
    }

    public void setBudAmount(int budAmount) {
        this.budAmount = budAmount;
    }

    public String getBudDescription() {
        return budDescription;
    }

    public void setBudDescription(String budDescription) {
        this.budDescription = budDescription;
    }

    public String getBudStartDate() {
        return budStartDate;
    }

    public void setBudStartDate(String budStartDate) {
        this.budStartDate = budStartDate;
    }

    public String getBudEndDate() {
        return budEndDate;
    }

    public void setBudEndDate(String budEndDate) {
        this.budEndDate = budEndDate;
    }
}