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

public class IncomeCategory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<IncomeCategoryModel> incomeCategoriesList;
    FloatingActionButton addIncCatFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inccat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.inccat_list_rc);
        addIncCatFAB = (FloatingActionButton) findViewById(R.id.inccat_fab_addIncCat);
        databaseHelper = new DatabaseHelper(this);
        displayIncomeCategoriesList();

        addIncCatFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog();
            }
        });
    }

    //display notes list
    public void displayIncomeCategoriesList() {
        incomeCategoriesList = new ArrayList<>(databaseHelper.getIncomeCategoryList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        IncomeCategoryAdapter adapter = new IncomeCategoryAdapter(getApplicationContext(), this, incomeCategoriesList);
        recyclerView.setAdapter(adapter);
    }

    public void showCreateDialog() {
        final EditText name;
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.inccat_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.inccat_createform_text_name);
        submitCreate = (Button) dialog.findViewById(R.id.inccat_createform_submit);

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                IncomeCategoryModel incomeCategoryModel = new IncomeCategoryModel();

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę kategorii.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-]{2,30}")) {
                    name.setError("Nazwa kategorii powinna składać się z co najmniej dwóch liter.");
                } else if (databaseHelper.checkExistingIncomeCategoryName(name.getText().toString())) {
                    name.setError("Istnieje już kategoria z podaną nazwą.");
                } else {
                    incomeCategoryModel.setInccatName(name.getText().toString());
                    databaseHelper.addIncomeCategory(incomeCategoryModel);

                    dialog.cancel();
                    displayIncomeCategoriesList();
                }
            }
        });
    }
}