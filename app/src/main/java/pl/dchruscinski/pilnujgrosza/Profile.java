package pl.dchruscinski.pilnujgrosza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Set;

public class Profile extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<ProfileModel> profilesList;
    FloatingActionButton addProfileFAB;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PilnujGrosza);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recyclerView = (RecyclerView) findViewById(R.id.profile_list_rc);
        addProfileFAB = (FloatingActionButton) findViewById(R.id.profile_fab_addProfile);
        databaseHelper = new DatabaseHelper(this);
        displayProfilesList();

        addProfileFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog();
            }
        });
    }

    //display notes list
    public void displayProfilesList() {
        profilesList = new ArrayList<>(databaseHelper.getProfileList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ProfileAdapter adapter = new ProfileAdapter(this, this, profilesList);
        recyclerView.setAdapter(adapter);
    }

    public void showCreateDialog() {
        final EditText name, PIN, PINConfirm, initialBalance, currency;
        Button nextStageButton;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_create_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.profile_createform_text_name);
        PIN = (EditText) dialog.findViewById(R.id.profile_createform_text_pin);
        PINConfirm = (EditText) dialog.findViewById(R.id.profile_createform_text_pin_confirm);
        initialBalance = (EditText) dialog.findViewById(R.id.profile_createform_text_initialBalance);
        currency = (EditText) dialog.findViewById(R.id.profile_createform_text_currency);
        nextStageButton = (Button) dialog.findViewById(R.id.profile_createform_button_nextstage);

        nextStageButton.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                // hashing password
                String hashSalt = BCrypt.gensalt();
                String hashedPIN = BCrypt.hashpw(PIN.getText().toString(), hashSalt);
                String hashedPINConfirm = BCrypt.hashpw(PINConfirm.getText().toString(), hashSalt);
                boolean passwordMatches = hashedPIN.equals(hashedPINConfirm);
                boolean isInitialBalanceValid = false;

                String profName = name.getText().toString().trim();
                int profInitialBalance = 0;
                int profBalance = 0;

                String profCurrency = currency.getText().toString().trim();
                if (currency.getText().toString().trim().isEmpty()) profCurrency = "zł";

                if (!initialBalance.getText().toString().trim().isEmpty()) {
                    String stringInitialBalance = initialBalance.getText().toString().trim();
                    if (stringInitialBalance.contains(".")) {
                        if (stringInitialBalance.substring(stringInitialBalance.indexOf(".") + 1).length() > 2) {
                            isInitialBalanceValid = false;
                        } else {
                            isInitialBalanceValid = true;
                            BigDecimal bigDecimalInitialBalance = new BigDecimal(stringInitialBalance.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            profInitialBalance = (bigDecimalInitialBalance.multiply(BigDecimal.valueOf(100))).intValueExact();
                            profBalance = profInitialBalance;
                        }
                    } else {
                        isInitialBalanceValid = true;
                        BigDecimal bigDecimalInitialBalance = new BigDecimal(stringInitialBalance.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        profInitialBalance = (bigDecimalInitialBalance.multiply(BigDecimal.valueOf(100))).intValueExact();
                        profBalance = profInitialBalance;
                    }
                } else {
                    isInitialBalanceValid = true;
                }

                if (!passwordMatches) {
                    PIN.setError(getString(R.string.profile_createprofile_pin_error_passwordMismatch));
                } else if (PIN.getText().toString().isEmpty() || PINConfirm.getText().toString().isEmpty()) {
                    PIN.setError(getString(R.string.profile_createprofile_pin_error_empty));
                } else if (PIN.getText().toString().length() < 6 || PINConfirm.getText().toString().length() < 6) {
                    PIN.setError(getString(R.string.profile_createprofile_pin_error_insufficientChars));
                } else if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.profile_createprofile_name_error_empty));
                } else if (!name.getText().toString().matches("[a-zA-ZąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{2,20}")) {
                    name.setError("Nazwa profilu powinna składać się z co najmniej dwóch liter. Niedozwolone są cyfry oraz znaki specjalne.");
                } else if (databaseHelper.checkExistingProfileName(0, name.getText().toString())) {
                    name.setError(getString(R.string.profile_createprofile_name_error_existingName));
                } else if (!isInitialBalanceValid) {
                    initialBalance.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych.");
                } else if (currency.getText().toString().trim().matches("\\d+")) {
                    currency.setError("Wartość nie może być wyrażona w cyfrach.");
                } else if (!currency.getText().toString().trim().matches("[a-zA-ZąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{0,4}")) {
                    currency.setError(getString(R.string.profile_createprofile_currency_error_tooMuchChars));
                } else {
                    dialog.cancel();
                    displayProfilesList();
                    showHelperQuestionAndAnswerDialog(profName, hashedPIN, hashSalt, profInitialBalance, profBalance, profCurrency);
                }
            }
        });

    }

    public void showHelperQuestionAndAnswerDialog(String profName, String hashedPIN, String hashSalt, int profInitialBalance, int profBalance, String profCurrency) {
        final EditText question, answer;
        Button submitCreate;
        databaseHelper = new DatabaseHelper(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_create_form_remind_password);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        question = (EditText) dialog.findViewById(R.id.profile_createform_text_helperquestion_question);
        answer = (EditText) dialog.findViewById(R.id.profile_createform_text_helperquestion_answer);
        submitCreate = (Button) dialog.findViewById(R.id.profile_createform_submitProfile);

        submitCreate.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ProfileModel profileModel = new ProfileModel();

                String helperHashSalt = BCrypt.gensalt();
                String helperQuestion = question.getText().toString().trim();
                String helperHashedAnswer = BCrypt.hashpw(answer.getText().toString().trim(), helperHashSalt);

                if (question.getText().toString().trim().isEmpty()) {
                    question.setError(getString(R.string.profile_createprofile_question_error_empty));
                } else if (answer.getText().toString().trim().isEmpty()) {
                    answer.setError(getString(R.string.profile_createprofile_answer_error_empty));
                } else if (!question.getText().toString().trim().matches("[\\sa-zA-Z0-9_.,-ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]{2,128}[?]")) {
                    question.setError(getString(R.string.profile_createprofile_question_error_syntax));
                } else {
                    profileModel.setProfName(profName);
                    profileModel.setProfPIN(hashedPIN);
                    profileModel.setProfPINSalt(hashSalt);
                    profileModel.setProfInitialBalance(profInitialBalance);
                    profileModel.setProfBalance(profBalance);
                    profileModel.setProfHelperQuestion(helperQuestion);
                    profileModel.setProfHelperAnswer(helperHashedAnswer);
                    profileModel.setProfHelperSalt(helperHashSalt);
                    databaseHelper.addProfile(profileModel, profCurrency);

                    dialog.cancel();
                    displayProfilesList();
                }
            }
        });

    }

}


        /*
        use this setting to improve performance if you know that changes in content
        do not change the layout size of the RecyclerView
        */
        /*
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProfileDatabaseHelper profileDatabaseHelper = new ProfileDatabaseHelper(Profile.this);
        List<ProfileModel> profilesList = profileDatabaseHelper.getProfileList();

        List<String> profilesList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            profilesList.add("Test" + i);
        }
        */

        /*
        // put this after your definition of your recyclerview
        // input in your data mode in this example a java.util.List, adjust if necessary
        // adapter is your adapter
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        profilesList.remove(viewHolder.getAdapterPosition());
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
    */