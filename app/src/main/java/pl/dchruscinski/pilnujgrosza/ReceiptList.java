package pl.dchruscinski.pilnujgrosza;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class ReceiptList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ReceiptModel> receiptList;
    FloatingActionButton addReceiptMenuFAB, addReceiptFromCameraFAB, addReceiptFromFileFAB;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    DatabaseHelper databaseHelper;
    Uri imageUri;
    ActivityResultLauncher cropActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiptlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null
                && getIntent().getStringExtra("path") != null) {
            showCreateDialog(getIntent().getStringExtra("path"));
        } else if (getIntent().getExtras() != null
                && getIntent().getStringExtra("source").equals("menu")) {
            showCreateMenuDialog();
        }

        databaseHelper = new DatabaseHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.receipt_list_rc);
        addReceiptMenuFAB = (FloatingActionButton) findViewById(R.id.receiptlist_fab_menu);
        addReceiptFromCameraFAB = (FloatingActionButton) findViewById(R.id.receiptlist_fab_photo);
        addReceiptFromFileFAB = (FloatingActionButton) findViewById(R.id.receiptlist_fab_fromFile);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        displayReceiptsList();

        addReceiptMenuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addReceiptFromFileFAB.getVisibility() == View.GONE || addReceiptFromCameraFAB.getVisibility() == View.GONE) {
                    addReceiptFromFileFAB.setVisibility(View.VISIBLE);
                    addReceiptFromCameraFAB.setVisibility(View.VISIBLE);

                    addReceiptFromFileFAB.startAnimation(fromBottom);
                    addReceiptFromCameraFAB.startAnimation(fromBottom);
                    addReceiptMenuFAB.startAnimation(rotateOpen);
                } else {
                    addReceiptFromFileFAB.setVisibility(View.GONE);
                    addReceiptFromCameraFAB.setVisibility(View.GONE);

                    addReceiptFromFileFAB.startAnimation(toBottom);
                    addReceiptFromCameraFAB.startAnimation(toBottom);
                    addReceiptMenuFAB.startAnimation(rotateClose);
                }
            }
        });

        addReceiptFromCameraFAB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });

        addReceiptFromFileFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
    }

    public void displayReceiptsList() {
        receiptList = new ArrayList<>(databaseHelper.getReceiptList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ReceiptAdapter(this, this, receiptList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sort_action_sort:
                View sortMenuItemView = findViewById(R.id.menu_sort_action_sort);
                PopupMenu popup = new PopupMenu(this, sortMenuItemView);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_sort_items_name_and_date, popup.getMenu());
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.menu_sort_action_sort_name_asc:
                            Collections.sort(receiptList, ReceiptModel.receiptNameAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_name_desc:
                            Collections.sort(receiptList, ReceiptModel.receiptNameDescComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_asc:
                            Collections.sort(receiptList, ReceiptModel.receiptDateAscComparator);
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.menu_sort_action_sort_date_desc:
                            Collections.sort(receiptList, ReceiptModel.receiptDateDescComparator);
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

    public void showCreateDialog(String pathToFile) {
        final EditText name, description;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.receipt_createform_text_name);
        dateTextView = dialog.findViewById(R.id.receipt_createform_text_date);
        description = (EditText) dialog.findViewById(R.id.receipt_createform_text_description);
        submitCreate = (Button) dialog.findViewById(R.id.receipt_createform_submit);

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
                new DatePickerDialog(ReceiptList.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ReceiptOCR receiptOCR = new ReceiptOCR(getApplicationContext());
                ReceiptModel receiptModel = new ReceiptModel();

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.receipt_createreceipt_name_error_empty));
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{2,30}")) {
                    name.setError(getString(R.string.receipt_createreceipt_name_error_syntax));
                } else {
                    receiptModel.setRecName(name.getText().toString());
                    receiptModel.setRecDate(dateTextView.getText().toString());
                    receiptModel.setRecDesc(description.getText().toString());
                    receiptModel.setRecImg(pathToFile);
                    receiptModel.setRecData(receiptOCR.recognizeReceipt(pathToFile));
                    databaseHelper.addReceipt(receiptModel);

                    dialog.cancel();
                    displayReceiptsList();
                }
            }
        });
    }

    public void showCreateMenuDialog() {
        final Button addReceiptFromCamera, addReceiptFromFile;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_create_menu);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        addReceiptFromCamera = (Button) dialog.findViewById(R.id.receipt_create_menu_newImageFromCamera);
        addReceiptFromFile = (Button) dialog.findViewById(R.id.receipt_create_menu_newImageFromFile);

        addReceiptFromCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getImageFromCamera();
            }
        });

        addReceiptFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getImageFromGallery();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getImageFromCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            useCamera();
        }
    }

    public void useCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "New picture");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraResultLauncher.launch(cameraIntent);
    }

    private void getImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryResultLauncher.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        CropImage.activity(imageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ReceiptList.this);

                        /*
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap takenPhoto = (Bitmap) extras.get("data");

                        File photoFile = null;
                        try {
                            photoFile = createImageFile(getApplicationContext());
                        } catch (IOException e) {
                            // Error occurred while creating the File
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            try {
                                FileOutputStream outStream = new FileOutputStream(photoFile);
                                takenPhoto.compress(Bitmap.CompressFormat.PNG, 0, outStream);
                                outStream.flush();
                                outStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // takenPhotoImageView.setImageBitmap(takenPhoto);
                            String pathToFile = photoFile.getAbsolutePath();
                            showCreateDialog(pathToFile);
                        }

                         */
                    }
                }
            });

    ActivityResultLauncher<String> cameraRequestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    useCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Nie można wykonać zdjęcia bez uprawnień.", Toast.LENGTH_LONG).show();
                }
            }
    );

    ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        CropImage.activity(data.getData())
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ReceiptList.this);
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap takenPhoto = null;
                try {
                    takenPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File photoFile = null;
                try {
                    photoFile = createImageFile(getApplicationContext());
                } catch (IOException e) {
                    // Error occurred while creating the File
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    try {
                        FileOutputStream outStream = new FileOutputStream(photoFile);
                        takenPhoto.compress(Bitmap.CompressFormat.PNG, 0, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // takenPhotoImageView.setImageBitmap(takenPhoto);
                    String pathToFile = photoFile.getAbsolutePath();
                    showCreateDialog(pathToFile);
                }
            }
        }
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date());
        String imageFileName = "PilnujGrosza_JPEG_" + timeStamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public byte[] getBytesFromTakenPhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}