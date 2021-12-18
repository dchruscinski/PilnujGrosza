package pl.dchruscinski.pilnujgrosza;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TestHelper {
    static String correctName = "Test";
    static String correctPin = "123456";
    static String correctInitialBalance = "999";
    static String correctCurrency = "EUR";
    static String correctHelperQuestion = "What was my first pet name?";
    static String correctHelperAnswer = "Doggy";
    static String initialAmount = "1000";
    static String description = "Test description";
    static String amount = "3";
    static String unit = "kg";
    static String value = "3.99";

    public static void createProfile() {
        onView(withId(R.id.profile_fab_addProfile)).perform(click());

        onView(withText(R.string.profile_createform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin_confirm))
                .perform(typeText(correctPin), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_initialBalance))
                .perform(typeText(correctInitialBalance), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_currency))
                .perform(typeText(correctCurrency), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_button_nextstage)).perform(click());

        onView(withText(R.string.profile_createform_info_helperquestion))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_helperquestion_question))
                .perform(typeText(correctHelperQuestion), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_helperquestion_answer))
                .perform(typeText(correctHelperAnswer), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_submitProfile)).perform(click());
    }

    public static void deleteProfile(String pin) {
        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(pin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withText(R.string.profile_editform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_editform_submit_deleteProfile)).perform(click());

        onView(withText(R.string.profile_loginform_predelete_info_delete))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_predelete_text_pin))
                .perform(typeText(pin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_predelete_button_login)).perform(click());
    }

    public static void loginToProfile() {
        try {
            deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_firstLine)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());
    }

    public static void createBudget(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
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
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(startYear, startMonth, startDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_toDate_info)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(endYear, endMonth, endDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.budget_createform_text_initialAmount))
                .perform(typeText(initialAmount), closeSoftKeyboard());
        onView(withId(R.id.budget_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.budget_createform_submit)).perform(click());
    }

    public static void createExpenseCategory(String categoryName) {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_expcat_button)).perform(click());
        onView(withId(R.id.expcat_fab_addExpCat)).perform(click());

        onView(withText(R.string.expcat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.expcat_createform_text_name))
                .perform(typeText(categoryName), closeSoftKeyboard());
        onView(withId(R.id.expcat_createform_submit)).perform(click());
    }

    public static void createIncomeCategory(String categoryName) {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_inccat_button)).perform(click());
        onView(withId(R.id.inccat_fab_addIncCat)).perform(click());

        onView(withText(R.string.inccat_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.inccat_createform_text_name))
                .perform(typeText(categoryName), closeSoftKeyboard());
        onView(withId(R.id.inccat_createform_submit)).perform(click());
    }

    public static void createScheduledPayment(int day, int month, int year) throws InterruptedException {
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
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.schpay_createform_text_value))
                .perform(typeText("1000"), closeSoftKeyboard());
        onView(withId(R.id.schpay_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.schpay_createform_submit)).perform(click());

        Thread.sleep(2000);

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int displayWidth = mDevice.getDisplayWidth();
        mDevice.click(displayWidth - 50, 80);
        mDevice.pressBack();
        mDevice.pressBack();
    }

    public static void createExpense(int day, int month, int year) {
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
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.expense_createform_text_value))
                .perform(typeText("1000"), closeSoftKeyboard());
        onView(withId(R.id.expense_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.expense_createform_submit)).perform(click());
    }

    public static void createIncome(int day, int month, int year) {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_finMgmt_button)).perform(click());
        onView(withId(R.id.finmgmt_transactionsList_button)).perform(click());
        onView(withId(R.id.trans_fab_addIncome)).perform(click());

        onView(withText(R.string.income_create_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.income_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.income_createform_text_value))
                .perform(typeText("1000"), closeSoftKeyboard());
        onView(withId(R.id.income_createform_text_desc))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.income_createform_submit)).perform(click());
    }

    public static void createReceiptFromCamera(int day, int month, int year) throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_receiptsList_button)).perform(click());
        onView(withId(R.id.receiptlist_fab_menu)).perform(click());
        onView(withId(R.id.receiptlist_fab_photo)).perform(click());

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int displayHeight = mDevice.getDisplayHeight();
        int displayWidth = mDevice.getDisplayWidth();
        Thread.sleep(2000);
        mDevice.click(displayWidth / 2, displayHeight - 10);
        Thread.sleep(2000);
        mDevice.click(displayWidth / 2, displayHeight - 60);
        Thread.sleep(2000);
        mDevice.click(displayWidth - 50, 80);

        onView(withText(R.string.receipt_create_menu_info))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.receipt_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.receipt_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.receipt_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.receipt_createform_submit)).perform(click());
    }

    public static void createReceiptFromFile(int day, int month, int year) throws InterruptedException {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_receiptsList_button)).perform(click());
        onView(withId(R.id.receiptlist_fab_menu)).perform(click());
        onView(withId(R.id.receiptlist_fab_fromFile)).perform(click());

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int displayHeight = mDevice.getDisplayHeight();
        int displayWidth = mDevice.getDisplayWidth();
        Thread.sleep(2000);
        mDevice.click(10, 400);
        Thread.sleep(2000);
        mDevice.click(10, 400);
        Thread.sleep(2000);
        mDevice.click(displayWidth - 50, 80);

        Thread.sleep(3000);

        onView(withText(R.string.receipt_create_menu_info))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.receipt_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.receipt_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.receipt_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.receipt_createform_submit)).perform(click());
    }

    public static void createShoppingList(int day, int month, int year) {
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
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.sholist_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.sholist_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.sholist_createform_submit)).perform(click());
    }

    public static void createShoppingContent(String correctName, String amount, String unit, String value) {
        TestHelper.createShoppingList(1, 1, 2023);

        onView(withId(R.id.sholist_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.sholist_rc_name)));
        onView(withId(R.id.shopping_fab_addShoCont)).perform(click());

        onView(withText(R.string.shocont_create_info_shocont))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.shocont_createform_text_name))
                .perform(clearText(), typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_amount))
                .perform(clearText(), typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_unit))
                .perform(clearText(), typeText(unit), closeSoftKeyboard());
        onView(withId(R.id.shocont_createform_text_value))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(withId(R.id.shocont_createform_submit)).perform(click());
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    public static ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker timePicker = (TimePicker) view;
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }

    public static Matcher<View> textViewHasErrorText(String errorText) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has error text: " + errorText);
            }

            @Override
            protected boolean matchesSafely(TextView view) {
                return errorText.matches((String) view.getError());
            }
        };
    }

}
