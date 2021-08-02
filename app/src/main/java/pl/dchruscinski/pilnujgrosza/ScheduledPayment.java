package pl.dchruscinski.pilnujgrosza;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class ScheduledPayment extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ScheduledPaymentModel> scheduledPaymentList;
    FloatingActionButton addScheduledPaymentFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_payment_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new ScheduledPaymentAdapter(this, this, scheduledPaymentList);

        recyclerView = (RecyclerView) findViewById(R.id.schpay_list_rc);
        addScheduledPaymentFAB = (FloatingActionButton) findViewById(R.id.schpay_fab_addScheduledPayment);
        databaseHelper = new DatabaseHelper(this);

        displayScheduledPaymentList();

        addScheduledPaymentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateScheduledPaymentDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_sort_action_sort:
                View sortMenuItemView = findViewById(R.id.menu_sort_action_sort);
                PopupMenu popup = new PopupMenu(this, sortMenuItemView);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_sort_items_value_and_date, popup.getMenu());
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.menu_sort_action_sort_value_asc:
                            Collections.sort(scheduledPaymentList, ScheduledPaymentModel.scheduledPaymentValueAscComparator);
                            Toast.makeText(ScheduledPayment.this, "a ", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_value_desc:
                            Collections.sort(scheduledPaymentList, ScheduledPaymentModel.scheduledPaymentValueDescComparator);
                            Toast.makeText(ScheduledPayment.this, "b ", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_asc:
                            Collections.sort(scheduledPaymentList, ScheduledPaymentModel.scheduledPaymentDateAscComparator);
                            Toast.makeText(ScheduledPayment.this, "c ", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_desc:
                            Collections.sort(scheduledPaymentList, ScheduledPaymentModel.scheduledPaymentDateDescComparator);
                            Toast.makeText(ScheduledPayment.this, "d ", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            return true;
                    }

                    return super.onOptionsItemSelected(item);
                });
                popup.show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    public void displayScheduledPaymentList() {
        scheduledPaymentList = new ArrayList<>(databaseHelper.getScheduledPaymentsList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ScheduledPaymentAdapter(getApplicationContext(), this, scheduledPaymentList);
        recyclerView.setAdapter(adapter);
    }

    public void displayExpenseCategoriesSpinnerData(Spinner spinner) {
        Cursor expenseCategoriesCursorForSpinner = databaseHelper.getExpenseCategoriesForSpinner();

        String[] from = { "expcatName" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter expenseCategoryAdapterForSpinner = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                expenseCategoriesCursorForSpinner, from, to, 0);

        expenseCategoryAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(expenseCategoryAdapterForSpinner);
    }

    public void displayBudgetsSpinnerData(Spinner spinner) {
        Cursor budgetsCursorForSpinner = databaseHelper.getBudgetsForSpinner();

        String[] from = { "budDates" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter budgetAdapterForSpinner = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                budgetsCursorForSpinner, from, to, 0);

        budgetAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(budgetAdapterForSpinner);
    }

    public void showCreateScheduledPaymentDialog() {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.schpay_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.schpay_createform_text_date);
        value = (EditText) dialog.findViewById(R.id.schpay_createform_text_value);
        currency = (TextView) dialog.findViewById(R.id.schpay_createform_text_currency);
        description = (EditText) dialog.findViewById(R.id.schpay_createform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.schpay_createform_spin_cat);
        countToBudget = (CheckBox) dialog.findViewById(R.id.schpay_createform_checkbox_budget);
        budget = (Spinner) dialog.findViewById(R.id.schpay_createform_spin_budget);
        budgetText = (TextView) dialog.findViewById(R.id.schpay_createform_spin_budget_info);
        submitCreate = (Button) dialog.findViewById(R.id.schpay_createform_submit);

        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        dateTextView.setText(databaseHelper.getCurrentDate());
        displayExpenseCategoriesSpinnerData(category);
        displayBudgetsSpinnerData(budget);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateTextView.setText(dateFormat.format(calendar.getTime()));
            }
        };

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduledPayment.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        countToBudget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(countToBudget.isChecked()) {
                    budget.setVisibility(VISIBLE);
                    budgetText.setVisibility(VISIBLE);
                } else {
                    budget.setVisibility(View.GONE);
                    budgetText.setVisibility(View.GONE);
                }
        }
        });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ScheduledPaymentModel scheduledPaymentModel = new ScheduledPaymentModel();
                int intValue = 0;
                boolean isValueValid = false;

                if (!value.getText().toString().trim().isEmpty()) {
                    String stringValue = value.getText().toString().trim();
                    if (stringValue.contains(".")) {
                        if (stringValue.substring(stringValue.indexOf(".") + 1).length() > 2) {
                            isValueValid = false;
                        } else {
                            isValueValid = true;
                            BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else {
                        isValueValid = true;
                        BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                } else {
                    isValueValid = true;
                }

                try {
                    if (dateFormat.parse(dateTextView.getText().toString()).compareTo(dateFormat.parse(databaseHelper.getNextDayDate())) < 0) {
                        dateTextView.requestFocus();
                        dateTextView.setError("Płatność musi zostać zaplanowana w dniu późniejszym niż dziś.");
                    } else if (value.getText().toString().trim().isEmpty()) {
                        value.setError("Podaj wartość płatności.");
                    } else if (!isValueValid) {
                        value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                    } else {
                        if (countToBudget.isChecked()) {
                            scheduledPaymentModel.setSchpayBudID((int) budget.getSelectedItemId());
                        } else {
                            scheduledPaymentModel.setSchpayBudID(NULL);
                        }
                        scheduledPaymentModel.setSchpayCatID((int) category.getSelectedItemId());
                        scheduledPaymentModel.setSchpayValue(intValue);
                        scheduledPaymentModel.setSchpayDescription(description.getText().toString());
                        scheduledPaymentModel.setSchpayDate(dateTextView.getText().toString());
                        databaseHelper.addScheduledPayment(scheduledPaymentModel);

                        String[] notificationsTime = databaseHelper.getNotificationsTime(chosenProfileID);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notificationsTime[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(notificationsTime[1]));

                        /*
                        ContentValues values = new ContentValues();
                        values.put(Events.CALENDAR_ID, calID); // find how to get an calID!
                        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw");
                        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.schpay_calevent_title)
                                + " "
                                + value.getText().toString().trim()
                                + " "
                                + currency.getText().toString().trim()
                                + "; ID: "
                                + databaseHelper.getLastScheduledPayment());
                        values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString().trim());
                        values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
                        values.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis() + 5*60*1000);

                        ContentResolver cr = getContentResolver();
                        cr.insert(CalendarContract.Events.CONTENT_URI, values);
                        */

                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.CALENDAR_TIME_ZONE, "Europe/Warsaw")
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis() + 5*60*1000) // five minutes
                                .putExtra(CalendarContract.Events.TITLE,
                                getString(R.string.schpay_calevent_title)
                                        + " "
                                        + value.getText().toString().trim()
                                        + " "
                                        + currency.getText().toString().trim()
                                        + "; ID: "
                                        + databaseHelper.getLastScheduledPayment())

                                .putExtra(CalendarContract.Events.DESCRIPTION, description.getText().toString().trim());

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(ScheduledPayment.this, "Nie posiadasz aplikacji, która obsługuje wydarzenia.", Toast.LENGTH_SHORT).show();
                        }

                        dialog.cancel();
                        displayScheduledPaymentList();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}