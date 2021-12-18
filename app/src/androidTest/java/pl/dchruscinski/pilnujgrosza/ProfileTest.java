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
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctCurrency;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctHelperAnswer;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctInitialBalance;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctName;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctPin;
import static pl.dchruscinski.pilnujgrosza.TestHelper.createProfile;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileTest {
    String newProfileName = "TestTest";
    String newProfilePin = "654321";
    String incorrectProfilePinForMismatch = "654321";
    String incorrectProfilePinForInsufficientCharacters = "1234";
    String newHelperQuestion = "What is my age?";
    String incorrectHelperQuestion = "Test question";
    String newHelperAnswer = "Infinity";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void createProfileWithCorrectData() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withText(correctName)).check(matches(isDisplayed()));

        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void createProfileWithPinMismatch() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        onView(withId(R.id.profile_fab_addProfile)).perform(click());

        onView(withText(R.string.profile_createform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin_confirm))
                .perform(typeText(incorrectProfilePinForMismatch), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_initialBalance))
                .perform(typeText(correctInitialBalance), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_currency))
                .perform(typeText(correctCurrency), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_button_nextstage)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_pin_error_passwordMismatch);
        onView(withId(R.id.profile_createform_text_pin))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithEmptyPin() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        onView(withId(R.id.profile_fab_addProfile)).perform(click());

        onView(withText(R.string.profile_createform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_initialBalance))
                .perform(typeText(correctInitialBalance), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_currency))
                .perform(typeText(correctCurrency), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_button_nextstage)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_pin_error_empty);
        onView(withId(R.id.profile_createform_text_pin))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithPinWithInsufficientCharacters() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        onView(withId(R.id.profile_fab_addProfile)).perform(click());

        onView(withText(R.string.profile_createform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_name))
                .perform(typeText(correctName), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin))
                .perform(typeText(incorrectProfilePinForInsufficientCharacters), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin_confirm))
                .perform(typeText(incorrectProfilePinForInsufficientCharacters), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_initialBalance))
                .perform(typeText(correctInitialBalance), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_currency))
                .perform(typeText(correctCurrency), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_button_nextstage)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_pin_error_insufficientChars);
        onView(withId(R.id.profile_createform_text_pin)).check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithEmptyName() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        onView(withId(R.id.profile_fab_addProfile)).perform(click());

        onView(withText(R.string.profile_createform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_createform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_pin_confirm))
                .perform(typeText(correctPin), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_initialBalance))
                .perform(typeText(correctInitialBalance), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_currency))
                .perform(typeText(correctCurrency), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_button_nextstage)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_name_error_empty);
        onView(withId(R.id.profile_createform_text_name))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithExistingName() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

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

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_name_error_existingName);
        onView(withId(R.id.profile_createform_text_name))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithEmptyHelperQuestion() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

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

        onView(withId(R.id.profile_createform_text_helperquestion_answer))
                .perform(typeText(correctHelperAnswer), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_submitProfile)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_question_error_empty);
        onView(withId(R.id.profile_createform_text_helperquestion_question))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithBadSyntaxOfHelperQuestion() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

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
                .perform(typeText(incorrectHelperQuestion), closeSoftKeyboard());
        onView(withId(R.id.profile_createform_text_helperquestion_answer))
                .perform(typeText(correctHelperAnswer), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_submitProfile)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_question_error_syntax);
        onView(withId(R.id.profile_createform_text_helperquestion_question))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void createProfileWithEmptyHelperAnswer() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

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
                .perform(typeText(correctHelperAnswer), closeSoftKeyboard());

        onView(withId(R.id.profile_createform_submitProfile)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String errorText = context.getResources().getString(R.string.profile_createprofile_answer_error_empty);
        onView(withId(R.id.profile_createform_text_helperquestion_answer))
                .check(matches(hasErrorText(errorText)));

        Espresso.pressBack();
    }

    @Test
    public void editProfileName() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withText(R.string.profile_editform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_editform_text_name))
                .perform(clearText(), typeText(newProfileName), closeSoftKeyboard());
        onView(withId(R.id.profile_editform_submit_name)).perform(click());

        onView(withText(newProfileName)).check(matches(isDisplayed()));

        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void editProfilePin() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withText(R.string.profile_editform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_editform_text_newPIN))
                .perform(typeText(newProfilePin), closeSoftKeyboard());
        onView(withId(R.id.profile_editform_text_newPIN_confirm))
                .perform(typeText(newProfilePin), closeSoftKeyboard());
        onView(withId(R.id.profile_editform_submit_PIN)).perform(click());

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(newProfilePin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withText(R.string.profile_editform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        Espresso.pressBack();

        TestHelper.deleteProfile(newProfilePin);
    }

    @Test
    public void editProfileHelperQuestionAndAnswer() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withText(R.string.profile_editform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_editform_text_newHelperQuestion))
                .perform(clearText(), typeText(newHelperQuestion), closeSoftKeyboard());
        onView(withId(R.id.profile_editform_text_newHelperAnswer))
                .perform(typeText(newHelperAnswer), closeSoftKeyboard());
        onView(withId(R.id.profile_editform_submit_QandA)).perform(click());

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_editicon)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_link_remindPIN)).perform(click());

        onView(withText(R.string.profile_remindPIN_info_helperquestion))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText(newHelperQuestion)).check(matches(isDisplayed()));

        Espresso.pressBack();

        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void deleteProfile() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withText(correctName)).check(matches(isDisplayed()));

        TestHelper.deleteProfile(correctPin);

        onView(withText(correctName)).check(doesNotExist());
    }

    @Test
    public void createProfileAndLogin() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();

        onView(withText(correctName)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_list_rc)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, TestHelper.clickChildViewWithId(R.id.prof_rc_firstLine)));

        onView(withText(R.string.profile_loginform_info_login))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_loginform_text_pin))
                .perform(typeText(correctPin), closeSoftKeyboard());

        onView(withId(R.id.profile_loginform_button_login)).perform(click());

        onView(withId(R.id.mainmenu_logout_button)).perform(click());

        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void displayProfilesList() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        createProfile();
        DatabaseHelper databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        List<ProfileModel> profilesList = new ArrayList<>(databaseHelper.getProfileList());
        Assert.assertEquals(1, profilesList.size());

        TestHelper.deleteProfile(correctPin);
    }
}