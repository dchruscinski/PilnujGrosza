package pl.dchruscinski.pilnujgrosza;

public class ExpenseModel {

    private int expID;
    private String expDescription;
    private int expValue;
    private int expProfID;
    private int expBudID;
    private int expExpcatID;
    private String expPayDate;
    private int expIsRecurring;

    public ExpenseModel(int expID, String expDescription, int expValue, int expProfID, int expBudID, int expExpcatID, String expPayDate, int expIsRecurring) {
        this.expID = expID;
        this.expDescription = expDescription;
        this.expValue = expValue;
        this.expProfID = expProfID;
        this.expBudID = expBudID;
        this.expExpcatID = expExpcatID;
        this.expPayDate = expPayDate;
        this.expIsRecurring = expIsRecurring;
    }

    public ExpenseModel() {

    }

    @Override
    public String toString() {
        return "ExpenseModel{" +
                "expID=" + expID +
                ", expDescription='" + expDescription + '\'' +
                ", expValue=" + expValue +
                ", expProfID=" + expProfID +
                ", expBudID=" + expBudID +
                ", expExpcatID=" + expExpcatID +
                ", expPayDate='" + expPayDate + '\'' +
                ", expIsRecurring=" + expIsRecurring +
                '}';
    }

    public int getExpID() {
        return expID;
    }

    public void setExpID(int expID) {
        this.expID = expID;
    }

    public String getExpDescription() {
        return expDescription;
    }

    public void setExpDescription(String expDescription) {
        this.expDescription = expDescription;
    }

    public int getExpValue() {
        return expValue;
    }

    public void setExpValue(int expValue) {
        this.expValue = expValue;
    }

    public int getExpProfID() {
        return expProfID;
    }

    public void setExpProfID(int expProfID) {
        this.expProfID = expProfID;
    }

    public int getExpBudID() {
        return expBudID;
    }

    public void setExpBudID(int expBudID) {
        this.expBudID = expBudID;
    }

    public int getExpExpcatID() {
        return expExpcatID;
    }

    public void setExpExpcatID(int expExpcatID) {
        this.expExpcatID = expExpcatID;
    }

    public String getExpPayDate() {
        return expPayDate;
    }

    public void setExpPayDate(String expPayDate) {
        this.expPayDate = expPayDate;
    }

    public int getExpIsRecurring() {
        return expIsRecurring;
    }

    public void setExpIsRecurring(int expIsRecurring) {
        this.expIsRecurring = expIsRecurring;
    }
}