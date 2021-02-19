package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList transactionsList;
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

        public TextView txtHeader, txtFooter;
        public ImageView editExpense;
        public View layout;

        public ViewHolderExpense(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.exp_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.exp_rc_secondLine);
        }
    }

    public class ViewHolderIncome extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter;
        public ImageView editIncome;
        public View layout;

        public ViewHolderIncome(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.inc_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.inc_rc_secondLine);
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
            Object transaction = transactionsList.get(position);
            if (transaction instanceof ExpenseModel) {
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

        if (viewType == VIEW_TYPE_EXPENSE_VIEW) {
            final ExpenseModel expense = (ExpenseModel) transactionsList.get(position);
            ViewHolderExpense expenseHolder = (ViewHolderExpense) holder;

            expenseHolder.txtHeader.setText(expense.getExpValue());
            expenseHolder.txtFooter.setText(expense.getExpDescription());

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

        } else if (viewType == VIEW_TYPE_INCOME_VIEW) {
            final IncomeModel income = (IncomeModel) transactionsList.get(position);
            ViewHolderExpense incomeHolder = (ViewHolderExpense) holder;

            incomeHolder.txtHeader.setText(income.getIncValue());
            incomeHolder.txtFooter.setText(income.getIncDescription());

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

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void showExpenseEditDialog(final int position) {
        final EditText description, value, budget, category, isRecurring, payDate;
        Button submitEdit;
        transactionsList = ArrayList<ExpenseModel>;

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

        description = (EditText) dialog.findViewById(R.id.profile_editform_text_name);
        value = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN);
        budget = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN_confirm);
        category = (EditText) dialog.findViewById(R.id.profile_editform_text_newHelperQuestion);
        isRecurring = (EditText) dialog.findViewById(R.id.profile_editform_text_newHelperAnswer);
        payDate = (EditText) dialog.findViewById(R.id.profile_editform_text_newHelperAnswer);

        submitEdit = (Button) dialog.findViewById(R.id.profile_editform_submit_name);

        description.setText((ExpenseModel) transactionsList.get(position).getExpDescription());
        value.setText(transactionsList.get(position).getProfHelperQuestion());
        budget.setText(transactionsList.get(position).getProfName());
        category.setText(transactionsList.get(position).getProfHelperQuestion());
        isRecurring.setText(transactionsList.get(position).getProfName());
        payDate.setText(transactionsList.get(position).getProfHelperQuestion());

        submitName.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nową nazwę profilu.");
                } else if (!name.getText().toString().matches("[a-zA-Z]{2,20}")) {
                    name.setError("Nazwa profilu powinna składać się z co najmniej dwóch liter. Niedozwolone są cyfry oraz znaki specjalne.");
                } else if (databaseHelper.checkExistingProfileName(name.getText().toString())) {
                    name.setError("Istnieje już profil z podaną nazwą.");
                } else {
                    databaseHelper.updateProfileName(name.getText().toString(), profilesList.get(position).getProfID());
                    profilesList.get(position).setProfName(name.getText().toString());
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitPIN.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                // hashing password
                String newHashSalt = BCrypt.gensalt();
                String hashedPIN = BCrypt.hashpw(newPIN.getText().toString(), newHashSalt);
                String hashedPINConfirm = BCrypt.hashpw(newPINConfirm.getText().toString(), newHashSalt);
                boolean passwordMatches = hashedPIN.equals(hashedPINConfirm);

                if (newPIN.getText().toString().isEmpty() || newPINConfirm.getText().toString().isEmpty()) {
                    newPIN.setError("W celu zmiany kodu PIN podaj nowy kod, a następnie go potwierdź.");
                } else if (newPIN.getText().toString().length() < 6 || newPINConfirm.getText().toString().length() < 6) {
                    newPIN.setError("Kod PIN musi składać się z 6 cyfr.");
                } else if (!passwordMatches) {
                    newPIN.setError("Podane nowe kody PIN nie są identyczne.");
                } else {
                    databaseHelper.updateProfilePIN(hashedPIN, newHashSalt, profilesList.get(position).getProfID());

                    profilesList.get(position).setProfPIN(hashedPIN);
                    profilesList.get(position).setProfPINSalt(newHashSalt);

                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitQandA.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String helperHashSalt = BCrypt.gensalt();
                String helperQuestion = newQuestion.getText().toString().trim();
                String helperHashedAnswer = BCrypt.hashpw(newAnswer.getText().toString().trim(), helperHashSalt);

                if (newQuestion.getText().toString().trim().isEmpty()) {
                    newQuestion.setError("Podaj pytanie pomocnicze.");
                } else if (newAnswer.getText().toString().trim().isEmpty()) {
                    newAnswer.setError("Podaj odpowiedź na pytanie pomocnicze.");
                } else if (!newQuestion.getText().toString().matches("[\\sa-zA-Z0-9_.,-]{2,128}[?]")) {
                    newQuestion.setError("Pytanie pomocnicze powinno składać się z co najmniej dwóch liter. Na końcu musi znajdować się znak zapytania.");
                } else {
                    databaseHelper.updateHelperQuestionAndAnswer(helperQuestion, helperHashedAnswer, helperHashSalt, profilesList.get(position).getProfID());

                    profilesList.get(position).setProfHelperQuestion(helperQuestion);
                    profilesList.get(position).setProfHelperAnswer(helperHashedAnswer);
                    profilesList.get(position).setProfHelperSalt(helperHashSalt);
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitDeleteProfile.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showLoginBeforeDeleteDialog(position);
            }
        });
    }

}
