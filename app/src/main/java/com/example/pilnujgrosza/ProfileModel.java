package com.example.pilnujgrosza;


public class ProfileModel {

    // profile attributes
    private int profID;
    private String profName;
    private String profEmail;
    private String profPIN;
    private String profPINSalt;
    private String profCreationDate;
    private String profLastLoginDate;
    private String profLastLoginAttempt;
    private int profInitialBalance;
    private int profBalance;

    // profile constructors
    public ProfileModel(int profID, String profName, String profEmail, String profPIN, String profPINSalt, String profCreationDate, String profLastLoginDate, String profLastLoginAttempt, int profInitialBalance, int profBalance) {
        this.profID = profID;
        this.profName = profName;
        this.profEmail = profEmail;
        this.profPIN = profPIN;
        this.profPINSalt = profPINSalt;
        this.profCreationDate = profCreationDate;
        this.profLastLoginDate = profLastLoginDate;
        this.profLastLoginAttempt = profLastLoginAttempt;
        this.profInitialBalance = profInitialBalance;
        this.profBalance = profBalance;
    }

    public ProfileModel() {
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "profID=" + profID +
                ", profName='" + profName + '\'' +
                ", profEmail='" + profEmail + '\'' +
                ", profPIN=" + profPIN + '\'' +
                ", profCreationDate='" + profCreationDate + '\'' +
                ", profLastLoginDate='" + profLastLoginDate + '\'' +
                ", profLastLoginAttempt='" + profLastLoginAttempt + '\'' +
                ", profInitialBalance=" + profInitialBalance +
                ", profBalance=" + profBalance +
                '}';
    }

    // profile getters and setters
    public int getProfID() {
        return profID;
    }

    public void setProfID(int profID) {
        this.profID = profID;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getProfEmail() {
        return profEmail;
    }

    public void setProfEmail(String profEmail) {
        this.profEmail = profEmail;
    }

    public String  getProfPIN() {
        return profPIN;
    }

    public void setProfPIN(String profPIN) {
        this.profPIN = profPIN;
    }

    public String getProfPINSalt() {
        return profPINSalt;
    }

    public void setProfPINSalt(String profPINSalt) {
        this.profPINSalt = profPINSalt;
    }

    public String getProfCreationDate() {
        return profCreationDate;
    }

    public void setProfCreationDate(String profCreationDate) {
        this.profCreationDate = profCreationDate;
    }

    public String getProfLastLoginDate() {
        return profLastLoginDate;
    }

    public void setProfLastLoginDate(String profLastLoginDate) {
        this.profLastLoginDate = profLastLoginDate;
    }

    public String getProfLastLoginAttempt() {
        return profLastLoginAttempt;
    }

    public void setProfLastLoginAttempt(String profLastLoginAttempt) {
        this.profLastLoginAttempt = profLastLoginAttempt;
    }

    public int getProfInitialBalance() {
        return profInitialBalance;
    }

    public void setProfInitialBalance(int profInitialBalance) {
        this.profInitialBalance = profInitialBalance;
    }

    public int getProfBalance() {
        return profBalance;
    }

    public void setProfBalance(int profBalance) {
        this.profBalance = profBalance;
    }
}