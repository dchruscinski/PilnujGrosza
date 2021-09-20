package pl.dchruscinski.pilnujgrosza;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfilePosition;


public class Settings extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView notificationsTimeTextView;
    EditText currencyEditText, peopleInHouseholdEditText;
    SwitchCompat nightModeSwitch;
    Button setNotificationsTimeButton, setBalance, saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        notificationsTimeTextView = findViewById(R.id.set_notifications_time_info_2);
        currencyEditText = findViewById(R.id.set_currency_text);
        peopleInHouseholdEditText = findViewById(R.id.set_people_in_household_text);
        nightModeSwitch = findViewById(R.id.set_night_mode_switch);
        setNotificationsTimeButton = findViewById(R.id.set_notifications_time_button);
        setBalance = findViewById(R.id.set_editBalance_button);
        saveSettingsButton = findViewById(R.id.set_save_button);

        currencyEditText.setText(databaseHelper.getCurrency(chosenProfileID));
        peopleInHouseholdEditText.setText(String.valueOf(databaseHelper.getPeopleInHousehold(chosenProfileID)));
        notificationsTimeTextView.setText(databaseHelper.getNotificationsTime(chosenProfileID)[0] + ":" + databaseHelper.getNotificationsTime(chosenProfileID)[1]);

        boolean isDarkModeOn = databaseHelper.getTheme(chosenProfileID);
        if (isDarkModeOn) {
            nightModeSwitch.setChecked(true);
        } else {
            nightModeSwitch.setChecked(false);
        }

        /*
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightModeSwitch.setChecked(true);
        } else {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
        */

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

            setBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditBalanceDialog();
                }
            });

            saveSettingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsModel currencySettingsModel = new SettingsModel();
                    SettingsModel peopleInHouseholdSettingsModel = new SettingsModel();
                    SettingsModel notificationsTimeSettingsModel = new SettingsModel();
                    SettingsModel darkModeModel = new SettingsModel();

                    if (currencyEditText.getText().toString().trim().isEmpty()) {
                        currencyEditText.setError("Podaj walutę.");
                    } else if (currencyEditText.getText().toString().trim().matches("\\d+")) {
                        currencyEditText.setError("Wartość nie może być wyrażona w cyfrach.");
                    } else if (!currencyEditText.getText().toString().trim().matches("[a-zA-ZąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{1,4}")) {
                        currencyEditText.setError("Waluta musi składać się maksymalnie z czterech liter.");
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

                        if (nightModeSwitch.isChecked()) {
                            darkModeModel.setSetName("theme");
                            darkModeModel.setSetValue("dark");
                            databaseHelper.updateSettings(darkModeModel, "theme", chosenProfileID);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            darkModeModel.setSetName("theme");
                            darkModeModel.setSetValue("light");
                            databaseHelper.updateSettings(darkModeModel, "theme", chosenProfileID);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }

                        Toast.makeText(getApplicationContext(), "Zapisano ustawienia.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    public void showEditBalanceDialog() {
        final EditText balance;
        final TextView name, currency;
        Button submitEdit;
        ProfileModel profileModel;
        BigDecimal balanceAmount;

        final Dialog dialog = new Dialog(Settings.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_edit_balance_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        databaseHelper = new DatabaseHelper(this);
        profileModel = databaseHelper.getProfile(chosenProfileID);

        name = (TextView) dialog.findViewById(R.id.set_edit_balance_text_name);
        balance = (EditText) dialog.findViewById(R.id.set_edit_balance_edit_balance);
        currency = (TextView) dialog.findViewById(R.id.set_edit_balance_text_currency);
        submitEdit = (Button) dialog.findViewById(R.id.set_edit_balance_submit);

        name.setText(String.valueOf(profileModel.getProfName()));
        balanceAmount = BigDecimal.valueOf(profileModel.getProfBalance()).divide(BigDecimal.valueOf(100));
        balance.setText(balanceAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(balanceAmount) : new DecimalFormat("#.##").format(balanceAmount));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(getApplicationContext());
                boolean isBalanceValid = false;
                int profBalance = 0;

                if (!balance.getText().toString().trim().isEmpty()) {
                    String stringBalance = balance.getText().toString().trim();
                    if (stringBalance.contains(".")) {
                        if (stringBalance.substring(stringBalance.indexOf(".") + 1).length() > 2) {
                            isBalanceValid = false;
                        } else {
                            isBalanceValid = true;
                            BigDecimal bigDecimalBalance = new BigDecimal(stringBalance.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            profBalance = (bigDecimalBalance.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else {
                        isBalanceValid = true;
                        BigDecimal bigDecimalBalance = new BigDecimal(stringBalance.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        profBalance = (bigDecimalBalance.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                }

                if (balance.getText().toString().trim().isEmpty()) {
                    balance.setError("Podaj nową wartość salda.");
                } else if (!isBalanceValid) {
                    balance.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else {
                    databaseHelper.updateProfileBalance(chosenProfileID, profBalance);

                    dialog.cancel();
                }
            }
        });
    }

    }