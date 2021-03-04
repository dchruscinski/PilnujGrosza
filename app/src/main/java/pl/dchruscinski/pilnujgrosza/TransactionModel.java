package pl.dchruscinski.pilnujgrosza;

public class TransactionModel {

    private int transID;
    private String transType;
    private String transDescription;
    private int transValue;
    private String transDate;
    private int transProfID;
    private int transBudID;
    private int transCatID;

    public TransactionModel(int transID, String transType, String transDescription, int transValue, String transDate, int transProfID, int transBudID, int transCatID) {
        this.transID = transID;
        this.transType = transType;
        this.transDescription = transDescription;
        this.transValue = transValue;
        this.transDate = transDate;
        this.transProfID = transProfID;
        this.transBudID = transBudID;
        this.transCatID = transCatID;
    }

    public TransactionModel() {

    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "transID=" + transID +
                ", transType='" + transType + '\'' +
                ", transDescription='" + transDescription + '\'' +
                ", transValue=" + transValue +
                ", transDate=" + transDate +
                ", transProfID=" + transProfID +
                ", transBudID=" + transBudID +
                ", transCatID=" + transCatID +
                '}';
    }

    public int getTransID() {
        return transID;
    }

    public void setTransID(int transID) {
        this.transID = transID;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransDescription() {
        return transDescription;
    }

    public void setTransDescription(String transDescription) {
        this.transDescription = transDescription;
    }

    public int getTransValue() {
        return transValue;
    }

    public void setTransValue(int transValue) {
        this.transValue = transValue;
    }

    public int getTransProfID() {
        return transProfID;
    }

    public void setTransProfID(int transProfID) {
        this.transProfID = transProfID;
    }

    public int getTransBudID() {
        return transBudID;
    }

    public void setTransBudID(int transBudID) {
        this.transBudID = transBudID;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public int getTransCatID() {
        return transCatID;
    }

    public void setTransCatID(int transCatID) {
        this.transCatID = transCatID;
    }
}