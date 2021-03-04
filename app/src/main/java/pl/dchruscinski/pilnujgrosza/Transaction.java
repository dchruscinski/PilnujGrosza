package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Transaction extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionsList;
    private List<String> incomeCategoriesListForSpinner, expenseCategoriesListForSpinner, budgetsListForSpinner;
    FloatingActionButton addIncomeFAB;
    FloatingActionButton addExpenseFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);

        recyclerView = (RecyclerView) findViewById(R.id.expense_list_rc);
        addIncomeFAB = (FloatingActionButton) findViewById(R.id.trans_fab_addIncome);
        addExpenseFAB = (FloatingActionButton) findViewById(R.id.trans_fab_addExpense);
        databaseHelper = new DatabaseHelper(this);
        displayTransactionsList();

        addIncomeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateIncomeDialog();
            }
        });

        addExpenseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateExpenseDialog();
            }
        });
    }

    public void displayTransactionsList() {
        transactionsList = new ArrayList<>(databaseHelper.getTransactionsList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(), this, transactionsList);
        recyclerView.setAdapter(adapter);
    }

    public void displayIncomeCategoriesSpinnerData(Spinner spinner) {
        Cursor incomeCategoriesCursorForSpinner = databaseHelper.getIncomeCategoriesForSpinner();

        String[] from = { "inccatName" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter incomeCategoryAdapterForSpinner = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                incomeCategoriesCursorForSpinner, from, to, 0);

        incomeCategoryAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(incomeCategoryAdapterForSpinner);
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

    public void showCreateIncomeDialog() {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;
        int selectedBudgetID, selectedCategoryID;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.income_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.income_createform_text_date);
        value = (EditText) dialog.findViewById(R.id.income_createform_text_value);
        description = (EditText) dialog.findViewById(R.id.income_createform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.income_createform_spin_cat);
        budget = (Spinner) dialog.findViewById(R.id.income_createform_spin_budget);
        submitCreate = (Button) dialog.findViewById(R.id.income_createform_submit);

        dateTextView.setText(databaseHelper.getCurrentDate());
        displayIncomeCategoriesSpinnerData(category);
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
                                            new DatePickerDialog(Transaction.this, date,
                                                    calendar.get(Calendar.YEAR),
                                                    calendar.get(Calendar.MONTH),
                                                    calendar.get(Calendar.DAY_OF_MONTH))
                                                    .show();
                                        }
                                    });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                TransactionModel transactionModel = new TransactionModel();
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

                if (value.getText().toString().trim().isEmpty()) {
                    value.setError("Podaj wartość przychodu.");
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else {
                    transactionModel.setTransBudID((int) budget.getSelectedItemId());
                    transactionModel.setTransCatID((int) category.getSelectedItemId());
                    transactionModel.setTransValue(intValue);
                    transactionModel.setTransDescription(description.getText().toString());
                    transactionModel.setTransDate(dateTextView.getText().toString());
                    databaseHelper.addIncome(transactionModel);

                    dialog.cancel();
                    displayTransactionsList();
                }
            }
        });

    }

    public void showCreateExpenseDialog() {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expense_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.expense_createform_text_date);
        value = (EditText) dialog.findViewById(R.id.expense_createform_text_value);
        description = (EditText) dialog.findViewById(R.id.expense_createform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.expense_createform_spin_cat);
        budget = (Spinner) dialog.findViewById(R.id.expense_createform_spin_budget);
        submitCreate = (Button) dialog.findViewById(R.id.expense_createform_submit);

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
                new DatePickerDialog(Transaction.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                TransactionModel transactionModel = new TransactionModel();
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

                if (value.getText().toString().trim().isEmpty()) {
                    value.setError("Podaj wartość wydatku.");
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else {
                    transactionModel.setTransBudID((int) budget.getSelectedItemId());
                    transactionModel.setTransCatID((int) category.getSelectedItemId());
                    transactionModel.setTransValue(intValue);
                    transactionModel.setTransDescription(description.getText().toString());
                    transactionModel.setTransDate(dateTextView.getText().toString());
                    databaseHelper.addExpense(transactionModel);

                    dialog.cancel();
                    displayTransactionsList();
                }
            }
        });

    }
}