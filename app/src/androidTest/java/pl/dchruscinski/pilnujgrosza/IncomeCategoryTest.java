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
import static pl.dchruscinski.pilnujgrosza.TestHelper.createIncomeCategory;
import static pl.dchruscinski.pilnujgrosza.TestHelper.textViewHasErrorText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IncomeCategoryTest {
    String categoryName = "Test name";
    String categoryEditedName = "Name of category";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createIncomeCategoryWithCorrectData() {
        createIncomeCategory(categoryName);
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createIncomeCategoryWithEmptyName() {
        createIncomeCategory(categoryName);

        onView(withId(R.id.inccat_fab_addIncCat)).perform(click());

        onView(withText(R.string.inccat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.inccat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_empty);
        onView(withId(R.id.inccat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createIncomeCategoryWithBadNameSyntax() {
        createIncomeCategory(categoryName);

        onView(withId(R.id.inccat_fab_addIncCat)).perform(click());

        onView(withText(R.string.inccat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.inccat_createform_text_name))
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.inccat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_syntax);
        onView(withId(R.id.inccat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createIncomeCategoryWithExistingName() {
        createIncomeCategory(categoryName);

        onView(withId(R.id.inccat_fab_addIncCat)).perform(click());

        onView(withText(R.string.inccat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.inccat_createform_text_name))
                .perform(typeText(categoryName), closeSoftKeyboard());
        onView(withId(R.id.inccat_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.expcat_createexpcat_name_error_existing);
        onView(withId(R.id.inccat_createform_text_name)).check(matches(textViewHasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editIncomeCategorysName() throws InterruptedException {
        createIncomeCategory(categoryName);

        onView(withId(R.id.inccat_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.inccat_rc_editicon)));

        Thread.sleep(2000);

        onView(withId(R.id.inccat_editform_text_name))
                .perform(clearText(), typeText(categoryEditedName), closeSoftKeyboard());
        onView(withId(R.id.inccat_editform_submit)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteIncomeCategory() throws InterruptedException {
        createIncomeCategory(categoryName);

        Thread.sleep(2000);

        onView(withId(R.id.inccat_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.inccat_rc_deleteicon)));

        Thread.sleep(2000);

        onView(withId(R.id.inccat_deleteform_submit)).perform(click());

        Thread.sleep(2000);

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayIncomeCategoriesList() {
        createIncomeCategory(categoryName);
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<IncomeCategoryModel> incomeCategories = new ArrayList<>(databaseHelper.getIncomeCategoryList());
        Assert.assertEquals(6, incomeCategories.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}