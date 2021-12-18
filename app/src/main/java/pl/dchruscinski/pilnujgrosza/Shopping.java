package pl.dchruscinski.pilnujgrosza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;


public class Shopping extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    public TextView shoListName, shoListDesc, shoListDate, shoListValue, shoListNumOfProd, shoListCurrency;
    Button editShoList, deleteShoList;
    BigDecimal prodValue;
    private ArrayList<ShoppingModel> shoppingList;
    private ArrayList<ShoppingContentModel> shoppingContentList;
    FloatingActionButton addShoContFAB;
    private RecyclerView recyclerView;
    private ShoppingContentAdapter adapter;
    private int shoID, position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        adapter = new ShoppingContentAdapter(this, this, shoppingContentList);

        databaseHelper = new DatabaseHelper(this);
        shoID = getIntent().getExtras().getInt("shoID");
        position = getIntent().getExtras().getInt("position");

        recyclerView = (RecyclerView) findViewById(R.id.shocont_list_rc);
        editShoList = (Button) findViewById(R.id.shopping_button_edit);
        deleteShoList = (Button) findViewById(R.id.shopping_button_delete);
        addShoContFAB = (FloatingActionButton) findViewById(R.id.shopping_fab_addShoCont);

        shoListName = (TextView) findViewById(R.id.shopping_rc_name);
        shoListDesc = (TextView) findViewById(R.id.shopping_rc_desc);
        shoListDate = (TextView) findViewById(R.id.shopping_rc_date);
        shoListValue = (TextView) findViewById(R.id.shopping_rc_value);
        shoListNumOfProd = (TextView) findViewById(R.id.shopping_rc_num_of_prod);
        shoListCurrency = (TextView) findViewById(R.id.shopping_rc_currency);

        // sprawdzić czemu nie mogę podać pozycji zamiast shoID

        displayShopping();
        displayShoppingContentList();

        editShoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditShoppingDialog(position);
            }
        });

        deleteShoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteShoppingDialog(position);
            }
        });

        addShoContFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShoppingContentDialog();
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
                inflater.inflate(R.menu.menu_sort_items_name_value_unit, popup.getMenu());
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.menu_sort_action_sort_name_asc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContNameAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_name_desc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContNameDescComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_value_asc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContValueAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_value_desc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContValueDescComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_unit_asc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContUnitAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_unit_desc:
                            Collections.sort(shoppingContentList, ShoppingContentModel.shoppingContUnitDescComparator);
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

    public void displayShopping() {
        shoListName.setText(String.valueOf(databaseHelper.getShopping(shoID).getShoName()));
        shoListDesc.setText(String.valueOf(databaseHelper.getShopping(shoID).getShoDesc()));
        shoListDate.setText(String.valueOf(databaseHelper.getShopping(shoID).getShoDate()));

        prodValue = BigDecimal.valueOf(databaseHelper.getShopping(shoID).getShoValue()).divide(BigDecimal.valueOf(100));
        shoListValue.setText(prodValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(prodValue) : new DecimalFormat("#.##").format(prodValue));

        shoListNumOfProd.setText(String.valueOf(databaseHelper.getNumberOfProductsInShoppingList(shoID)));
        shoListCurrency.setText(databaseHelper.getCurrency(chosenProfileID));
    }

    public void displayShoppingContentList() {
        shoppingContentList = new ArrayList<>(databaseHelper.getShoppingContentForShoppingList(shoID));
        shoppingList = new ArrayList<>(databaseHelper.getShoppingList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShoppingContentAdapter(this, this, shoppingContentList);
        recyclerView.setAdapter(adapter);
    }

    public void showEditShoppingDialog(final int position) {
        final EditText name, description;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitEdit;

        final Dialog dialog = new Dialog(Shopping.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sholist_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.sholist_editform_text_name);
        dateTextView = dialog.findViewById(R.id.sholist_editform_text_date);
        description = (EditText) dialog.findViewById(R.id.sholist_editform_text_description);
        submitEdit = (Button) dialog.findViewById(R.id.sholist_editform_submit);

        name.setText(String.valueOf(shoppingList.get(position).getShoName()));
        dateTextView.setText(String.valueOf(shoppingList.get(position).getShoDate()));
        description.setText(String.valueOf(shoppingList.get(position).getShoDesc()));

        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
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
                new DatePickerDialog(v.getRootView().getContext(), startDate,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(getApplicationContext());
                ShoppingModel shoppingModel = new ShoppingModel();
                int intAmount = 0;
                boolean isAmountValid = false;

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
                    databaseHelper.updateShopping(shoppingList.get(position).getShoID(), shoppingModel);

                    shoppingList.get(position).setShoName(name.getText().toString());
                    shoppingList.get(position).setShoDate(dateTextView.getText().toString());
                    shoppingList.get(position).setShoDesc(description.getText().toString());

                    dialog.cancel();
                    displayShopping();
                    displayShoppingContentList();
                }
            }
        });
    }

    public void showDeleteShoppingDialog(final int position) {
        final TextView name, dateTextView, description;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(getApplicationContext());

        final Dialog dialog = new Dialog(Shopping.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sholist_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.sholist_deleteform_info_name);
        dateTextView = (TextView) dialog.findViewById(R.id.sholist_deleteform_info_date);
        description = (TextView) dialog.findViewById(R.id.sholist_deleteform_info_desc);
        submitDelete = (Button) dialog.findViewById(R.id.sholist_deleteform_submit);

        name.setText(shoppingList.get(position).getShoName());
        dateTextView.setText(shoppingList.get(position).getShoDate());
        description.setText(shoppingList.get(position).getShoDesc());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteShopping(shoppingList.get(position).getShoID());
                shoppingList.remove(position);

                dialog.cancel();
                displayShoppingContentList();

                startActivity(new Intent(v.getContext(), ShoppingList.class));
            }
        });

    }

    public void showCreateShoppingContentDialog() {
        final EditText name, amount, unit, value;
        final TextView currency;
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shocont_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.shocont_createform_text_name);
        amount = (EditText) dialog.findViewById(R.id.shocont_createform_text_amount);
        unit = (EditText) dialog.findViewById(R.id.shocont_createform_text_unit);
        value = (EditText) dialog.findViewById(R.id.shocont_createform_text_value);
        currency = (TextView) dialog.findViewById(R.id.shocont_createform_info_currency);
        submitCreate = (Button) dialog.findViewById(R.id.shocont_createform_submit);

        // default value for amount and unit
        amount.setText("1");
        unit.setText(R.string.shocont_createform_unit_defvalue);

        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ShoppingContentModel shoppingContentModel = new ShoppingContentModel();
                int intValue = 0;
                boolean isValueValid = false;

                if (!value.getText().toString().trim().isEmpty()) {
                    String stringValue = value.getText().toString().trim();
                    if (stringValue.contains(".")) {
                        if (stringValue.substring(stringValue.indexOf(".") + 1).length() > 2 || !(value.getText().toString().trim().length() < 8)) {
                            isValueValid = false;
                        } else {
                            isValueValid = true;
                            BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else if (value.getText().toString().trim().length() < 5) {
                        isValueValid = true;
                        BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                } else {
                    isValueValid = true;
                }

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.shocont_createshocont_name_error_empty));
                } else if (!name.getText().toString().matches("[\\sa-zA-ZąĄćĆęĘłŁńŃóÓśŚźŹżŻ;:%\\-,.\\d]{2,25}")) {
                    name.setError(getString(R.string.shocont_createshocont_name_error_syntax));
                } else if (databaseHelper.checkExistingProductNameInShoppingList(shoID, 0, name.getText().toString())) {
                    name.setError(getString(R.string.shocont_createshocont_name_error_existing));
                } else if (amount.getText().toString().trim().isEmpty()) {
                    amount.setError(getString(R.string.shocont_createshocont_amount_error_empty));
                } else if (!amount.getText().toString().matches("[.,0-9]{1,4}")) {
                    amount.setError(getString(R.string.shocont_createshocont_amount_error_syntax));
                } else if (unit.getText().toString().trim().isEmpty()) {
                    unit.setError(getString(R.string.shocont_createshocont_unit_error_empty));
                } else if (!unit.getText().toString().matches("[a-zA-ZąĄćĆęĘłŁńŃóÓśŚźŹżŻ;:,.\\-]{1,5}")) {
                    unit.setError(getString(R.string.shocont_createshocont_unit_error_syntax));
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych, maksymalnie 7 znaków.");
                } else {
                    shoppingContentModel.setShoContName(name.getText().toString());
                    shoppingContentModel.setShoContAmount(amount.getText().toString());
                    shoppingContentModel.setShoContUnit(unit.getText().toString());
                    shoppingContentModel.setShoContValue(intValue);
                    databaseHelper.addShoppingContent(shoID, shoppingContentModel);

                    dialog.cancel();
                    displayShopping();
                    displayShoppingContentList();
                }
            }
        });
    }

}