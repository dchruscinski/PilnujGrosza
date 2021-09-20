package pl.dchruscinski.pilnujgrosza;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Objects;

public class ReceiptImage extends AppCompatActivity {
    int recID, position;
    String pathToReceiptImage;
    ImageView fullScreenReceiptImage;
    DatabaseHelper databaseHelper;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_image);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recID = getIntent().getExtras().getInt("recID");
        position = getIntent().getExtras().getInt("position");
        databaseHelper = new DatabaseHelper(getApplicationContext());
        fullScreenReceiptImage = (ImageView) findViewById(R.id.receipt_image_imageview);

        pathToReceiptImage = databaseHelper.getReceipt(recID).getRecImg();
        imageFile = new File(pathToReceiptImage);
        Glide.with(this)
                .load(Uri.fromFile(imageFile))
                .placeholder(R.drawable.baseline_photo_camera_24)
                .into(fullScreenReceiptImage);
    }
}