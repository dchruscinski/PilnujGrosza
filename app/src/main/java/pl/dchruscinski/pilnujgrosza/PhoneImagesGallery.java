package pl.dchruscinski.pilnujgrosza;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneImagesGallery extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    Cursor imagesCursor;
    GridView gridView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_images_gallery);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            externalStorageRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            listImages();
        }
    }

    ActivityResultLauncher<String> externalStorageRequestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    listImages();
                } else {
                    Toast.makeText(getApplicationContext(), "Nie można wyświetlić listy zdjęć bez uprawnień.", Toast.LENGTH_LONG).show();
                }
            }
    );

    private void listImages() {
        gridView = (GridView) findViewById(R.id.phone_images_gallery_view);
        String[] projection = {MediaStore.Images.Thumbnails.DATA};
        imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        gridView.setAdapter(new PhoneImagesGalleryAdapter(getApplicationContext(), imagesCursor));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imagesCursor.moveToPosition(position);
                String path = imagesCursor.getString(0);
                makeActionWithChosenImage(path);
            }
        });
    }

    private void makeActionWithChosenImage(String pathToImage) {
        if (getIntent().getExtras() != null 
                && getIntent().getStringExtra("action").equals("create")) {
            Intent intent = new Intent(getApplicationContext(), ReceiptList.class)
                    .putExtra("action","create")
                    .putExtra("path", pathToImage);
            startActivity(intent);
        } else if (getIntent().getStringExtra("action").equals("edit")) {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            ReceiptModel receiptModel = new ReceiptModel();
            receiptModel.setRecImg(pathToImage);
            databaseHelper.updateReceiptImage(getIntent().getExtras().getInt("recID"), receiptModel);

            Intent intent = new Intent(getApplicationContext(), Receipt.class)
                    .putExtra("recID", getIntent().getExtras().getInt("recID"))
                    .putExtra("position", getIntent().getExtras().getInt("position"));
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
        }
    }

}