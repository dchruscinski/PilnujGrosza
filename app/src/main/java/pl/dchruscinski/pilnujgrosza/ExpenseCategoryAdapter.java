package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;

public class ExpenseCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExpenseCategoryModel> expenseCategoriesList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public ExpenseCategoryAdapter(Context context, Activity activity, ArrayList<ExpenseCategoryModel> expenseCategoriesList) {
        this.context = context;
        this.activity  = activity ;
        this.expenseCategoriesList = expenseCategoriesList;
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
            emptyText = (TextView) v.findViewById(R.id.expcat_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.expcat_empty_logo);
        }
    }

    public class ViewHolderExpenseCategory extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView editExpenseCategory, deleteExpenseCategory;
        public View layout;

        public ViewHolderExpenseCategory(View v) {
            super(v);
            layout = v;
            categoryName = (TextView) v.findViewById(R.id.expcat_rc_name);
            editExpenseCategory = (ImageView) v.findViewById(R.id.expcat_rc_editicon);
            deleteExpenseCategory = (ImageView) v.findViewById(R.id.expcat_rc_deleteicon);
        }
    }

    @Override
    public int getItemCount() {
        return expenseCategoriesList.size() > 0 ? expenseCategoriesList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (expenseCategoriesList.isEmpty()) {
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
            v = inflater.inflate(R.layout.expcat_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderExpenseCategory(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.expcat_empty_view, parent, false);
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

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            final ExpenseCategoryModel expenseCategory = expenseCategoriesList.get(position);
            ViewHolderExpenseCategory expenseCategoryHolder = (ViewHolderExpenseCategory) holder;

            expenseCategoryHolder.categoryName.setText(expenseCategory.getExpcatName());

            expenseCategoryHolder.editExpenseCategory.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            expenseCategoryHolder.deleteExpenseCategory.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position);
                }
            });
        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void showEditDialog(final int position) {
        final EditText name;
        Button submitEdit;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expcat_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.expcat_editform_text_name);
        submitEdit = (Button) dialog.findViewById(R.id.expcat_editform_submit);

        name.setText(expenseCategoriesList.get(position).getExpcatName());

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(context);

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę kategorii.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-]{2,30}")) {
                    name.setError("Nazwa kategorii powinna składać się z co najmniej dwóch liter.");
                } else if (databaseHelper.checkExistingExpenseCategoryName(expenseCategoriesList.get(position).getExpcatID(), name.getText().toString())) {
                    name.setError("Istnieje już kategoria z podaną nazwą.");
                } else {
                    databaseHelper.updateExpenseCategoryName(name.getText().toString(), expenseCategoriesList.get(position).getExpcatID());
                    expenseCategoriesList.get(position).setExpcatName(name.getText().toString());

                    dialog.cancel();
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void showDeleteDialog(final int position) {
        final TextView name;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expcat_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.expcat_deleteform_info_name);
        submitDelete = (Button) dialog.findViewById(R.id.expcat_deleteform_submit);

        name.setText(expenseCategoriesList.get(position).getExpcatName());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteExpenseCategory(expenseCategoriesList.get(position).getExpcatID());
                expenseCategoriesList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

    }

}
