package pl.dchruscinski.pilnujgrosza;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class Receipt extends AppCompatActivity {
    private TextView recName, recDate, recDesc, recData;
    private Button editReceipt, deleteReceipt;
    private ImageView recImg;
    private ArrayList<ReceiptModel> receiptList;
    private DatabaseHelper databaseHelper;
    private Uri imageUri;
    private int recID, position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        recID = getIntent().getExtras().getInt("recID");
        position = getIntent().getExtras().getInt("position");

        editReceipt = (Button) findViewById(R.id.receipt_button_edit);
        deleteReceipt = (Button) findViewById(R.id.receipt_button_delete);

        recName = (TextView) findViewById(R.id.receipt_text_name);
        recDesc = (TextView) findViewById(R.id.receipt_text_desc);
        recDate = (TextView) findViewById(R.id.receipt_text_date);
        recData = (TextView) findViewById(R.id.receipt_text_data);
        recImg = (ImageView) findViewById(R.id.receipt_img);

        displayReceipt();

        editReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditMenuDialog(position);
            }
        });

        deleteReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteReceiptDialog(position);
            }
        });

        recImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReceiptImage.class)
                        .putExtra("recID", recID)
                        .putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public void displayReceipt() {
        receiptList = new ArrayList<>(databaseHelper.getReceiptList());

        recName.setText(String.valueOf(receiptList.get(position).getRecName()));
        recDesc.setText(String.valueOf(receiptList.get(position).getRecDesc()));
        recDate.setText(String.valueOf(receiptList.get(position).getRecDate()));

        String receiptData = (String.valueOf(receiptList.get(position).getRecData()).equals("null")) ?
                "Brak danych" : String.valueOf(receiptList.get(position).getRecData());

        recData.setText(receiptData);

        File imageFile = new File(String.valueOf(receiptList.get(position).getRecImg()));
        Glide.with(this)
                .load(Uri.fromFile(imageFile))
                .centerCrop()
                .placeholder(R.drawable.baseline_photo_camera_24)
                .into(recImg);

    }

    public void showEditMenuDialog(final int position) {
        Button editAttributes, editData, recognizeDataFromReceipt, newImageFromFile, newImageFromCamera;

        final Dialog dialog = new Dialog(Receipt.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_edit_menu);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        editAttributes = (Button) dialog.findViewById((R.id.receipt_edit_menu_editAttributes));
        editData = (Button) dialog.findViewById((R.id.receipt_edit_menu_editData));
        recognizeDataFromReceipt = (Button) dialog.findViewById((R.id.receipt_edit_menu_recognizeDataFromReceipt));
        newImageFromCamera = (Button) dialog.findViewById((R.id.receipt_edit_menu_newImageFromCamera));
        newImageFromFile = (Button) dialog.findViewById((R.id.receipt_edit_menu_newImageFromFile));

        editAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showEditReceiptAttributesDialog(position);
            }
        });

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showEditReceiptDataDialog(position);
            }
        });

        recognizeDataFromReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                recognizeDataFromReceipt(String.valueOf(receiptList.get(position).getRecImg()));
            }
        });

        newImageFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getImageFromGallery();
            }
        });

        newImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getImageFromCamera();
            }
        });
    }

    public void showEditReceiptAttributesDialog(final int position) {
        final EditText name, description;
        final TextView dateTextView;
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button submitEdit;

        final Dialog dialog = new Dialog(Receipt.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_edit_attributes_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.receipt_editAttributes_form_text_name);
        dateTextView = dialog.findViewById(R.id.receipt_editAttributes_form_text_date);
        description = (EditText) dialog.findViewById(R.id.receipt_editAttributes_form_text_description);
        submitEdit = (Button) dialog.findViewById(R.id.receipt_editAttributes_form_submit);

        name.setText(String.valueOf(receiptList.get(position).getRecName()));
        dateTextView.setText(String.valueOf(receiptList.get(position).getRecDate()));
        description.setText(String.valueOf(receiptList.get(position).getRecDesc()));

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
                ReceiptModel receiptModel = new ReceiptModel();

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę paragonu.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:,-ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{2,30}")) {
                    name.setError("Nazwa paragonu powinna składać się z co najmniej dwóch liter.");
                } else {
                    receiptModel.setRecName(name.getText().toString());
                    receiptModel.setRecDate(dateTextView.getText().toString());
                    receiptModel.setRecDesc(description.getText().toString());
                    databaseHelper.updateReceiptAttributes(receiptList.get(position).getRecID(), receiptModel);

                    receiptList.get(position).setRecName(name.getText().toString());
                    receiptList.get(position).setRecDate(dateTextView.getText().toString());
                    receiptList.get(position).setRecDesc(description.getText().toString());

                    dialog.cancel();
                    displayReceipt();
                }
            }
        });
    }

    public void showEditReceiptDataDialog(final int position) {
        final EditText data;
        Button submitEdit;

        final Dialog dialog = new Dialog(Receipt.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_edit_data_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        data = (EditText) dialog.findViewById(R.id.receipt_editData_form_text_data);
        submitEdit = (Button) dialog.findViewById(R.id.receipt_editData_form_submit);

        data.setText(String.valueOf(receiptList.get(position).getRecData()));

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(getApplicationContext());
                databaseHelper.updateReceiptData(receiptList.get(position).getRecID(), data.getText().toString());

                receiptList.get(position).setRecData(data.getText().toString());

                dialog.cancel();
                displayReceipt();
            }
        });
    }

    public void showDeleteReceiptDialog(final int position) {
        final TextView name, dateTextView, description;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(getApplicationContext());

        final Dialog dialog = new Dialog(Receipt.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.receipt_deleteform_info_name);
        dateTextView = (TextView) dialog.findViewById(R.id.receipt_deleteform_info_date);
        description = (TextView) dialog.findViewById(R.id.receipt_deleteform_info_desc);
        submitDelete = (Button) dialog.findViewById(R.id.receipt_deleteform_submit);

        name.setText(receiptList.get(position).getRecName());
        dateTextView.setText(receiptList.get(position).getRecDate());
        description.setText(receiptList.get(position).getRecDesc());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteReceipt(receiptList.get(position).getRecID());
                receiptList.remove(position);

                dialog.cancel();

                startActivity(new Intent(v.getContext(), ReceiptList.class));
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
                                .start(Receipt.this);
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
                                .start(Receipt.this);
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
                    photoFile = ReceiptList.createImageFile(getApplicationContext());
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

                    ReceiptModel receiptModel = new ReceiptModel();
                    receiptModel.setRecImg(pathToFile);
                    databaseHelper.updateReceiptImage(receiptList.get(position).getRecID(), receiptModel);

                    receiptList.get(position).setRecImg(pathToFile);

                    displayReceipt();
                }
            }
        }
    }

    private void recognizeDataFromReceipt(String pathToImage) {
        ReceiptOCR receiptOCR = new ReceiptOCR(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());
        String resultOfOCR = "";

        resultOfOCR = receiptOCR.recognizeReceipt(pathToImage);
        databaseHelper.updateReceiptData(recID, resultOfOCR);

        displayReceipt();
    }

}