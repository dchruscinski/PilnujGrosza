package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TransactionModel> transactionsList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_EXPENSE_VIEW = 1;
    private static final int VIEW_TYPE_INCOME_VIEW = 2;

    public TransactionAdapter(Context context, Activity activity, ArrayList transactionsList) {
        this.context = context;
        this.activity  = activity ;
        this.transactionsList = transactionsList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolderEmpty extends RecyclerView.ViewHolder {

        public TextView emptyText;
        public ImageView emptyIcon;
        public View layout;

        public ViewHolderEmpty(View v) {
            super(v);
            layout = v;
            emptyText = (TextView) v.findViewById(R.id.trans_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.trans_empty_logo);
        }
    }

    public class ViewHolderExpense extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter, transactionDate, categoryName;
        public View layout;

        public ViewHolderExpense(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.exp_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.exp_rc_secondLine);
            transactionDate = (TextView) v.findViewById(R.id.exp_rc_transDate);
            categoryName = (TextView) v.findViewById(R.id.exp_rc_category);
        }
    }

    public class ViewHolderIncome extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter, transactionDate, categoryName;
        public View layout;

        public ViewHolderIncome(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.inc_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.inc_rc_secondLine);
            transactionDate = (TextView) v.findViewById(R.id.inc_rc_transDate);
            categoryName = (TextView) v.findViewById(R.id.inc_rc_category);
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size() > 0 ? transactionsList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (transactionsList.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST;
        } else {
            if (transactionsList.get(position).getTransType().equals("expense")) {
                return VIEW_TYPE_EXPENSE_VIEW;
            } else {
                return VIEW_TYPE_INCOME_VIEW;
            }

        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TYPE_EXPENSE_VIEW) {
            v = inflater.inflate(R.layout.exp_rc_row, parent, false);
            vh = new TransactionAdapter.ViewHolderExpense(v);
        } else if (viewType == VIEW_TYPE_INCOME_VIEW) {
            v = inflater.inflate(R.layout.inc_rc_row, parent, false);
            vh = new TransactionAdapter.ViewHolderIncome(v);
        } else {
            v = inflater.inflate(R.layout.trans_empty_view, parent, false);
            vh = new TransactionAdapter.ViewHolderEmpty(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*
            - get element from your dataset at this position
            - replace the contents of the view with that element
         */
        int viewType = getItemViewType(position);
        databaseHelper = new DatabaseHelper(context);
        BigDecimal transactionValue;

        if (viewType == VIEW_TYPE_EXPENSE_VIEW) {
            final TransactionModel expense = transactionsList.get(position);
            ViewHolderExpense expenseHolder = (ViewHolderExpense) holder;

            transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

            expenseHolder.txtHeader.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
            expenseHolder.txtFooter.setText(expense.getTransDescription());
            expenseHolder.transactionDate.setText(expense.getTransDate());
            expenseHolder.categoryName.setText(databaseHelper.getExpenseCategory(expense.getTransCatID()).getExpcatName());

            expenseHolder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExpenseEditDialog(position);
                }
            });

            expenseHolder.txtFooter.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showExpenseEditDialog(position);
                }
            });

            expenseHolder.transactionDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showExpenseEditDialog(position);
                }
            });

            expenseHolder.categoryName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showExpenseEditDialog(position);
                }
            });

        } else if (viewType == VIEW_TYPE_INCOME_VIEW) {
            final TransactionModel income = transactionsList.get(position);
            ViewHolderIncome incomeHolder = (ViewHolderIncome) holder;

            transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

            incomeHolder.txtHeader.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
            incomeHolder.txtFooter.setText(income.getTransDescription());
            incomeHolder.transactionDate.setText(income.getTransDate());
            incomeHolder.categoryName.setText(databaseHelper.getIncomeCategory(income.getTransCatID()).getInccatName());

            incomeHolder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showIncomeEditDialog(position);
                }
            });

            incomeHolder.txtFooter.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showIncomeEditDialog(position);
                }
            });

            incomeHolder.transactionDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showIncomeEditDialog(position);
                }
            });

            incomeHolder.categoryName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showIncomeEditDialog(position);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void displayExpenseCategoriesSpinnerData(Spinner spinner, String searchString) {
        Cursor expenseCategoriesCursorForSpinner = databaseHelper.getExpenseCategoriesForSpinner();

        String[] from = { "expcatName" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter expenseCategoryAdapterForSpinner = new SimpleCursorAdapter(this.context,
                android.R.layout.simple_spinner_item,
                expenseCategoriesCursorForSpinner, from, to, 0);

        expenseCategoryAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(expenseCategoryAdapterForSpinner);
        if (!searchString.equals("")) {
            spinner.setSelection(getIndexForSpinner(spinner, from[0], searchString));
        }
    }

    public void displayIncomeCategoriesSpinnerData(Spinner spinner, String searchString) {
        Cursor incomeCategoriesCursorForSpinner = databaseHelper.getIncomeCategoriesForSpinner();

        String[] from = { "inccatName" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter incomeCategoryAdapterForSpinner = new SimpleCursorAdapter(this.context,
                android.R.layout.simple_spinner_item,
                incomeCategoriesCursorForSpinner, from, to, 0);

        incomeCategoryAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(incomeCategoryAdapterForSpinner);
        if (!searchString.equals("")) {
            spinner.setSelection(getIndexForSpinner(spinner, from[0], searchString));
        }
    }

    public void displayBudgetsSpinnerData(Spinner spinner, String searchString) {
        Cursor budgetsCursorForSpinner = databaseHelper.getBudgetsForSpinner();

        String[] from = { "budDates" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter budgetAdapterForSpinner = new SimpleCursorAdapter(this.context,
                android.R.layout.simple_spinner_item,
                budgetsCursorForSpinner, from, to, 0);

        budgetAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(budgetAdapterForSpinner);
        if (!searchString.equals("")) {
            spinner.setSelection(getIndexForSpinner(spinner, from[0], searchString));
        }
    }

    private int getIndexForSpinner(Spinner spinner, String columnName, String searchString) {

        if (searchString == null || spinner.getCount() == 0) {
            return -1; // not found
        }
        else {
            Cursor cursor = (Cursor)spinner.getItemAtPosition(0);

            int initialCursorPos = cursor.getPosition(); //  remember for later

            int index = -1; // not found
            for (int i = 0; i < spinner.getCount(); i++) {

                cursor.moveToPosition(i);
                String itemText = cursor.getString(cursor.getColumnIndex(columnName));

                if (itemText.equals(searchString)) {
                    index = i; // found!
                    break;
                }
            }

            cursor.moveToPosition(initialCursorPos); // leave cursor as we found it.

            return index;
        }
    }

    public void showExpenseEditDialog(final int position) {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitEdit, submitDelete;
        String selectedCategory, selectedBudget;

        BigDecimal transactionValue;
        transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expense_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.expense_editform_text_date);
        value = (EditText) dialog.findViewById(R.id.expense_editform_text_value);
        currency = (TextView) dialog.findViewById(R.id.expense_editform_text_currency);
        description = (EditText) dialog.findViewById(R.id.expense_editform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.expense_editform_spin_cat);
        countToBudget = (CheckBox) dialog.findViewById(R.id.expense_editform_checkbox_budget);
        budget = (Spinner) dialog.findViewById(R.id.expense_editform_spin_budget);
        budgetText = (TextView) dialog.findViewById(R.id.expense_editform_spin_budget_info);
        submitEdit = (Button) dialog.findViewById(R.id.expense_editform_submitEdit);
        submitDelete = (Button) dialog.findViewById(R.id.expense_editform_submitDelete);

        dateTextView.setText(transactionsList.get(position).getTransDate());
        value.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(transactionsList.get(position).getTransDescription());

        if (transactionsList.get(position).getTransCatID() != 0) {
            selectedCategory = databaseHelper.getExpenseCategory(transactionsList.get(position).getTransCatID()).getExpcatName();
        } else {
            selectedCategory = "";
        }

        if (transactionsList.get(position).getTransBudID() != 0) {
            selectedBudget = databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudEndDate();
        } else {
            selectedBudget = "";
        }

        displayExpenseCategoriesSpinnerData(category, selectedCategory);
        displayBudgetsSpinnerData(budget, selectedBudget);

        if (!selectedBudget.equals("")) {
            countToBudget.setChecked(true);
            budget.setVisibility(VISIBLE);
            budgetText.setVisibility(VISIBLE);
        }

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
                new DatePickerDialog(v.getRootView().getContext(), date,
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

        submitEdit.setOnClickListener(new View.OnClickListener() {;
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
                    value.setError("Podaj nową wartość wydatku.");
                } else if (!isValueValid) {
                    value.setError("Podaj nową wartość z dokładnością do dwóch miejsc dziesiętnych.");
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
                    databaseHelper.updateExpense(transactionsList.get(position).getTransID(), transactionModel);

                    transactionsList.get(position).setTransDate(dateTextView.getText().toString());
                    transactionsList.get(position).setTransValue(intValue);
                    transactionsList.get(position).setTransDescription(description.getText().toString());
                    if (countToBudget.isChecked()) {
                        transactionsList.get(position).setTransBudID((int) budget.getSelectedItemId());
                    } else {
                        transactionsList.get(position).setTransBudID(NULL);
                    }
                    transactionsList.get(position).setTransCatID((int) category.getSelectedItemId());

                    // notify list
                    notifyDataSetChanged();
                    dialog.cancel();
                }
            }
        });

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDeleteExpenseContinuationDialog(position);
                }
        });

    }

    public void showDeleteExpenseContinuationDialog(final int position) {
        final TextView dateTextView, currency, description, value, category, budget;
        Button submitDelete;

        BigDecimal transactionValue;
        transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expense_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.expense_deleteform_text_date);
        value = (TextView) dialog.findViewById(R.id.expense_deleteform_text_value);
        currency = (TextView) dialog.findViewById(R.id.expense_deleteform_text_currency);
        description = (TextView) dialog.findViewById(R.id.expense_deleteform_text_desc);
        category = (TextView) dialog.findViewById(R.id.expense_deleteform_spin_cat_text);
        budget = (TextView) dialog.findViewById(R.id.expense_deleteform_spin_budget_text);
        submitDelete = (Button) dialog.findViewById(R.id.expense_deleteform_submitDelete);

        dateTextView.setText(transactionsList.get(position).getTransDate());
        value.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(transactionsList.get(position).getTransDescription());
        category.setText(databaseHelper.getExpenseCategory(transactionsList.get(position).getTransCatID()).getExpcatName());
        if (transactionsList.get(position).getTransBudID() != 0) {
            String budgetInfoText = databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudEndDate();
            budget.setText(budgetInfoText);
        } else {
            budget.setText("Nie zalicza się do budżetu");
        }

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteExpense(transactionsList.get(position).getTransID());
                transactionsList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

    }

    public void showIncomeEditDialog(final int position) {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget, changeBudgetInitialAmount;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitEdit, submitDelete;
        String selectedCategory, selectedBudget;

        BigDecimal transactionValue;
        transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.income_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.income_editform_text_date);
        value = (EditText) dialog.findViewById(R.id.income_editform_text_value);
        currency = (TextView) dialog.findViewById(R.id.income_editform_text_currency);
        description = (EditText) dialog.findViewById(R.id.income_editform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.income_editform_spin_cat);
        countToBudget = (CheckBox) dialog.findViewById(R.id.income_editform_checkbox_budget);
        budget = (Spinner) dialog.findViewById(R.id.income_editform_spin_budget);
        budgetText = (TextView) dialog.findViewById(R.id.income_editform_spin_budget_info);
        changeBudgetInitialAmount = (CheckBox) dialog.findViewById(R.id.income_editform_checkbox_budgetInitialAmount);
        submitEdit = (Button) dialog.findViewById(R.id.income_editform_submitEdit);
        submitDelete = (Button) dialog.findViewById(R.id.income_editform_submitDelete);

        dateTextView.setText(transactionsList.get(position).getTransDate());
        value.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(transactionsList.get(position).getTransDescription());

        if (transactionsList.get(position).getTransCatID() != 0) {
            selectedCategory = databaseHelper.getIncomeCategory(transactionsList.get(position).getTransCatID()).getInccatName();
        } else {
            selectedCategory = "";
        }

        if (transactionsList.get(position).getTransBudID() != 0) {
            selectedBudget = databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudEndDate();
        } else {
            selectedBudget = "";
        }

        displayIncomeCategoriesSpinnerData(category, selectedCategory);
        displayBudgetsSpinnerData(budget, selectedBudget);

        if (!selectedBudget.equals("")) {
            countToBudget.setChecked(true);
            budget.setVisibility(VISIBLE);
            budgetText.setVisibility(VISIBLE);
            changeBudgetInitialAmount.setVisibility(VISIBLE);
        }

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
                new DatePickerDialog(v.getRootView().getContext(), date,
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

        submitEdit.setOnClickListener(new View.OnClickListener() {;
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
                    value.setError("Podaj nową wartość przychodu.");
                } else if (!isValueValid) {
                    value.setError("Podaj nową wartość z dokładnością do dwóch miejsc dziesiętnych.");
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
                    if (changeBudgetInitialAmount.isChecked()) {
                        databaseHelper.updateIncome(transactionsList.get(position).getTransID(), transactionModel, true);
                    } else {
                        databaseHelper.updateIncome(transactionsList.get(position).getTransID(), transactionModel, false);
                    }

                    transactionsList.get(position).setTransDate(dateTextView.getText().toString());
                    transactionsList.get(position).setTransValue(intValue);
                    transactionsList.get(position).setTransDescription(description.getText().toString());
                    if (countToBudget.isChecked()) {
                        transactionsList.get(position).setTransBudID((int) budget.getSelectedItemId());
                    } else {
                        transactionsList.get(position).setTransBudID(NULL);
                    }
                    transactionsList.get(position).setTransCatID((int) category.getSelectedItemId());

                    // notify list
                    notifyDataSetChanged();
                    dialog.cancel();
                }
            }
        });

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (!selectedBudget.equals("")) {
                    showDeleteIncomeContinuationDialog(position, true);
                } else {
                    showDeleteIncomeContinuationDialog(position, false);
                }
            }
        });

    }

    public void showDeleteIncomeContinuationDialog(final int position, boolean changeInitialBudget) {
        final TextView dateTextView, currency, description, value, category, budget;
        Button submitDelete;

        BigDecimal transactionValue;
        transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.income_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.income_deleteform_text_date);
        value = (TextView) dialog.findViewById(R.id.income_deleteform_text_value);
        currency = (TextView) dialog.findViewById(R.id.income_deleteform_text_currency);
        description = (TextView) dialog.findViewById(R.id.income_deleteform_text_desc);
        category = (TextView) dialog.findViewById(R.id.income_deleteform_spin_cat_text);
        budget = (TextView) dialog.findViewById(R.id.income_deleteform_spin_budget_text);
        submitDelete = (Button) dialog.findViewById(R.id.income_deleteform_submitDelete);

        dateTextView.setText(transactionsList.get(position).getTransDate());
        value.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(transactionsList.get(position).getTransDescription());
        category.setText(databaseHelper.getExpenseCategory(transactionsList.get(position).getTransCatID()).getExpcatName());
        if (transactionsList.get(position).getTransBudID() != 0) {
            String budgetInfoText = databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudEndDate();
            budget.setText(budgetInfoText);
        } else {
            budget.setText("Nie zalicza się do budżetu");
        }

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (!changeInitialBudget) {
                    databaseHelper.deleteIncome(transactionsList.get(position).getTransID(), false);

                    transactionsList.remove(position);
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                } else {
                    dialog.cancel();
                    showDeleteIncomeWithBudgetInitialAmount(position);
                }
            }
        });

    }

    public void showDeleteIncomeWithBudgetInitialAmount(final int position) {
        final TextView dateTextView, currency, description, value, category, budget;
        Button submitChange, submitDontChange;

        BigDecimal transactionValue;
        transactionValue = BigDecimal.valueOf(transactionsList.get(position).getTransValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.income_delete_cont_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.income_deleteform_cont_text_date);
        value = (TextView) dialog.findViewById(R.id.income_deleteform_cont_text_value);
        currency = (TextView) dialog.findViewById(R.id.income_deleteform_cont_text_currency);
        description = (TextView) dialog.findViewById(R.id.income_deleteform_cont_text_desc);
        category = (TextView) dialog.findViewById(R.id.income_deleteform_cont_spin_cat_text);
        budget = (TextView) dialog.findViewById(R.id.income_deleteform_cont_spin_budget_text);
        submitChange = (Button) dialog.findViewById(R.id.income_deleteform_cont_submitChange);
        submitDontChange = (Button) dialog.findViewById(R.id.income_deleteform_cont_submitDontChange);

        dateTextView.setText(transactionsList.get(position).getTransDate());
        value.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(transactionsList.get(position).getTransDescription());
        category.setText(databaseHelper.getExpenseCategory(transactionsList.get(position).getTransCatID()).getExpcatName());
        if (transactionsList.get(position).getTransBudID() != 0) {
            String budgetInfoText = databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(transactionsList.get(position).getTransBudID()).getBudEndDate();
            budget.setText(budgetInfoText);
        } else {
            budget.setText("Nie zalicza się do budżetu");
        }

        submitChange.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteIncome(transactionsList.get(position).getTransID(), true);

                transactionsList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

        submitDontChange.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteIncome(transactionsList.get(position).getTransID(), false);

                transactionsList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

    }

}
