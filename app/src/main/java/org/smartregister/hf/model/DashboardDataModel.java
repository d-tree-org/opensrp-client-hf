package org.smartregister.hf.model;

public class DashboardDataModel {

    private int lastThreeDaysReferralCount;
    private int referralsAttendedTodayCount;

    public DashboardDataModel(){}

    public int getLastThreeDaysReferralCount() {
        return lastThreeDaysReferralCount;
    }

    public int getReferralsAttendedTodayCount() {
        return referralsAttendedTodayCount;
    }

    public void setLastThreeDaysReferralCount(int lastThreeDaysReferralCount) {
        this.lastThreeDaysReferralCount = lastThreeDaysReferralCount;
    }

    public void setReferralsAttendedTodayCount(int referralsAttendedTodayCount) {
        this.referralsAttendedTodayCount = referralsAttendedTodayCount;
    }
}
