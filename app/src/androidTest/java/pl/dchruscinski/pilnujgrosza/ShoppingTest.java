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
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShoppingTest {
    int transactionDateDay = 1;
    int transactionDateMonth = 1;
    int transactionDateYear = 2023;
    String name = "Test";
    String description = "Test description";
    String amount = "3";
    String unit = "kg";
    String value = "3.99";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createShoppingListWithCorrectData() {
        TestHelper.createShoppingList(1, 1, 2023);
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingListWithEmptyName() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_shoppingLists_button)).perform(click());
        onView(withId(R.id.sholist_fab_addShoList)).perform(click());

        onView(withText(R.string.sholist_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.sholist_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.sholist_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.sholist_createsholist_name_error_empty);
        onView(withId(R.id.sholist_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingListWithNameBadSyntax() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_shoppingLists_button)).perform(click());
        onView(withId(R.id.sholist_fab_addShoList)).perform(click());

        onView(withText(R.string.sholist_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.sholist_createform_text_name))
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.sholist_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.sholist_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.sholist_createsholist_name_error_syntax);
        onView(withId(R.id.sholist_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingListsName() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_button_edit)).perform(click());

        onView(withId(R.id.sholist_editform_text_name))
                .perform(clearText(), typeText("Name"), closeSoftKeyboard());
        onView(withId(R.id.sholist_editform_submit)).perform(click());
        onView(withText("Name")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingListsDate() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_button_edit)).perform(click());

        onView(withId(R.id.sholist_editform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.sholist_editform_submit)).perform(click());
        onView(withText("2023-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingListsDescription() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_button_edit)).perform(click());

        onView(withId(R.id.sholist_editform_text_description))
                .perform(clearText(), typeText("Edited description"), closeSoftKeyboard());
        onView(withId(R.id.sholist_editform_submit)).perform(click());
        onView(withText("Edited description")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteShoppingList() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_button_delete)).perform(click());
        onView(withId(R.id.sholist_deleteform_submit)).perform(click());
        onView(withText("2023-01-01")).check(doesNotExist());
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayShoppingInstancesList() {
        TestHelper.createShoppingList(1, 1, 2023);

        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ShoppingModel> shoppingLists = new ArrayList<>(databaseHelper.getShoppingList());
        Assert.assertEquals(1, shoppingLists.size());

        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithCorrectData() {
        TestHelper.createShoppingContent(name, amount, unit, value);

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithEmptyName() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_name_error_empty);
        onView(withId(R.id.shocont_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithBadSyntaxName() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_name_error_syntax);
        onView(withId(R.id.shocont_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithExistingName() {
        TestHelper.createShoppingContent(name, amount, unit, value);
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText(name), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_name_error_existing);
        onView(withId(R.id.shocont_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithEmptyAmount() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText(name), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_amount_error_empty);
        onView(withId(R.id.shocont_createform_text_amount)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithEmptyUnit() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText(name), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_unit_error_empty);
        onView(withId(R.id.shocont_createform_text_unit)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createShoppingContentWithBadSyntaxUnit() {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText(name), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.shocont_createshocont_unit_error_syntax);
        onView(withId(R.id.shocont_createform_text_unit)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingContentsName() throws InterruptedException {
        TestHelper.createShoppingContent(name, amount, unit, value);

        onView(withId(R.id.shocont_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.shocont_rc_name)));

        Thread.sleep(1000);

        onView(withId(R.id.shocont_editform_text_name))
                .perform(clearText(), typeText("Edited name"), closeSoftKeyboard());
        onView(withId(R.id.shocont_editform_submit)).perform(click());
        onView(withText("Edited name")).check(matches(isDisplayed()));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingContentsAmount() throws InterruptedException {
        TestHelper.createShoppingContent(name, amount, unit, value);

        onView(withId(R.id.shocont_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.shocont_rc_name)));

        Thread.sleep(1000);

        onView(withId(R.id.shocont_editform_text_amount))
                .perform(clearText(), typeText("11"), closeSoftKeyboard());
        onView(withId(R.id.shocont_editform_submit)).perform(click());
        onView(withText("11")).check(matches(isDisplayed()));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingContentsUnit() throws InterruptedException {
        TestHelper.createShoppingContent(name, amount, unit, value);

        onView(withId(R.id.shocont_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.shocont_rc_name)));

        Thread.sleep(1000);

        onView(withId(R.id.shocont_editform_text_unit))
                .perform(clearText(), typeText("szt."), closeSoftKeyboard());
        onView(withId(R.id.shocont_editform_submit)).perform(click());
        onView(withText("szt.")).check(matches(isDisplayed()));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editShoppingContentsValue() throws InterruptedException {
        TestHelper.createShoppingContent(name, amount, unit, value);

        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText("Second test"), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());

        onView(withId(R.id.shocont_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.shocont_rc_name)));

        Thread.sleep(1000);

        onView(withId(R.id.shocont_editform_text_value))
                .perform(clearText(), typeText("9.99"), closeSoftKeyboard());
        onView(withId(R.id.shocont_editform_submit)).perform(click());
        onView(withText("9,99")).check(matches(isDisplayed()));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteShoppingContent() throws InterruptedException {
        TestHelper.createShoppingContent("Test content", amount, unit, value);

        onView(withId(R.id.shocont_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.shocont_rc_deleteicon)));

        Thread.sleep(1000);

        onView(withId(R.id.shocont_deleteform_submit)).perform(click());
        onView(withText("Test content")).check(doesNotExist());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayShoppingContentsList() {
        TestHelper.createShoppingContent(name, amount, unit, value);

        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ShoppingContentModel> shoppingContentsList = new ArrayList<>(databaseHelper.getShoppingContentList());
        Assert.assertEquals(1, shoppingContentsList.size());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}