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
import static pl.dchruscinski.pilnujgrosza.TestHelper.createExpense;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpenseTest {
    int transactionDateDay = 1;
    int transactionDateMonth = 1;
    int transactionDateYear = 2023;
    String description = "Test description";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createExpenseWithCorrectData() {
        createExpense(1, 1, 2023);
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createExpenseWithEmptyValue() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_transactionsList_button)).perform(click());
        onView(withId(R.id.trans_fab_addExpense)).perform(click());

        onView(withText(R.string.expense_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.expense_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, transactionDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.expense_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.expense_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expense_createexpense_value_error_empty);
        onView(withId(R.id.expense_createform_text_value)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editExpensesDate() throws InterruptedException {
        createExpense(1, 1, 2023);

        onView(withId(R.id.transaction_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.exp_rc_firstLine)));

        Thread.sleep(3000);

        onView(withId(R.id.expense_editform_text_date)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.expense_editform_submitEdit)).perform(click());
        onView(withText("2023-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editExpensesValue() throws InterruptedException {
        createExpense(1, 1, 2023);

        onView(withId(R.id.transaction_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.exp_rc_firstLine)));

        Thread.sleep(3000);

        onView(withId(R.id.expense_editform_text_value))
                .perform(clearText(), typeText("2000"), closeSoftKeyboard());
        onView(withId(R.id.expense_editform_submitEdit)).perform(click());

        onView(withText("2000")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editExpensesDescription() throws InterruptedException {
        createExpense(1, 1, 2023);

        onView(withId(R.id.transaction_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.exp_rc_firstLine)));

        Thread.sleep(3000);

        onView(withId(R.id.expense_editform_text_desc))
                .perform(clearText(), typeText("Edited description"), closeSoftKeyboard());
        onView(withId(R.id.expense_editform_submitEdit)).perform(click());

        onView(withText("Edited description")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteExpense() throws InterruptedException {
        createExpense(1, 1, 2023);

        onView(withId(R.id.transaction_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.exp_rc_firstLine)));

        Thread.sleep(3000);

        onView(withId(R.id.expense_editform_submitDelete)).perform(click());

        onView(withId(R.id.expense_deleteform_submitDelete)).perform(click());
        onView(withText("2023-01-01")).check(doesNotExist());
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayExpensesList() {
        createExpense(1, 1, 2023);
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<TransactionModel> transactionsList = new ArrayList<>(databaseHelper.getTransactionsList());
        Assert.assertEquals(1, transactionsList.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}