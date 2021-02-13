package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Budget extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
    }

    public void onClickIncomeButton(View view) {
        startActivity(new Intent(this, IncomeList.class));
    }

    public void onClickExpensesButton(View view) {
        startActivity(new Intent(this, ExpensesList.class));
    }

    public void onClickSummaryButton(View view) {
        startActivity(new Intent(this, Summary.class));
    }
}