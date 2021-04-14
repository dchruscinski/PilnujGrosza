package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class Budget extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<BudgetModel> budgetsList;
    FloatingActionButton addBudgetFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.budget_list_rc);
        addBudgetFAB = (FloatingActionButton) findViewById(R.id.budget_fab_addBudget);
        databaseHelper = new DatabaseHelper(this);
        displayBudgetsList();

        addBudgetFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog();
            }
        });
    }

    //display notes list
    public void displayBudgetsList() {
        budgetsList = new ArrayList<>(databaseHelper.getBudgetsList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        BudgetAdapter adapter = new BudgetAdapter(getApplicationContext(), this, budgetsList);
        recyclerView.setAdapter(adapter);
    }

    public void showCreateDialog() {
        final EditText initialAmount, description;
        final TextView startDateTextView, endDateTextView;
        Button submitCreate;

        final Calendar startDateCalendar = Calendar.getInstance();
        final Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.MONTH, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.budget_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startDateTextView = (TextView) dialog.findViewById(R.id.budget_createform_text_startDate);
        endDateTextView = (TextView) dialog.findViewById(R.id.budget_createform_text_endDate);
        initialAmount = (EditText) dialog.findViewById(R.id.budget_createform_text_initialAmount);
        description = (EditText) dialog.findViewById(R.id.budget_createform_text_desc);
        submitCreate = (Button) dialog.findViewById(R.id.budget_createform_submit);

        startDateTextView.setText(databaseHelper.getCurrentDate());
        endDateTextView.setText(databaseHelper.getNextMonthDate());

        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateTextView.setText(dateFormat.format(startDateCalendar.getTime()));
            }
        };

        DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateTextView.setText(dateFormat.format(endDateCalendar.getTime()));
            }
        };

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Budget.this, startDate,
                        startDateCalendar.get(Calendar.YEAR),
                        startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Budget.this, endDate,
                        endDateCalendar.get(Calendar.YEAR),
                        endDateCalendar.get(Calendar.MONTH),
                        endDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                BudgetModel budgetModel = new BudgetModel();
                int intAmount = 0;
                boolean isAmountValid = false;

                if (!initialAmount.getText().toString().trim().isEmpty()) {
                    String stringAmount = initialAmount.getText().toString().trim();
                    if (stringAmount.contains(".")) {
                        if (stringAmount.substring(stringAmount.indexOf(".") + 1).length() > 2) {
                            isAmountValid = false;
                        } else {
                            isAmountValid = true;
                            BigDecimal bigDecimalAmount = new BigDecimal(stringAmount.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intAmount = (bigDecimalAmount.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else {
                        isAmountValid = true;
                        BigDecimal bigDecimalAmount = new BigDecimal(stringAmount.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        intAmount = (bigDecimalAmount.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                } else {
                    isAmountValid = true;
                }

                if (initialAmount.getText().toString().trim().isEmpty()) {
                    initialAmount.setError("Podaj wysokość budżetu.");
                } else {
                    try {
                        if (!databaseHelper.checkStartAndEndDateInDialog(startDateTextView.getText().toString(), endDateTextView.getText().toString())) {
                            startDateTextView.requestFocus();
                            startDateTextView.setError("Data rozpoczęcia okresu rozliczeniowego nie może być późniejsza niż jego zakończenie.");
                        } else if (!databaseHelper.checkStartDateInCreateDialog(chosenProfileID, startDateTextView.getText().toString())) {
                            startDateTextView.requestFocus();
                            startDateTextView.setError("Okres rozliczeniowy nie może rozpoczynać się przed zakończeniem innego okresu rozliczeniowego.");
                        } else if (!databaseHelper.checkEndDateInCreateDialog(chosenProfileID, endDateTextView.getText().toString())) {
                            startDateTextView.requestFocus();
                            endDateTextView.setError("Okres rozliczeniowy nie może zakończyć się w trakcie innego okresu rozliczeniowego.");
                        } else if (!databaseHelper.checkDatesBetweenStartAndEndDatesInCreateDialog(chosenProfileID, startDateTextView.getText().toString(), endDateTextView.getText().toString())) {
                            endDateTextView.setError("Istnieje już okres rozliczeniowy pomiędzy podaną datą rozpoczęcia i zakończenia okresu rozliczeniowego.");
                        } else if (!isAmountValid) {
                            initialAmount.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                        } else {
                            budgetModel.setBudInitialAmount(intAmount);
                            budgetModel.setBudDescription(description.getText().toString());
                            budgetModel.setBudStartDate(startDateTextView.getText().toString());
                            budgetModel.setBudEndDate(endDateTextView.getText().toString());
                            databaseHelper.addBudget(budgetModel);
                            System.out.println("ProfileID: " + chosenProfileID);

                            dialog.cancel();
                            displayBudgetsList();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}