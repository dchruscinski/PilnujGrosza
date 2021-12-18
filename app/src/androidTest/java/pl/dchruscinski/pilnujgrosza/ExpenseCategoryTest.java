package pl.dchruscinski.pilnujgrosza;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctPin;
import static pl.dchruscinski.pilnujgrosza.TestHelper.createExpenseCategory;
import static pl.dchruscinski.pilnujgrosza.TestHelper.textViewHasErrorText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpenseCategoryTest {
    String categoryName = "Test name";
    String categoryEditedName = "Name of category";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createExpenseCategoryWithCorrectData() {
        createExpenseCategory(categoryName);
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createExpenseCategoryWithEmptyName() {
        createExpenseCategory(categoryName);

        onView(withId(R.id.expcat_fab_addExpCat)).perform(click());

        onView(withText(R.string.expcat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.expcat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_empty);
        onView(withId(R.id.expcat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createExpenseCategoryWithBadNameSyntax() {
        createExpenseCategory(categoryName);

        onView(withId(R.id.expcat_fab_addExpCat)).perform(click());

        onView(withText(R.string.expcat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.expcat_createform_text_name))
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.expcat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_syntax);
        onView(withId(R.id.expcat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createExpenseCategoryWithExistingName() {
        createExpenseCategory(categoryName);

        onView(withId(R.id.expcat_fab_addExpCat)).perform(click());

        onView(withText(R.string.expcat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.expcat_createform_text_name))
                .perform(typeText(categoryName), closeSoftKeyboard());
        onView(withId(R.id.expcat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_existing);
        onView(withId(R.id.expcat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editExpenseCategorysName() throws InterruptedException {
        createExpenseCategory(categoryName);

        onView(withId(R.id.expcat_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.expcat_rc_editicon)));

        Thread.sleep(2000);

        onView(withId(R.id.expcat_editform_text_name))
                .perform(clearText(), typeText(categoryEditedName), closeSoftKeyboard());
        onView(withId(R.id.expcat_editform_submit)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteExpenseCategory() throws InterruptedException {
        createExpenseCategory(categoryName);

        Thread.sleep(1000);

        onView(withId(R.id.expcat_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.expcat_rc_deleteicon)));

        Thread.sleep(1000);

        onView(withId(R.id.expcat_deleteform_submit)).perform(click());

        Thread.sleep(1000);

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayExpenseCategoriesList() {
        createExpenseCategory(categoryName);
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ExpenseCategoryModel> expenseCategories = new ArrayList<>(databaseHelper.getExpenseCategoryList());
        Assert.assertEquals(13, expenseCategories.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}