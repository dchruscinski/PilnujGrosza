package pl.dchruscinski.pilnujgrosza;

import java.util.Arrays;
import java.util.Comparator;

public class ReceiptModel {

    private int recID;
    private int recProfID;
    private int recExpID;
    private String recImg;
    private String recName;
    private String recDate;
    private String recDesc;
    private String recData;

    public ReceiptModel(int recID, int recProfID, int recExpID, String recImg, String recName, String recDate, String recDesc, String recData) {
        this.recID = recID;
        this.recProfID = recProfID;
        this.recExpID = recExpID;
        this.recImg = recImg;
        this.recName = recName;
        this.recDate = recDate;
        this.recDesc = recDesc;
        this.recData = recData;
    }

    public ReceiptModel() {

    }

    public static Comparator<ReceiptModel> receiptDateAscComparator = new Comparator<ReceiptModel>() {
        @Override
        public int compare(ReceiptModel s1, ReceiptModel s2) {
            return s1.getRecDate().compareTo(s2.getRecDate());
        }
    };

    public static Comparator<ReceiptModel> receiptDateDescComparator = new Comparator<ReceiptModel>() {
        @Override
        public int compare(ReceiptModel s1, ReceiptModel s2) {
            return s2.getRecDate().compareTo(s1.getRecDate());
        }
    };

    public static Comparator<ReceiptModel> receiptNameAscComparator = new Comparator<ReceiptModel>() {
        @Override
        public int compare(ReceiptModel s1, ReceiptModel s2) {
            return s1.getRecName().compareTo(s2.getRecName());
        }
    };

    public static Comparator<ReceiptModel> receiptNameDescComparator = new Comparator<ReceiptModel>() {
        @Override
        public int compare(ReceiptModel s1, ReceiptModel s2) {
            return s2.getRecName().compareTo(s1.getRecName());
        }
    };

    @Override
    public String toString() {
        return "ReceiptModel{" +
                "recID=" + recID +
                ", recProfID=" + recProfID +
                ", recExpID=" + recExpID +
                ", recImg='" + recImg + '\'' +
                ", recName='" + recName + '\'' +
                ", recDate='" + recDate + '\'' +
                ", recDesc='" + recDesc + '\'' +
                ", recData='" + recData + '\'' +
                '}';
    }

    public int getRecID() {
        return recID;
    }

    public void setRecID(int recID) {
        this.recID = recID;
    }

    public int getRecProfID() {
        return recProfID;
    }

    public void setRecProfID(int recProfID) {
        this.recProfID = recProfID;
    }

    public int getRecExpID() {
        return recExpID;
    }

    public void setRecExpID(int recExpID) {
        this.recExpID = recExpID;
    }

    public String getRecImg() {
        return recImg;
    }

    public void setRecImg(String recImg) {
        this.recImg = recImg;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getRecDate() {
        return recDate;
    }

    public void setRecDate(String recDate) {
        this.recDate = recDate;
    }

    public String getRecDesc() {
        return recDesc;
    }

    public void setRecDesc(String recDesc) {
        this.recDesc = recDesc;
    }

    public String getRecData() {
        return recData;
    }

    public void setRecData(String recData) {
        this.recData = recData;
    }
}