package com.example.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Zalogowano jako " +
                getIntent().getExtras().getString("name") + ".", Toast.LENGTH_SHORT).show();
    }

    public void onClickBudgetButton(View view) {
        startActivity(new Intent(this, Budget.class));
    }

    public void onClickShoppingButton(View view) {
        startActivity(new Intent(this, Shopping.class));
    }

    public void onClickCalendarButton(View view) {
        startActivity(new Intent(this, Calendar.class));
    }

    public void onClickProfileButton(View view) {
        startActivity(new Intent(this, Profile.class));
    }

    public void onClickSettingsButton(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}