package pl.dchruscinski.pilnujgrosza;

import android.widget.TimePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static pl.dchruscinski.pilnujgrosza.TestHelper.correctPin;
import static pl.dchruscinski.pilnujgrosza.TestHelper.loginToProfile;
import static pl.dchruscinski.pilnujgrosza.TestHelper.setTime;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsTest {
    String newCurrency = "zl";
    String newNumberOfPeopleInHousehold = "3";
    int newHourOfNotificationsTime = 10;
    int newMinutesOfNotificationsTime = 45;
    String newProfileBalance = "333";

    @Rule
    public ActivityScenarioRule<Profile> activityRule = new ActivityScenarioRule<>(Profile.class);

    @Test
    public void changeCurrency() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withId(R.id.set_currency_text))
                .perform(clearText(), typeText(newCurrency), closeSoftKeyboard());
        onView(withId(R.id.set_save_button)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withText(newCurrency)).check(matches(isDisplayed()));
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void changeNumberOfPeopleInHousehold() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withId(R.id.set_people_in_household_text))
                .perform(clearText(), typeText(newNumberOfPeopleInHousehold), closeSoftKeyboard());
        onView(withId(R.id.set_save_button)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withText(newNumberOfPeopleInHousehold)).check(matches(isDisplayed()));
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void changeNotificationsTime() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withId(R.id.set_notifications_time_button)).perform(click());
        ViewInteraction notificationsTimePicker = onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        notificationsTimePicker.perform(setTime(newHourOfNotificationsTime, newMinutesOfNotificationsTime));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.set_save_button)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withText(newHourOfNotificationsTime + ":" + newMinutesOfNotificationsTime)).check(matches(isDisplayed()));
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }

    @Test
    public void resetBalance() {
        try {
            TestHelper.deleteProfile(correctPin);
        } catch (NullPointerException ignore) { }

        loginToProfile();
        onView(withId(R.id.mainmenu_settings_button)).perform(click());

        onView(withId(R.id.set_editBalance_button)).perform(click());
        onView(withId(R.id.set_edit_balance_edit_balance))
                .perform(clearText(), typeText(newProfileBalance), closeSoftKeyboard());
        onView(withId(R.id.set_edit_balance_submit)).perform(click());
        onView(withId(R.id.set_save_button)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_settings_button)).perform(click());
        onView(withId(R.id.set_editBalance_button)).perform(click());
        onView(withText(newProfileBalance)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.mainmenu_logout_button)).perform(click());
        TestHelper.deleteProfile(correctPin);
    }
}