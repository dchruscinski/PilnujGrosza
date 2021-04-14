package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        public TextView budgetStartDate, budgetEndDate, description, amount, initialAmount, currency;
        public View layout;

        public ViewHolderBudget(View v) {
            super(v);
            layout = v;
            budgetStartDate = (TextView) v.findViewById(R.id.budget_rc_startDate);
            budgetEndDate = (TextView) v.findViewById(R.id.budget_rc_endDate);
            description = (TextView) v.findViewById(R.id.budget_rc_desc);
            amount = (TextView) v.findViewById(R.id.budget_rc_amount);
            initialAmount = (TextView) v.findViewById(R.id.budget_rc_initialAmount);
            currency = (TextView) v.findViewById(R.id.budget_rc_currency);
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
            databaseHelper = new DatabaseHelper(context);
            final BudgetModel budget = budgetsList.get(position);
            ViewHolderBudget budgetHolder = (ViewHolderBudget) holder;

            budgetHolder.budgetStartDate.setText(budget.getBudStartDate());
            budgetHolder.budgetEndDate.setText(budget.getBudEndDate());

            budgetsAmount = BigDecimal.valueOf(budgetsList.get(position).getBudAmount()).divide(BigDecimal.valueOf(100));
            budgetsInitialAmount = BigDecimal.valueOf(budgetsList.get(position).getBudInitialAmount()).divide(BigDecimal.valueOf(100));

            budgetHolder.description.setText(budget.getBudDescription());
            budgetHolder.amount.setText(budgetsAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsAmount) : new DecimalFormat("#.##").format(budgetsAmount));
            budgetHolder.initialAmount.setText(budgetsInitialAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(budgetsInitialAmount) : new DecimalFormat("#.##").format(budgetsInitialAmount));
            budgetHolder.currency.setText(databaseHelper.getCurrency(chosenProfileID));

            budgetHolder.budgetStartDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("budID", budgetsList.get(position).getBudID());
                    bundle.putInt("position", position);
                    Intent intent = new Intent(context, BudgetTransaction.class).putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

            budgetHolder.budgetEndDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("budID", budgetsList.get(position).getBudID());
                    bundle.putInt("position", position);
                    Intent intent = new Intent(context, BudgetTransaction.class).putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

            budgetHolder.description.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("budID", budgetsList.get(position).getBudID());
                    bundle.putInt("position", position);
                    Intent intent = new Intent(context, BudgetTransaction.class).putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

            budgetHolder.amount.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("budID", budgetsList.get(position).getBudID());
                    bundle.putInt("position", position);
                    Intent intent = new Intent(context, BudgetTransaction.class).putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

            budgetHolder.initialAmount.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("budID", budgetsList.get(position).getBudID());
                    bundle.putInt("position", position);
                    Intent intent = new Intent(context, BudgetTransaction.class).putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

}
