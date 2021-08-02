package pl.dchruscinski.pilnujgrosza;

import java.util.Comparator;

public class ShoppingModel {

    private int shoID;
    private int shoProfID;
    private int shoRecID;
    private int shoExpID;
    private String shoName;
    private String shoDate;
    private String shoDesc;
    private int shoValue;

    public ShoppingModel(int shoID, int shoProfID, int shoRecID, int shoExpID, String shoName, String shoDate, String shoDesc, int shoValue) {
        this.shoID = shoID;
        this.shoProfID = shoProfID;
        this.shoRecID = shoRecID;
        this.shoExpID = shoExpID;
        this.shoName = shoName;
        this.shoDate = shoDate;
        this.shoDesc = shoDesc;
        this.shoValue = shoValue;
    }

    public ShoppingModel() {

    }

    public static Comparator<ShoppingModel> shoppingDateAscComparator = new Comparator<ShoppingModel>() {
        @Override
        public int compare(ShoppingModel s1, ShoppingModel s2) {
            return s1.getShoDate().compareTo(s2.getShoDate());
        }
    };

    public static Comparator<ShoppingModel> shoppingDateDescComparator = new Comparator<ShoppingModel>() {
        @Override
        public int compare(ShoppingModel s1, ShoppingModel s2) {
            return s2.getShoDate().compareTo(s1.getShoDate());
        }
    };

    public static Comparator<ShoppingModel> shoppingNameAscComparator = new Comparator<ShoppingModel>() {
        @Override
        public int compare(ShoppingModel s1, ShoppingModel s2) {
            return s1.getShoName().compareTo(s2.getShoName());
        }
    };

    public static Comparator<ShoppingModel> shoppingNameDescComparator = new Comparator<ShoppingModel>() {
        @Override
        public int compare(ShoppingModel s1, ShoppingModel s2) {
            return s2.getShoName().compareTo(s1.getShoName());
        }
    };

    @Override
    public String toString() {
        return "ShoppingModel{" +
                "shoID=" + shoID +
                ", shoProfID=" + shoProfID +
                ", shoRecID=" + shoRecID +
                ", shoExpID=" + shoExpID +
                ", shoName='" + shoName + '\'' +
                ", shoDate='" + shoDate + '\'' +
                ", shoDesc='" + shoDesc + '\'' +
                ", shoValue=" + shoValue +
                '}';
    }

    public int getShoID() {
        return shoID;
    }

    public void setShoID(int shoID) {
        this.shoID = shoID;
    }

    public int getShoProfID() {
        return shoProfID;
    }

    public void setShoProfID(int shoProfID) {
        this.shoProfID = shoProfID;
    }

    public int getShoRecID() {
        return shoRecID;
    }

    public void setShoRecID(int shoRecID) {
        this.shoRecID = shoRecID;
    }

    public int getShoExpID() {
        return shoExpID;
    }

    public void setShoExpID(int shoExpID) {
        this.shoExpID = shoExpID;
    }

    public String getShoName() {
        return shoName;
    }

    public void setShoName(String shoName) {
        this.shoName = shoName;
    }

    public String getShoDate() {
        return shoDate;
    }

    public void setShoDate(String shoDate) {
        this.shoDate = shoDate;
    }

    public String getShoDesc() {
        return shoDesc;
    }

    public void setShoDesc(String shoDesc) {
        this.shoDesc = shoDesc;
    }

    public int getShoValue() {
        return shoValue;
    }

    public void setShoValue(int shoValue) {
        this.shoValue = shoValue;
    }
}