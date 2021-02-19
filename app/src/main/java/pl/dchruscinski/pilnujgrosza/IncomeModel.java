package pl.dchruscinski.pilnujgrosza;

public class IncomeModel {

    private int incID;
    private String incDescription;
    private int incValue;
    private int incProfID;
    private int incBudID;
    private int incExpcatID;
    private int incIsRecurring;

    public IncomeModel(int expID, String incDescription, int incValue, int incProfID, int incBudID, int incExpcatID, int incIsRecurring) {
        this.incID = expID;
        this.incDescription = incDescription;
        this.incValue = incValue;
        this.incProfID = incProfID;
        this.incBudID = incBudID;
        this.incExpcatID = incExpcatID;
        this.incIsRecurring = incIsRecurring;
    }

    public IncomeModel() {

    }

    @Override
    public String toString() {
        return "ExpenseModel{" +
                "incID=" + incID +
                ", incDescription='" + incDescription + '\'' +
                ", incValue=" + incValue +
                ", incProfID=" + incProfID +
                ", incBudID=" + incBudID +
                ", incExpcatID=" + incExpcatID +
                ", incIsRecurring=" + incIsRecurring +
                '}';
    }

    public int getIncID() {
        return incID;
    }

    public void setIncID(int incID) {
        this.incID = incID;
    }

    public String getIncDescription() {
        return incDescription;
    }

    public void setIncDescription(String incDescription) {
        this.incDescription = incDescription;
    }

    public int getIncValue() {
        return incValue;
    }

    public void setIncValue(int incValue) {
        this.incValue = incValue;
    }

    public int getIncProfID() {
        return incProfID;
    }

    public void setIncProfID(int incProfID) {
        this.incProfID = incProfID;
    }

    public int getIncBudID() {
        return incBudID;
    }

    public void setIncBudID(int incBudID) {
        this.incBudID = incBudID;
    }

    public int getIncExpcatID() {
        return incExpcatID;
    }

    public void setIncExpcatID(int incExpcatID) {
        this.incExpcatID = incExpcatID;
    }

    public int getIncIsRecurring() {
        return incIsRecurring;
    }

    public void setIncIsRecurring(int incIsRecurring) {
        this.incIsRecurring = incIsRecurring;
    }
}