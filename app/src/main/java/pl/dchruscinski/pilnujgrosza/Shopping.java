package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Shopping extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClickShoppingListsButton(View view) {
        startActivity(new Intent(this, ShoppingList.class));
    }

    public void onClickReceiptsButton(View view) {
        startActivity(new Intent(this, Receipt.class));
    }
}