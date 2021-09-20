package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfilePosition;

public class MainMenu extends AppCompatActivity {
    Button addIncomeButton, addExpenseButton, financeManagementButton, addReceiptButton, receiptsListButton,
            addShoppingListButton, shoppingListsButton, settingsButton, logoutButton;
    TextView profileName, balance, currency;
    ArrayList<ProfileModel> profilesList;
    ArrayList<SettingsModel> settingsList;
    DatabaseHelper databaseHelper;
    BigDecimal balanceAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            Toast.makeText(this, "Zalogowano jako " +
                    getIntent().getExtras().getString("name") + ".", Toast.LENGTH_SHORT).show();
        }

        databaseHelper = new DatabaseHelper(this);
        profilesList = new ArrayList<>(databaseHelper.getProfileList());
        settingsList = new ArrayList<>(databaseHelper.getSettingsList());

        profileName = (TextView) findViewById(R.id.mainmenu_profile_text);
        balance = (TextView) findViewById(R.id.mainmenu_balance_amount_text);
        currency = (TextView) findViewById(R.id.mainmenu_balance_currency_text);

        addIncomeButton = (Button) findViewById(R.id.mainmenu_addIncome_button);
        addExpenseButton = (Button) findViewById(R.id.mainmenu_addExpense_button);
        financeManagementButton = (Button) findViewById(R.id.mainmenu_finMgmt_button);
        addReceiptButton = (Button) findViewById(R.id.mainmenu_addReceipt_button);
        receiptsListButton = (Button) findViewById(R.id.mainmenu_receiptsList_button);
        addShoppingListButton = (Button) findViewById(R.id.mainmenu_addShoppingList_button);
        shoppingListsButton = (Button) findViewById(R.id.mainmenu_shoppingLists_button);
        settingsButton = (Button) findViewById(R.id.mainmenu_settings_button);
        logoutButton = (Button) findViewById(R.id.mainmenu_logout_button);

        profileName.setText(profilesList.get(chosenProfilePosition).getProfName());

        balanceAmount = BigDecimal.valueOf(profilesList.get(chosenProfilePosition).getProfBalance()).divide(BigDecimal.valueOf(100));
        balance.setText(balanceAmount.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(balanceAmount) : new DecimalFormat("#.##").format(balanceAmount));

        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        boolean isDarkModeOn = databaseHelper.getTheme(chosenProfileID);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Transaction.class).putExtra("action","incomeButton");
                startActivity(intent);
            }
        });

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Transaction.class).putExtra("action","expenseButton");
                startActivity(intent);
            }
        });

        financeManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), FinanceManagement.class));
            }
        });

        addReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReceiptList.class)
                        .putExtra("source","menu");
                startActivity(intent);
            }
        });

        receiptsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ReceiptList.class));
            }
        });

        addShoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingList.class)
                        .putExtra("action","shoppingListButton");
                startActivity(intent);
            }
        });

        shoppingListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ShoppingList.class));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Settings.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(v.getContext(), Profile.class));
                finish();
            }
        });
    }
}