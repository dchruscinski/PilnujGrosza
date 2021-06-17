package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class Statistics extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    RadioButton overallButton, budgetButton, monthlyButton, periodButton;
    TextView monthlyDateTextView, startDateTextView, startDateTextViewInfo, endDateTextView, endDateTextViewInfo;
    Spinner budget;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        Calendar startDateCalendar = Calendar.getInstance();
        Calendar endDateCalendar = Calendar.getInstance();
        Calendar monthCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
        databaseHelper = new DatabaseHelper(this);

        overallButton = findViewById(R.id.stat_overall_rb);
        budgetButton = findViewById(R.id.stat_budget_rb);
        monthlyButton = findViewById(R.id.stat_monthly_rb);
        periodButton = findViewById(R.id.stat_period_rb);

        budget = findViewById(R.id.stat_spin_budget);

        monthlyDateTextView = findViewById(R.id.stat_text_monthly);
        startDateTextView = findViewById(R.id.stat_text_startDate);
        startDateTextViewInfo = findViewById(R.id.stat_startDate_info);
        endDateTextView = findViewById(R.id.stat_text_endDate);
        endDateTextViewInfo = findViewById(R.id.stat_endDate_info);

        submit = findViewById(R.id.stat_submit);

        monthlyDateTextView.setText(databaseHelper.getCurrentMonthYearDate());
        startDateTextView.setText(databaseHelper.getCurrentDate());
        endDateTextView.setText(databaseHelper.getNextDayDate());

        displayBudgetsSpinnerData(budget);

        monthlyButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(monthlyButton.isChecked()) {
                    monthlyDateTextView.setVisibility(VISIBLE);
                } else {
                    monthlyDateTextView.setVisibility(View.GONE);
                }
            }
        });

        budgetButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(budgetButton.isChecked()) {
                    budget.setVisibility(VISIBLE);
                } else {
                    budget.setVisibility(View.GONE);
                }
            }
        });

        periodButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(periodButton.isChecked()) {
                    startDateTextView.setVisibility(VISIBLE);
                    startDateTextViewInfo.setVisibility(VISIBLE);
                    endDateTextView.setVisibility(VISIBLE);
                    endDateTextViewInfo.setVisibility(VISIBLE);
                } else {
                    startDateTextView.setVisibility(View.GONE);
                    startDateTextViewInfo.setVisibility(View.GONE);
                    endDateTextView.setVisibility(View.GONE);
                    endDateTextViewInfo.setVisibility(View.GONE);
                }
            }
        });

        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateTextView.setText(yearMonthDayFormat.format(startDateCalendar.getTime()));
            }
        };

        DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateTextView.setText(yearMonthDayFormat.format(endDateCalendar.getTime()));
            }
        };

        monthlyDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog(monthCalendar);

                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        monthlyDateTextView.setText(yearMonthFormat.format(monthCalendar.getTime()));
                    }
                });

                pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Statistics.this, startDate,
                        startDateCalendar.get(Calendar.YEAR),
                        startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Statistics.this, endDate,
                        endDateCalendar.get(Calendar.YEAR),
                        endDateCalendar.get(Calendar.MONTH),
                        endDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (overallButton.isChecked()) {
                    if (!databaseHelper.doesUserHaveAnyTransaction(chosenProfileID)) {
                        submit.requestFocus();
                        submit.setError("Użytkownik nie dokonał jeszcze żadnej transakcji.");
                    } else {
                        Intent intent = new Intent(v.getContext(), StatisticsPresentation.class)
                                .putExtra("type","overall");
                        startActivity(intent);
                    }
                } else if (budgetButton.isChecked()) {
                    if (!databaseHelper.doesUserHaveAnyTransactionInBudget(chosenProfileID, (int) budget.getSelectedItemId())) {
                        submit.requestFocus();
                        submit.setError("Użytkownik nie dokonał jeszcze żadnej transakcji.");
                    } else {
                        Intent intent = new Intent(v.getContext(), StatisticsPresentation.class)
                                .putExtra("type","budget")
                                .putExtra("budID", (int) budget.getSelectedItemId());
                        startActivity(intent);
                    }
                } else if (monthlyButton.isChecked()) {
                    if (!databaseHelper.doesUserHaveAnyTransactionInMonth(chosenProfileID, monthlyDateTextView.getText().toString())) {
                        submit.requestFocus();
                        submit.setError("Użytkownik nie dokonał jeszcze żadnej transakcji.");
                    } else {
                        Intent intent = new Intent(v.getContext(), StatisticsPresentation.class)
                                .putExtra("type","monthly")
                                .putExtra("month", monthlyDateTextView.getText().toString());
                        startActivity(intent);
                    }
                } else {
                    if (!databaseHelper.doesUserHaveAnyTransactionInPeriod(chosenProfileID, startDateTextView.getText().toString(), endDateTextView.getText().toString())) {
                        submit.requestFocus();
                        submit.setError("Użytkownik nie dokonał jeszcze żadnej transakcji.");
                    } else {
                        Intent intent = new Intent(v.getContext(), StatisticsPresentation.class)
                                .putExtra("type","period")
                                .putExtra("startDate", startDateTextView.getText().toString())
                                .putExtra("endDate", endDateTextView.getText().toString());
                        startActivity(intent);
                    }
                }
            }
        });

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

}