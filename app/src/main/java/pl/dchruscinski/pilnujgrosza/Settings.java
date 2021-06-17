package pl.dchruscinski.pilnujgrosza;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class Settings extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView notificationsTimeTextView;
    EditText currencyEditText, peopleInHouseholdEditText;
    SwitchCompat nightModeSwitch;
    Button setNotificationsTimeButton, saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        databaseHelper = new DatabaseHelper(this);

        notificationsTimeTextView = findViewById(R.id.set_notifications_time_info_2);
        currencyEditText = findViewById(R.id.set_currency_text);
        peopleInHouseholdEditText = findViewById(R.id.set_people_in_household_text);
        nightModeSwitch = findViewById(R.id.set_night_mode_switch);
        setNotificationsTimeButton = findViewById(R.id.set_notifications_time_button);
        saveSettingsButton = findViewById(R.id.set_save_button);

        currencyEditText.setText(databaseHelper.getCurrency(chosenProfileID));
        peopleInHouseholdEditText.setText(String.valueOf(databaseHelper.getPeopleInHousehold(chosenProfileID)));
        notificationsTimeTextView.setText(databaseHelper.getNotificationsTime(chosenProfileID)[0] + ":" + databaseHelper.getNotificationsTime(chosenProfileID)[1]);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightModeSwitch.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightModeSwitch.setChecked(false);
        }

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                }
            }
        });

        setNotificationsTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(Settings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10) {
                            notificationsTimeTextView.setText(hourOfDay + ":0" + minute);
                        } else {
                            notificationsTimeTextView.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, hour, minute, true);
                timePickerDialog.setTitle(getString(R.string.set_timepicker_dialog_title));
                timePickerDialog.show();
            }
        });

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel currencySettingsModel = new SettingsModel();
                SettingsModel peopleInHouseholdSettingsModel = new SettingsModel();
                SettingsModel notificationsTimeSettingsModel = new SettingsModel();

                if (currencyEditText.getText().toString().trim().isEmpty()) {
                    currencyEditText.setError("Podaj walutę.");
                } else if (currencyEditText.getText().toString().trim().matches("\\d{1,4}")) {
                    currencyEditText.setError("Wartość nie może być wyrażona w cyfrach.");
                } else if (peopleInHouseholdEditText.getText().toString().trim().isEmpty()) {
                    peopleInHouseholdEditText.setError("Podaj liczbę osób w gospodarstwie domowym.");
                } else if (!peopleInHouseholdEditText.getText().toString().trim().matches("\\d{1,2}")) {
                    peopleInHouseholdEditText.setError("Wartość nie może być wyrażona w cyfrach.");
                } else {
                    currencySettingsModel.setSetName("currency");
                    currencySettingsModel.setSetValue(currencyEditText.getText().toString());
                    databaseHelper.updateSettings(currencySettingsModel, "currency", chosenProfileID);

                    peopleInHouseholdSettingsModel.setSetName("peopleInHousehold");
                    peopleInHouseholdSettingsModel.setSetValue(peopleInHouseholdEditText.getText().toString());
                    databaseHelper.updateSettings(peopleInHouseholdSettingsModel, "peopleInHousehold", chosenProfileID);

                    notificationsTimeSettingsModel.setSetName("notificationsTime");
                    notificationsTimeSettingsModel.setSetValue(notificationsTimeTextView.getText().toString());
                    databaseHelper.updateSettings(notificationsTimeSettingsModel, "notificationsTime", chosenProfileID);

                    if (isDarkModeOn) {

                    }

                    Toast.makeText(getApplicationContext(), "Zapisano ustawienia.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}