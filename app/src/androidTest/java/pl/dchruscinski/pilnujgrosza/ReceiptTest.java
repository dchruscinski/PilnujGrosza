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
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

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
import static pl.dchruscinski.pilnujgrosza.TestHelper.createIncome;
import static pl.dchruscinski.pilnujgrosza.TestHelper.createReceiptFromCamera;
import static pl.dchruscinski.pilnujgrosza.TestHelper.createReceiptFromFile;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReceiptTest {
    int transactionDateDay = 1;
    int transactionDateMonth = 1;
    int transactionDateYear = 2023;
    String name = "Test name";
    String description = "Test description";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createReceiptFromCameraWithCorrectData() throws InterruptedException {
        createReceiptFromCamera(1, 1, 2023);
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createReceiptFromFileWithCorrectData() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void checkOcrFunctionality() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));
        onView(withId(R.id.receipt_button_edit)).perform(click());
        onView(withId(R.id.receipt_edit_menu_editData)).perform(click());

        String OcrData = "? KASZTELAN, SORT. F 1%*53,993,\n" +
                ":SO MIEL.W.-WOŁ. - F 1*68,998,\n" +
                "10 SEREK WANIL, F 19053\n" +
                "IDORY MIĘSISTE F\n" +
                "\n" +
                "0,688 * 9,99 6,\n" +
                "MUS PASTA WARZ., P. .. 3 95\n" +
                "ETTE SEREK F 1x 4194,\n" +
                "I ODŚW.ŻEL DO RĄK - X 2 * 3,49 6,\n" +
                "SI REGULAR 4 sipec\n" +
                "STO FRAN. SUROWE Fr l* 2,998\n" +
                "NER_SAŁATKA Fu * 6,59 6,\n" +
                "URYDZA DAWTONA P* 2792,\n" +
                "CZKO KOKOSOWE I * *+5995'\n" +
                "IDORY W PUSZCE P * 1,99 1*\n" +
                "POM.BAZ . 720ML Fo 1 * 389 31i\n" +
                "POMARAŃCZOWYPET — F 6 * 2,49 17 <\n" +
                "JABŁKOWY NFC 3L p . 11,99 11 .*\n" +
                "ed, opod. PTU A 65, :\n" +
                "[a A 23,00% 2i\n" +
                "7ed. opod, PTU B 5Ś\n" +
                "'a B 08,00% 0,\n" +
                "ed. ogod. PTU C 91,i\n" +
                "'a G 05,00% 4'";

        onView(withText(OcrData)).check(matches(isDisplayed()));

        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createReceiptWithEmptyName() throws InterruptedException {
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

        onView(withId(R.id.receipt_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, transactionDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.receipt_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.receipt_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.receipt_createreceipt_name_error_empty);
        onView(withId(R.id.receipt_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createReceiptWithNameBadSyntax() throws InterruptedException {
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
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.receipt_createform_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, transactionDateDay));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.receipt_createform_text_description))
                .perform(typeText(description), closeSoftKeyboard());

        onView(withId(R.id.receipt_createform_submit)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.receipt_createreceipt_name_error_syntax);
        onView(withId(R.id.receipt_createform_text_name)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editReceiptsName() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));
        onView(withId(R.id.receipt_button_edit)).perform(click());
        onView(withId(R.id.receipt_edit_menu_editAttributes)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.receipt_editAttributes_form_text_name))
                .perform(clearText(), typeText("Name"), closeSoftKeyboard());
        onView(withId(R.id.receipt_editAttributes_form_submit)).perform(click());
        onView(withText("Name")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editReceiptsDate() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));
        onView(withId(R.id.receipt_button_edit)).perform(click());
        onView(withId(R.id.receipt_edit_menu_editAttributes)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.receipt_editAttributes_form_text_date)).perform(doubleClick());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(transactionDateYear, transactionDateMonth, 8));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.receipt_editAttributes_form_submit)).perform(click());
        onView(withText("2023-01-08")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editReceiptsDescription() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));
        onView(withId(R.id.receipt_button_edit)).perform(click());
        onView(withId(R.id.receipt_edit_menu_editAttributes)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.receipt_editAttributes_form_text_description))
                .perform(clearText(), typeText("Edited description"), closeSoftKeyboard());
        onView(withId(R.id.receipt_editAttributes_form_submit)).perform(click());
        onView(withText("Edited description")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editReceiptsData() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));
        onView(withId(R.id.receipt_button_edit)).perform(click());
        onView(withId(R.id.receipt_edit_menu_editData)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.receipt_editData_form_text_data))
                .perform(clearText(), typeText("Edited data"), closeSoftKeyboard());
        onView(withId(R.id.receipt_editData_form_submit)).perform(click());
        onView(withText("Edited data")).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteReceipt() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(1000);

        onView(withId(R.id.receipt_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.receipt_rc_name)));

        Thread.sleep(1000);

        onView(withId(R.id.receipt_button_delete)).perform(click());
        onView(withId(R.id.receipt_deleteform_submit)).perform(click());
        onView(withText("2023-01-01")).check(doesNotExist());
        Espresso.pressBack();
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayReceiptsList() throws InterruptedException {
        createReceiptFromFile(1, 1, 2023);

        Thread.sleep(3000);

        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ReceiptModel> receiptsList = new ArrayList<>(databaseHelper.getReceiptList());
        Assert.assertEquals(1, receiptsList.size());

        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}