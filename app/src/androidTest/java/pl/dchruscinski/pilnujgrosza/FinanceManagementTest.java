package pl.dchruscinski.pilnujgrosza;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctPin;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FinanceManagementTest {

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void goToBudgetsList() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_budgetmenu_button)).perform(click());

        onView(withText(R.string.title_activity_budget)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void goToTransactionsList() throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();

        Thread.sleep(2000);

        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_transactionsList_button)).perform(click());

        onView(withText(R.string.title_activity_transaction)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void goToScheduledPaymentsList() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_schpay_button)).perform(click());

        onView(withText(R.string.title_activity_scheduled_payment)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void goToIncomeCategoriesList() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_inccat_button)).perform(click());

        onView(withText(R.string.title_activity_income_category)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void goToExpenseCategoriesList() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_expcat_button)).perform(click());

        onView(withText(R.string.title_activity_expense_category)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void goToStatisticsMenu() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());

        onView(withId(R.id.finmgmt_statistics_button)).perform(click());

        onView(withText(R.string.title_activity_statistics)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}