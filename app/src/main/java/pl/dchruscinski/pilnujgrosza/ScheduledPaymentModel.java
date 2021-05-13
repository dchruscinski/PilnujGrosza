package pl.dchruscinski.pilnujgrosza;

import java.util.Comparator;

public class ScheduledPaymentModel {

    private int schpayID;
    private String schpayDescription;
    private int schpayValue;
    private String schpayDate;
    private int schpayProfID;
    private int schpayBudID;
    private int schpayCatID;

    public ScheduledPaymentModel(int schpayID, String schpayDescription, int schpayValue, String schpayDate, int schpayProfID, int schpayBudID, int schpayCatID) {
        this.schpayID = schpayID;
        this.schpayDescription = schpayDescription;
        this.schpayValue = schpayValue;
        this.schpayDate = schpayDate;
        this.schpayProfID = schpayProfID;
        this.schpayBudID = schpayBudID;
        this.schpayCatID = schpayCatID;
    }

    public ScheduledPaymentModel() {

    }

    public static Comparator<ScheduledPaymentModel> scheduledPaymentDateAscComparator = new Comparator<ScheduledPaymentModel>() {
        @Override
        public int compare(ScheduledPaymentModel t1, ScheduledPaymentModel t2) {
            return t1.getSchpayDate().compareTo(t2.getSchpayDate());
        }
    };

    public static Comparator<ScheduledPaymentModel> scheduledPaymentDateDescComparator = new Comparator<ScheduledPaymentModel>() {
        @Override
        public int compare(ScheduledPaymentModel t1, ScheduledPaymentModel t2) {
            return t2.getSchpayDate().compareTo(t1.getSchpayDate());
        }
    };

    public static Comparator<ScheduledPaymentModel> scheduledPaymentValueAscComparator = new Comparator<ScheduledPaymentModel>() {
        @Override
        public int compare(ScheduledPaymentModel t1, ScheduledPaymentModel t2) {
            return t1.getSchpayValue() - t2.getSchpayValue();
        }
    };

    public static Comparator<ScheduledPaymentModel> scheduledPaymentValueDescComparator = new Comparator<ScheduledPaymentModel>() {
        @Override
        public int compare(ScheduledPaymentModel t1, ScheduledPaymentModel t2) {
            return t2.getSchpayValue() - t1.getSchpayValue();
        }
    };

    @Override
    public String toString() {
        return "ScheduledPaymentModel{" +
                "schpayID=" + schpayID +
                ", schpayDescription='" + schpayDescription + '\'' +
                ", schpayValue=" + schpayValue +
                ", schpayDate='" + schpayDate + '\'' +
                ", schpayProfID=" + schpayProfID +
                ", schpayBudID=" + schpayBudID +
                ", schpayCatID=" + schpayCatID +
                '}';
    }

    public int getSchpayID() {
        return schpayID;
    }

    public void setSchpayID(int schpayID) {
        this.schpayID = schpayID;
    }

    public String getSchpayDescription() {
        return schpayDescription;
    }

    public void setSchpayDescription(String schpayDescription) {
        this.schpayDescription = schpayDescription;
    }

    public int getSchpayValue() {
        return schpayValue;
    }

    public void setSchpayValue(int schpayValue) {
        this.schpayValue = schpayValue;
    }

    public String getSchpayDate() {
        return schpayDate;
    }

    public void setSchpayDate(String schpayDate) {
        this.schpayDate = schpayDate;
    }

    public int getSchpayProfID() {
        return schpayProfID;
    }

    public void setSchpayProfID(int schpayProfID) {
        this.schpayProfID = schpayProfID;
    }

    public int getSchpayBudID() {
        return schpayBudID;
    }

    public void setSchpayBudID(int schpayBudID) {
        this.schpayBudID = schpayBudID;
    }

    public int getSchpayCatID() {
        return schpayCatID;
    }

    public void setSchpayCatID(int schpayCatID) {
        this.schpayCatID = schpayCatID;
    }
}