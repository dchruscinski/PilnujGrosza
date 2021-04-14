package pl.dchruscinski.pilnujgrosza;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class BudgetTransaction extends AppCompatActivity {
    public TextView budgetStartDate, budgetEndDate, amount, initialAmount, currency;
    Button editBudget, deleteBudget;
    BigDecimal budgetsAmount, budgetsInitialAmount;
    private ArrayList<BudgetModel> budgetsList;
    private ArrayList<TransactionModel> transactionsList;
    private RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    private int budID, position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budtrans_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        budID = getIntent().getExtras().getInt("budID");
        position = getIntent().getExtras().getInt("position");

        recyclerView = (RecyclerView) findViewById(R.id.budtrans_list_rc);

        budgetStartDate = (TextView) findViewById(R.id.budtrans_rc_startDate);
        budgetEndDate = (TextView) findViewById(R.id.budtrans_rc_endDate);
        amount = (TextView) findViewById(R.id.budtrans_rc_amount);
        initialAmount = (TextView) findViewById(R.id.budtrans_rc_initialAmount);
        currency = (TextView) findViewById(R.id.budtrans_rc_currency);
        editBudget = (Button) findViewById(R.id.budtrans_button_edit);
        deleteBudget = (Button) findViewById(R.id.budtrans_button_delete);

        budgetStartDate.setText(String.valueOf(databaseHelper.getBudget(budID).getBudStartDate()));
        budgetEndDate.setText(databaseHelper.getBudget(budID).getBudEndDate());

        budgetsAmount = BigDecimal.valueOf(databaseHelper.getBudget(budID).getBudAmount()).divide(BigDecimal.valueOf(100));
        budgetsInitialAmount = BigDecimal.valueOf(databaseHelper.getBudget(budID).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        amount.setText(budgetsAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsAmount) : new DecimalFormat("#.##").format(budgetsAmount));
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("#.##").format(budgetsInitialAmount));

        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        displayBudgetTransactionsList();

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(position);
            }
        });

        deleteBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });
    }

    public void displayBudgetTransactionsList() {
        transactionsList = new ArrayList<>(databaseHelper.getTransactionsListForBudget(budID));
        budgetsList = new ArrayList<>(databaseHelper.getBudgetsList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(), this, transactionsList);
        recyclerView.setAdapter(adapter);
    }

    public void showEditDialog(final int budID) {
        final EditText initialAmount, description;
        final TextView startDateTextView, endDateTextView, currency;
        Button submitEdit;

        BigDecimal budgetsInitialAmount;
        budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        final Calendar startDateCalendar = Calendar.getInstance();
        final Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.MONTH, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        final Dialog dialog = new Dialog(BudgetTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.budget_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startDateTextView = (TextView) dialog.findViewById(R.id.budget_editform_text_startDate);
        endDateTextView = (TextView) dialog.findViewById(R.id.budget_editform_text_endDate);
        initialAmount = (EditText) dialog.findViewById(R.id.budget_editform_text_initialAmount);
        currency = (TextView) dialog.findViewById(R.id.budget_editform_text_currency);
        description = (EditText) dialog.findViewById(R.id.budget_editform_text_desc);
        submitEdit = (Button) dialog.findViewById(R.id.budget_editform_submit);

        startDateTextView.setText(budgetsList.get(position).getBudStartDate());
        endDateTextView.setText(budgetsList.get(position).getBudEndDate());
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("#.##").format(budgetsInitialAmount));
        description.setText(budgetsList.get(position).getBudDescription());
        currency.setText(databaseHelper.getCurrency(chosenProfileID));

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
                new DatePickerDialog(v.getRootView().getContext(), startDate,
                        startDateCalendar.get(Calendar.YEAR),
                        startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getRootView().getContext(), endDate,
                        endDateCalendar.get(Calendar.YEAR),
                        endDateCalendar.get(Calendar.MONTH),
                        endDateCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(getApplicationContext());
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
                    initialAmount.setError("Podaj nową wysokość budżetu.");
                } else {
                    try {
                        if (!databaseHelper.checkStartAndEndDateInDialog(startDateTextView.getText().toString(), endDateTextView.getText().toString())) {
                            startDateTextView.setError("Data rozpoczęcia okresu rozliczeniowego nie może być późniejsza niż jego zakończenie.");
                        } else if (!databaseHelper.checkStartDateInEditDialog(chosenProfileID, budgetsList.get(position).getBudID(), startDateTextView.getText().toString())) {
                            startDateTextView.setError("Okres rozliczeniowy nie może rozpoczynać się przed zakończeniem innego okresu rozliczeniowego.");
                        } else if (!databaseHelper.checkEndDateInEditDialog(chosenProfileID, budgetsList.get(position).getBudID(), endDateTextView.getText().toString())) {
                            endDateTextView.setError("Okres rozliczeniowy nie może zakończyć się w trakcie innego okresu rozliczeniowego.");
                        } else if (!databaseHelper.checkDatesBetweenStartAndEndDatesInEditDialog(chosenProfileID, budgetsList.get(position).getBudID(), startDateTextView.getText().toString(), endDateTextView.getText().toString())) {
                            endDateTextView.setError("Istnieje już okres rozliczeniowy pomiędzy podaną datą rozpoczęcia i zakończenia okresu rozliczeniowego.");
                        } else if (!isAmountValid) {
                            initialAmount.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                        } else {
                            budgetModel.setBudInitialAmount(intAmount);
                            budgetModel.setBudDescription(description.getText().toString());
                            budgetModel.setBudStartDate(startDateTextView.getText().toString());
                            budgetModel.setBudEndDate(endDateTextView.getText().toString());
                            databaseHelper.updateBudget(budgetModel, budgetsList.get(position).getBudID());

                            budgetsList.get(position).setBudInitialAmount(intAmount);
                            budgetsList.get(position).setBudDescription(description.getText().toString());
                            budgetsList.get(position).setBudStartDate(startDateTextView.getText().toString());
                            budgetsList.get(position).setBudEndDate(endDateTextView.getText().toString());

                            dialog.cancel();
                            displayBudgetTransactionsList();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void showDeleteDialog(final int position) {
        final TextView startDateTextView, endDateTextView, initialAmount, description, currency;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(getApplicationContext());

        BigDecimal budgetsInitialAmount;
        budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(BudgetTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.budget_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startDateTextView = (TextView) dialog.findViewById(R.id.budget_delete_text_startDate);
        endDateTextView = (TextView) dialog.findViewById(R.id.budget_delete_text_endDate);
        initialAmount = (TextView) dialog.findViewById(R.id.budget_delete_text_initialAmount);
        currency = (TextView) dialog.findViewById(R.id.budget_delete_text_currency);
        description = (TextView) dialog.findViewById(R.id.budget_delete_text_desc);
        submitDelete = (Button) dialog.findViewById(R.id.budget_delete_submit);

        startDateTextView.setText(budgetsList.get(position).getBudStartDate());
        endDateTextView.setText(budgetsList.get(position).getBudEndDate());
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("#.##").format(budgetsInitialAmount));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(budgetsList.get(position).getBudDescription());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDeleteContinuationDialog(position);

                // notify list
                displayBudgetTransactionsList();
            }
        });

    }

    public void showDeleteContinuationDialog(final int position) {
        final TextView startDateTextView, endDateTextView, initialAmount, description, currency;
        Button submitDeleteTransactions, submitUnlinkTransactions;
        databaseHelper = new DatabaseHelper(getApplicationContext());

        BigDecimal budgetsInitialAmount;
        budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(BudgetTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.budget_delete_cont_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startDateTextView = (TextView) dialog.findViewById(R.id.budget_delete_cont_text_startDate);
        endDateTextView = (TextView) dialog.findViewById(R.id.budget_delete_cont_text_endDate);
        initialAmount = (TextView) dialog.findViewById(R.id.budget_delete_cont_text_initialAmount);
        currency = (TextView) dialog.findViewById(R.id.budget_delete_cont_text_currency);
        description = (TextView) dialog.findViewById(R.id.budget_delete_cont_text_desc);
        submitUnlinkTransactions = (Button) dialog.findViewById(R.id.budget_delete_cont_unlinkTrans);
        submitDeleteTransactions = (Button) dialog.findViewById(R.id.budget_delete_cont_deleteTrans);

        startDateTextView.setText(budgetsList.get(position).getBudStartDate());
        endDateTextView.setText(budgetsList.get(position).getBudEndDate());
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("#.##").format(budgetsInitialAmount));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(budgetsList.get(position).getBudDescription());

        submitUnlinkTransactions.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.unlinkTransactions(budgetsList.get(position).getBudID());
                databaseHelper.deleteBudget(budgetsList.get(position).getBudID());

                budgetsList.remove(position);
                dialog.cancel();

                startActivity(new Intent(v.getContext(), Budget.class));
            }
        });

        submitDeleteTransactions.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteTransactionsWithBudget(budgetsList.get(position).getBudID());
                databaseHelper.deleteBudget(budgetsList.get(position).getBudID());

                budgetsList.remove(position);
                dialog.cancel();

                startActivity(new Intent(v.getContext(), Budget.class));
            }
        });

    }
}