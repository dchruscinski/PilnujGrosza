package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IncomeCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<IncomeCategoryModel> incomeCategoriesList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public IncomeCategoryAdapter(Context context, Activity activity, ArrayList<IncomeCategoryModel> incomeCategoriesList) {
        this.context = context;
        this.activity  = activity ;
        this.incomeCategoriesList = incomeCategoriesList;
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

    public class ViewHolderIncomeCategory extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView editIncomeCategory, deleteIncomeCategory;
        public View layout;

        public ViewHolderIncomeCategory(View v) {
            super(v);
            layout = v;
            categoryName = (TextView) v.findViewById(R.id.inccat_rc_name);
            editIncomeCategory = (ImageView) v.findViewById(R.id.inccat_rc_editicon);
            deleteIncomeCategory = (ImageView) v.findViewById(R.id.inccat_rc_deleteicon);
        }
    }

    @Override
    public int getItemCount() {
        return incomeCategoriesList.size() > 0 ? incomeCategoriesList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (incomeCategoriesList.isEmpty()) {
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
            v = inflater.inflate(R.layout.inccat_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderIncomeCategory(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.inccat_empty_view, parent, false);
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
            final IncomeCategoryModel incomeCategory = incomeCategoriesList.get(position);
            ViewHolderIncomeCategory incomeCategoryHolder = (ViewHolderIncomeCategory) holder;

            incomeCategoryHolder.categoryName.setText(incomeCategory.getInccatName());

            incomeCategoryHolder.editIncomeCategory.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            incomeCategoryHolder.deleteIncomeCategory.setOnClickListener(new View.OnClickListener() {;
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
        dialog.setContentView(R.layout.inccat_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.inccat_editform_text_name);
        submitEdit = (Button) dialog.findViewById(R.id.inccat_editform_submit);

        name.setText(incomeCategoriesList.get(position).getInccatName());

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(context);

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę kategorii.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-]{2,30}")) {
                    name.setError("Nazwa kategorii powinna składać się z co najmniej dwóch liter.");
                } else if (databaseHelper.checkExistingIncomeCategoryName(incomeCategoriesList.get(position).getInccatID(), name.getText().toString())) {
                    name.setError("Istnieje już kategoria z podaną nazwą.");
                } else {
                    databaseHelper.updateIncomeCategoryName(name.getText().toString(), incomeCategoriesList.get(position).getInccatID());
                    incomeCategoriesList.get(position).setInccatName(name.getText().toString());

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
        dialog.setContentView(R.layout.inccat_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.inccat_deleteform_info_name);
        submitDelete = (Button) dialog.findViewById(R.id.inccat_deleteform_submit);

        name.setText(incomeCategoriesList.get(position).getInccatName());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteIncomeCategory(incomeCategoriesList.get(position).getInccatID());
                incomeCategoriesList.remove(position);
                dialog.cancel();

                // notify list
                notifyDataSetChanged();
            }
        });

    }

}
