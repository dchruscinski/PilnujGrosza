package pl.dchruscinski.pilnujgrosza;

import android.content.Context;
import android.widget.DatePicker;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctPin;
import static pl.dchruscinski.pilnujgrosza.TestHelper.createScheduledPayment;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;
import static pl.dchruscinski.pilnujgrosza.TestHelper.textViewHasErrorText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduledPaymentTest {
    int transactionDateDay = 1;
    int transactionDateMonth = 1;
    int transactionDateYear = 2023;
    String transactionValue = "1000";
    String description = "Test description";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createScheduledPaymentWithCorrectData() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createScheduledPaymentWithBadDate() throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_schpay_button)).perform(click());
        onView(withId(R.id.schpay_fab_addScheduledPayment)).perform(click());

        onView(withText(R.string.schpay_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.schpay_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.schpay_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.schpay_createschpay_date_error_badDate);
        onView(withId(R.id.schpay_createform_text_date)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createScheduledPaymentWithEmptyValue() throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_schpay_button)).perform(click());
        onView(withId(R.id.schpay_fab_addScheduledPayment)).perform(click());

        onView(withText(R.string.schpay_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.schpay_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, transactionDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.schpay_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.schpay_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.schpay_createschpay_value_error_empty);
        onView(withId(R.id.schpay_createform_text_value)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editScheduledPaymentsDate() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);

        onView(withId(R.id.schpay_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.schpay_rc_firstLine)));

        onView(withId(R.id.schpay_editform_text_date)).perform(click());
        onView(withId(R.id.schpay_editform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.schpay_editform_submitEdit)).perform(click());
        onView(withText("2023-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editScheduledPaymentsValue() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);

        onView(withId(R.id.schpay_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.schpay_rc_firstLine)));
        onView(withId(R.id.schpay_editform_text_value))
                .perform(clearText(), typeText("2000"), closeSoftKeyboard());
        onView(withId(R.id.schpay_editform_submitEdit)).perform(click());
        onView(withText("2000")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editScheduledPaymentsDescription() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);

        onView(withId(R.id.schpay_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.schpay_rc_firstLine)));

        Thread.sleep(2000);

        onView(withId(R.id.schpay_editform_text_desc))
                .perform(clearText(), typeText("Edited description"), closeSoftKeyboard());
        onView(withId(R.id.schpay_editform_submitEdit)).perform(click());
        onView(withText("Edited description")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteScheduledPayment() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);

        onView(withId(R.id.schpay_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.schpay_rc_firstLine)));
        onView(withId(R.id.schpay_editform_submitDelete)).perform(click());
        onView(withId(R.id.schpay_deleteform_submitDelete)).perform(click());
        onView(withText("2023-01-01")).check(doesNotExist());
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayScheduledPaymentsList() throws InterruptedException {
        createScheduledPayment(1, 1, 2023);
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ScheduledPaymentModel> scheduledPayments = new ArrayList<>(databaseHelper.getScheduledPaymentsList());
        Assert.assertEquals(1, scheduledPayments.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}