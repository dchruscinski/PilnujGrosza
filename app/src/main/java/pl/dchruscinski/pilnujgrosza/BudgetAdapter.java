package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfilePosition;

public class BudgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BudgetModel> budgetsList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public BudgetAdapter(Context context, Activity activity, ArrayList<BudgetModel> budgetsList) {
        this.context = context;
        this.activity  = activity ;
        this.budgetsList = budgetsList;
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
            emptyText = (TextView) v.findViewById(R.id.inccat_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.inccat_empty_logo);
        }
    }

    public class ViewHolderBudget extends RecyclerView.ViewHolder {
        public TextView budgetStartDate, budgetEndDate, amount, initialAmount;
        public View layout;

        public ViewHolderBudget(View v) {
            super(v);
            layout = v;
            budgetStartDate = (TextView) v.findViewById(R.id.budget_rc_startDate);
            budgetEndDate = (TextView) v.findViewById(R.id.budget_rc_endDate);
            amount = (TextView) v.findViewById(R.id.budget_rc_amount);
            initialAmount = (TextView) v.findViewById(R.id.budget_rc_initialAmount);
        }
    }

    @Override
    public int getItemCount() {
        return budgetsList.size() > 0 ? budgetsList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (budgetsList.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            // create a new view
            v = inflater.inflate(R.layout.budget_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderBudget(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.budget_empty_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderEmpty(v);
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
        BigDecimal budgetsAmount, budgetsInitialAmount;

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            final BudgetModel budget = budgetsList.get(position);
            ViewHolderBudget budgetHolder = (ViewHolderBudget) holder;

            budgetHolder.budgetStartDate.setText(budget.getBudStartDate());
            budgetHolder.budgetEndDate.setText(budget.getBudEndDate());

            budgetsAmount = BigDecimal.valueOf(budgetsList.get(position).getBudAmount()).divide(BigDecimal.valueOf(100));
            budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

            budgetHolder.amount.setText(budgetsAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsAmount) : new DecimalFormat("0.00").format(budgetsAmount));
            budgetHolder.initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("0.00").format(budgetsInitialAmount));

            budgetHolder.budgetStartDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            budgetHolder.budgetEndDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            budgetHolder.amount.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            budgetHolder.initialAmount.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void showEditDialog(final int position) {
        final EditText initialAmount, description;
        final TextView startDateTextView, endDateTextView;
        Button submitEdit, submitDelete;

        BigDecimal budgetsInitialAmount;
        budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        final Calendar startDateCalendar = Calendar.getInstance();
        final Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.MONTH, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        final Dialog dialog = new Dialog(activity);
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
        description = (EditText) dialog.findViewById(R.id.budget_editform_text_desc);
        submitEdit = (Button) dialog.findViewById(R.id.budget_editform_submit);
        submitDelete = (Button) dialog.findViewById(R.id.budget_editform_delete);

        startDateTextView.setText(budgetsList.get(position).getBudStartDate());
        endDateTextView.setText(budgetsList.get(position).getBudEndDate());
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("0.00").format(budgetsInitialAmount));
        description.setText(budgetsList.get(position).getBudDescription());

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
                databaseHelper = new DatabaseHelper(context);
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
                            notifyDataSetChanged();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        submitDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDeleteDialog(position);
            }
        });
    }

    public void showDeleteDialog(final int position) {
        final TextView startDateTextView, endDateTextView, initialAmount, description;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(context);

        BigDecimal budgetsInitialAmount;
        budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
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
        description = (TextView) dialog.findViewById(R.id.budget_delete_text_desc);
        submitDelete = (Button) dialog.findViewById(R.id.budget_delete_submit);

        startDateTextView.setText(budgetsList.get(position).getBudStartDate());
        endDateTextView.setText(budgetsList.get(position).getBudEndDate());
        initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("0.00").format(budgetsInitialAmount));
        description.setText(budgetsList.get(position).getBudDescription());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteBudget(budgetsList.get(position).getBudID());
                budgetsList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

    }

}
