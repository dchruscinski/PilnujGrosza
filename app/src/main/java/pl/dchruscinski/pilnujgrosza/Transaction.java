package pl.dchruscinski.pilnujgrosza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class Transaction extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<TransactionModel> transactionsList;
    FloatingActionButton addIncomeFAB;
    FloatingActionButton addExpenseFAB;
    DatabaseHelper databaseHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        adapter = new TransactionAdapter(getApplicationContext(), this, transactionsList);

        recyclerView = (RecyclerView) findViewById(R.id.transaction_list_rc);
        addIncomeFAB = (FloatingActionButton) findViewById(R.id.trans_fab_addIncome);
        addExpenseFAB = (FloatingActionButton) findViewById(R.id.trans_fab_addExpense);
        databaseHelper = new DatabaseHelper(this);

        if (getIntent().getExtras() != null && getIntent().getStringExtra("action").equals("incomeButton")) {
            displayTransactionsList();
            showCreateIncomeDialog();
        } else if (getIntent().getExtras() != null && getIntent().getStringExtra("action").equals("expenseButton")) {
            displayTransactionsList();
            showCreateExpenseDialog();
        } else {
            displayTransactionsList();
        }

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
                            Collections.sort(transactionsList, TransactionModel.transactionValueAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_value_desc:
                            Collections.sort(transactionsList, TransactionModel.transactionValueDescComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_asc:
                            Collections.sort(transactionsList, TransactionModel.transactionDateAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_desc:
                            Collections.sort(transactionsList, TransactionModel.transactionDateDescComparator);
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

    public void displayTransactionsList() {
        transactionsList = new ArrayList<>(databaseHelper.getTransactionsList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new TransactionAdapter(this, this, transactionsList);
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
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget, changeBudgetInitialAmount;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;

        context = getApplicationContext();
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

        dateTextView = dialog.findViewById(R.id.income_createform_text_date);
        value = dialog.findViewById(R.id.income_createform_text_value);
        currency = dialog.findViewById(R.id.income_createform_text_currency);
        description = dialog.findViewById(R.id.income_createform_text_desc);
        category = dialog.findViewById(R.id.income_createform_spin_cat);
        countToBudget = dialog.findViewById(R.id.income_createform_checkbox_budget);
        changeBudgetInitialAmount = dialog.findViewById(R.id.income_createform_checkbox_budgetInitialAmount);
        budget = dialog.findViewById(R.id.income_createform_spin_budget);
        budgetText = dialog.findViewById(R.id.income_createform_spin_budget_info);
        submitCreate = dialog.findViewById(R.id.income_createform_submit);

        currency.setText(databaseHelper.getCurrency(chosenProfileID));

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

        countToBudget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(countToBudget.isChecked()) {
                    budget.setVisibility(VISIBLE);
                    budgetText.setVisibility(VISIBLE);
                    changeBudgetInitialAmount.setVisibility(VISIBLE);
                } else {
                    budget.setVisibility(View.GONE);
                    budgetText.setVisibility(View.GONE);
                    changeBudgetInitialAmount.setVisibility(View.GONE);
                }
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
                    value.setError(getString(R.string.income_createincome_value_error_empty));
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else {
                    if (countToBudget.isChecked()) {
                        transactionModel.setTransBudID((int) budget.getSelectedItemId());
                    } else {
                        transactionModel.setTransBudID(NULL);
                    }
                    transactionModel.setTransCatID((int) category.getSelectedItemId());
                    transactionModel.setTransValue(intValue);
                    transactionModel.setTransDescription(description.getText().toString());
                    transactionModel.setTransDate(dateTextView.getText().toString());
                    if(changeBudgetInitialAmount.isChecked()) {
                        transactionModel.setTransChangeInitialBudget(1);
                        databaseHelper.addIncome(transactionModel, true);
                    } else {
                        transactionModel.setTransChangeInitialBudget(0);
                        databaseHelper.addIncome(transactionModel, false);
                    }

                    dialog.cancel();
                    displayTransactionsList();
                }
            }
        });

    }

    public void showCreateExpenseDialog() {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget;
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
        currency = (TextView) dialog.findViewById(R.id.expense_createform_text_currency);
        description = (EditText) dialog.findViewById(R.id.expense_createform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.expense_createform_spin_cat);
        countToBudget = (CheckBox) dialog.findViewById(R.id.expense_createform_checkbox_budget);
        budget = (Spinner) dialog.findViewById(R.id.expense_createform_spin_budget);
        budgetText = (TextView) dialog.findViewById(R.id.expense_createform_spin_budget_info);
        submitCreate = (Button) dialog.findViewById(R.id.expense_createform_submit);

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
                new DatePickerDialog(Transaction.this, date,
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
                    value.setError(getString(R.string.expense_createexpense_value_error_empty));
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else {
                    if (countToBudget.isChecked()) {
                        transactionModel.setTransBudID((int) budget.getSelectedItemId());
                    } else {
                        transactionModel.setTransBudID(NULL);
                    }
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