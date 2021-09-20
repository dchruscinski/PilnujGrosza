package pl.dchruscinski.pilnujgrosza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;


public class ShoppingList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ShoppingModel> shoppingList;
    FloatingActionButton addShoListFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        adapter = new ShoppingListAdapter(this, this, shoppingList);

        recyclerView = (RecyclerView) findViewById(R.id.sholist_list_rc);
        addShoListFAB = (FloatingActionButton) findViewById(R.id.sholist_fab_addShoList);
        databaseHelper = new DatabaseHelper(this);

        if (getIntent().getExtras() != null && getIntent().getStringExtra("action").equals("shoppingListButton")) {
            displayShoppingsList();
            showCreateDialog();
        } else {
            displayShoppingsList();
        }

        addShoListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog();
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
                inflater.inflate(R.menu.menu_sort_items_name_and_date, popup.getMenu());
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.menu_sort_action_sort_name_asc:
                            Collections.sort(shoppingList, ShoppingModel.shoppingNameAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_name_desc:
                            Collections.sort(shoppingList, ShoppingModel.shoppingNameDescComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_asc:
                            Collections.sort(shoppingList, ShoppingModel.shoppingDateAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_desc:
                            Collections.sort(shoppingList, ShoppingModel.shoppingDateDescComparator);
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

    public void displayShoppingsList() {
        shoppingList = new ArrayList<>(databaseHelper.getShoppingList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShoppingListAdapter(this, this, shoppingList);
        recyclerView.setAdapter(adapter);
    }

    public void showCreateDialog() {
        final EditText name, description;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sholist_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.sholist_createform_text_name);
        dateTextView = dialog.findViewById(R.id.sholist_createform_text_date);
        description = (EditText) dialog.findViewById(R.id.sholist_createform_text_description);
        submitCreate = (Button) dialog.findViewById(R.id.sholist_createform_submit);

        dateTextView.setText(databaseHelper.getCurrentDate());

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
                new DatePickerDialog(ShoppingList.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ShoppingModel shoppingModel = new ShoppingModel();

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę listy zakupów.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{2,30}")) {
                    name.setError("Nazwa listy powinna składać się z co najmniej dwóch liter.");
                /*
                } else if (databaseHelper.checkExistingExpenseCategoryName(name.getText().toString())) {
                    System.out.println("Name: " + name.getText().toString());
                    name.setError("Istnieje już kategoria z podaną nazwą.");
                */
                } else {
                    shoppingModel.setShoName(name.getText().toString());
                    shoppingModel.setShoDate(dateTextView.getText().toString());
                    shoppingModel.setShoDesc(description.getText().toString());
                    databaseHelper.addShopping(shoppingModel);

                    dialog.cancel();
                    displayShoppingsList();
                }
            }
        });
    }
}