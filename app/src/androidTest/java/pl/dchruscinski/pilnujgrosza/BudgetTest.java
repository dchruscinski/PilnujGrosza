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
import static pl.dchruscinski.pilnujgrosza.TestHelper.createBudget;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;
import static pl.dchruscinski.pilnujgrosza.TestHelper.textViewHasErrorText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BudgetTest {
    int firstBudgetStartDateDay = 1;
    int firstBudgetEndDateDay = 30;
    int firstBudgetDateMonth = 1;
    int budgetDateYear = 2021;
    String initialAmount = "1000";
    String description = "Test description";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createBudgetWithCorrectData() throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_budgetmenu_button)).perform(click());
        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetStartDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetEndDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createBudgetWithEmptyInitialAmount() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_budgetmenu_button)).perform(click());
        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetStartDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetEndDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.budget_createbudget_initialAmount_error_empty);
        onView(withId(R.id.budget_createform_text_initialAmount)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createBudgetWithStartDateAfterEndDate() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_budgetmenu_button)).perform(click());
        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetEndDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, firstBudgetStartDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.budget_createbudget_date_error_startDateAfterEndDate);
        onView(withId(R.id.budget_createform_text_startDate)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createBudgetWithStartDateBeforeOtherBudgetsEndDate() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 8));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 18));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.budget_createbudget_date_error_startDateBeforeOtherBudgetsEndDate);
        onView(withId(R.id.budget_createform_text_startDate)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createBudgetWithEndDateWhileAnotherBudgetIsRunning() {
        createBudget(10, 1, 2021, 20, 1, 2021);

        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 1));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 12));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.budget_createbudget_date_error_endDateWhileOtherBudgetIsRunning);
        onView(withId(R.id.budget_createform_text_endDate)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createBudgetWhileAnotherBudgetIsRunningBetweenStartAndEndDate() {
        createBudget(10, 1, 2021, 20, 1, 2021);

        onView(withId(R.id.budget_fab_addBudget)).perform(click());

        onView(withText(R.string.budget_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.budget_createform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 1));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 30));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.budget_createbudget_date_error_otherBudgetIsExistingBetweenStartAndEndDate);
        onView(withId(R.id.budget_createform_text_endDate)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editBudgetsStartDate() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.budget_rc_desc)));

        onView(withText(R.string.title_activity_budgettransaction)).check(matches(isDisplayed()));
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withId(R.id.budget_editform_fromDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.budget_editform_submit)).perform(click());
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withText("2021-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editBudgetsEndDate() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.budget_rc_desc)));

        onView(withText(R.string.title_activity_budgettransaction)).check(matches(isDisplayed()));
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withId(R.id.budget_editform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(budgetDateYear, firstBudgetDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.budget_editform_submit)).perform(click());
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withText("2021-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editBudgetsInitialAmount() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.budget_rc_desc)));

        onView(withText(R.string.title_activity_budgettransaction)).check(matches(isDisplayed()));
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withId(R.id.budget_editform_text_initialAmount))
                .perform(clearText(), typeText("2000"), closeSoftKeyboard());

        onView(withId(R.id.budget_editform_submit)).perform(click());
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withText("2000")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editBudgetsDescription() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.budget_rc_desc)));

        onView(withText(R.string.title_activity_budgettransaction)).check(matches(isDisplayed()));
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withId(R.id.budget_editform_text_desc))
                .perform(clearText(), typeText("Edited description"), closeSoftKeyboard());

        onView(withId(R.id.budget_editform_submit)).perform(click());
        onView(withId(R.id.budtrans_button_edit)).perform(click());
        onView(withText("Edited description")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteBudget() {
        createBudget(1, 1, 2021, 10, 1, 2021);

        onView(withId(R.id.budget_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.budget_rc_desc)));

        onView(withText(R.string.title_activity_budgettransaction)).check(matches(isDisplayed()));
        onView(withId(R.id.budtrans_button_delete)).perform(click());
        onView(withId(R.id.budget_delete_submit)).perform(click());
        onView(withId(R.id.budget_delete_cont_deleteTrans)).perform(click());
        onView(withText("2021-01-01")).check(doesNotExist());
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayBudgetsList() {
        createBudget(1, 1, 2021, 10, 1, 2021);
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<BudgetModel> budgetsList = new ArrayList<>(databaseHelper.getBudgetsList());
        Assert.assertEquals(1, budgetsList.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}