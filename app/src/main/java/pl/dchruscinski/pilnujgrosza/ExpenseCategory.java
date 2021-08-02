package pl.dchruscinski.pilnujgrosza;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpenseCategory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<ExpenseCategoryModel> expenseCategoriesList;
    FloatingActionButton addExpCatFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expcat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.expcat_list_rc);
        addExpCatFAB = (FloatingActionButton) findViewById(R.id.expcat_fab_addExpCat);
        databaseHelper = new DatabaseHelper(this);
        displayExpenseCategoriesList();

        addExpCatFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog();
            }
        });
    }

    //display notes list
    public void displayExpenseCategoriesList() {
        expenseCategoriesList = new ArrayList<>(databaseHelper.getExpenseCategoryList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ExpenseCategoryAdapter adapter = new ExpenseCategoryAdapter(this, this, expenseCategoriesList);
        recyclerView.setAdapter(adapter);
    }

    public void showCreateDialog() {
        final EditText name;
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expcat_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.expcat_createform_text_name);
        submitCreate = (Button) dialog.findViewById(R.id.expcat_createform_submit);

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel();

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę kategorii.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-]{2,30}")) {
                    name.setError("Nazwa kategorii powinna składać się z co najmniej dwóch liter.");
                } else if (databaseHelper.checkExistingExpenseCategoryName(0, name.getText().toString())) {
                    System.out.println("Name: " + name.getText().toString());
                    name.setError("Istnieje już kategoria z podaną nazwą.");
                } else {
                    expenseCategoryModel.setExpcatName(name.getText().toString());
                    databaseHelper.addExpenseCategory(expenseCategoryModel);

                    dialog.cancel();
                    displayExpenseCategoriesList();
                }
            }
        });
    }
}