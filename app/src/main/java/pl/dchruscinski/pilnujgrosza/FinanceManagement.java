package pl.dchruscinski.pilnujgrosza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class FinanceManagement extends AppCompatActivity {
    Button budgetButton, transactionsListButton, scheduledPaymentsButton, incomeCategoriesButton, expenseCategoriesButton, calendarButton, statisticsButton;
    TextView actualBudget;
    // ArrayList<ProfileModel> profilesList;
    DatabaseHelper databaseHelper;
    // BigDecimal balanceAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_mgmt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        budgetButton = (Button) findViewById(R.id.finmgmt_budgetmenu_button);
        transactionsListButton = (Button) findViewById(R.id.finmgmt_transactionsList_button);
        scheduledPaymentsButton = (Button) findViewById(R.id.finmgmt_schpay_button);
        incomeCategoriesButton = (Button) findViewById(R.id.finmgmt_inccat_button);
        expenseCategoriesButton = (Button) findViewById(R.id.finmgmt_expcat_button);
        statisticsButton = (Button) findViewById(R.id.finmgmt_statistics_button);

        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Budget.class));
            }
        });

        transactionsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Transaction.class));
            }
        });

        scheduledPaymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ScheduledPayment.class));
            }
        });

        incomeCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), IncomeCategory.class));
            }
        });

        expenseCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ExpenseCategory.class));
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Statistics.class));
            }
        });
    }
}