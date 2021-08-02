package pl.dchruscinski.pilnujgrosza;

import java.util.Comparator;

public class ShoppingContentModel {

    private int shoContID;
    private int shoContShoID;
    private String shoContName;
    private String shoContAmount;
    private String shoContUnit;
    private int shoContValue;
    private boolean shoContChecked;

    public ShoppingContentModel(int shoContID, int shoContShoID, String shoContName, String shoContAmount, String shoContUnit, int shoContValue, boolean shoContChecked) {
        this.shoContID = shoContID;
        this.shoContShoID = shoContShoID;
        this.shoContName = shoContName;
        this.shoContAmount = shoContAmount;
        this.shoContUnit = shoContUnit;
        this.shoContValue = shoContValue;
        this.shoContChecked = shoContChecked;
    }

    public ShoppingContentModel() {

    }

    public static Comparator<ShoppingContentModel> shoppingContNameAscComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s1.getShoContName().compareTo(s2.getShoContName());
        }
    };

    public static Comparator<ShoppingContentModel> shoppingContNameDescComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s2.getShoContName().compareTo(s1.getShoContName());
        }
    };

    public static Comparator<ShoppingContentModel> shoppingContValueAscComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s1.getShoContValue() - s2.getShoContValue();
        }
    };

    public static Comparator<ShoppingContentModel> shoppingContValueDescComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s2.getShoContValue() - s1.getShoContValue();
        }
    };

    public static Comparator<ShoppingContentModel> shoppingContUnitAscComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s1.getShoContUnit().compareTo(s2.getShoContUnit());
        }
    };

    public static Comparator<ShoppingContentModel> shoppingContUnitDescComparator = new Comparator<ShoppingContentModel>() {
        @Override
        public int compare(ShoppingContentModel s1, ShoppingContentModel s2) {
            return s2.getShoContUnit().compareTo(s1.getShoContUnit());
        }
    };

    @Override
    public String toString() {
        return "ShoppingContentModel{" +
                "shoContID=" + shoContID +
                ", shoContShoID=" + shoContShoID +
                ", shoContName='" + shoContName + '\'' +
                ", shoContAmount=" + shoContAmount + '\'' +
                ", shoContUnit='" + shoContUnit + '\'' +
                ", shoContValue=" + shoContValue +
                ", shoContChecked=" + shoContChecked +
                '}';
    }

    public int getShoContID() {
        return shoContID;
    }

    public void setShoContID(int shoContID) {
        this.shoContID = shoContID;
    }

    public int getShoContShoID() {
        return shoContShoID;
    }

    public void setShoContShoID(int shoContShoID) {
        this.shoContShoID = shoContShoID;
    }

    public String getShoContName() {
        return shoContName;
    }

    public void setShoContName(String shoContName) {
        this.shoContName = shoContName;
    }

    public String getShoContAmount() {
        return shoContAmount;
    }

    public void setShoContAmount(String shoContAmount) { this.shoContAmount = shoContAmount; }

    public String getShoContUnit() {
        return shoContUnit;
    }

    public void setShoContUnit(String shoContUnit) {
        this.shoContUnit = shoContUnit;
    }

    public int getShoContValue() {
        return shoContValue;
    }

    public void setShoContValue(int shoContValue) {
        this.shoContValue = shoContValue;
    }

    public boolean isShoContChecked() {
        return shoContChecked;
    }

    public void setShoContChecked(boolean shoContChecked) {
        this.shoContChecked = shoContChecked;
    }
}