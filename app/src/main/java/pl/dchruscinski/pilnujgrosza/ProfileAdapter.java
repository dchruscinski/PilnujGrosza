package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProfileModel> profilesList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;
    static int chosenProfileID = 0;
    static int chosenProfilePosition = 0;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public ProfileAdapter(Context context, Activity activity, ArrayList<ProfileModel> profilesList) {
        this.context = context;
        this.activity  = activity ;
        this.profilesList = profilesList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolderEmpty extends RecyclerView.ViewHolder {

        public TextView emptyText;
        public ImageView emptyIcon;
        public View layout;

        public ViewHolderEmpty(View v) {
            super(v);
            layout = v;
            emptyText = (TextView) v.findViewById(R.id.profile_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.profile_empty_logo);
        }
    }

    public class ViewHolderProfile extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter;
        public ImageView editProfile;
        public View layout;

        public ViewHolderProfile(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.prof_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.prof_rc_secondLine);
            editProfile = (ImageView) v.findViewById(R.id.prof_rc_editicon);
        }
    }

    @Override
    public int getItemCount() {
        return profilesList.size() > 0 ? profilesList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (profilesList.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            // create a new view
            v = inflater.inflate(R.layout.profile_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderProfile(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.profile_empty_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderEmpty(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*
            - get element from your dataset at this position
            - replace the contents of the view with that element
         */
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            final ProfileModel profile = profilesList.get(position);
            ViewHolderProfile profileHolder = (ViewHolderProfile) holder;

            profileHolder.txtHeader.setText(profile.getProfName());

            String lastLoginDate = (profilesList.get(position).getProfLastLoginDate() == null) ?
                    "" : "Ostatnie logowanie: " + profilesList.get(position).getProfLastLoginDate();
            profileHolder.txtFooter.setText(lastLoginDate);

            profileHolder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginDialog(position);
                }
            });

            profileHolder.editProfile.setOnClickListener(new View.OnClickListener() {
                ;

                @Override
                public void onClick(View v) {
                    showLoginBeforeEditDialog(position);
                }
            });
        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void showLoginDialog(final int position) {
        final TextView login, remindPIN;
        final EditText PIN;
        Button submitLogin;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        login = (TextView) dialog.findViewById(R.id.profile_loginform_text_name);
        PIN = (EditText) dialog.findViewById(R.id.profile_loginform_text_pin);
        remindPIN = (TextView) dialog.findViewById(R.id.profile_loginform_link_remindPIN);
        submitLogin = (Button) dialog.findViewById(R.id.profile_loginform_button_login);

        login.setText(profilesList.get(position).getProfName());

        submitLogin.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String hashSalt = profilesList.get(position).getProfPINSalt();
                String hashedPIN = BCrypt.hashpw(PIN.getText().toString(), hashSalt);

                try {
                    if (!databaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania lub odzyskania hasła. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                        databaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                    } else {
                        chosenProfileID = profilesList.get(position).getProfID();
                        chosenProfilePosition = position;

                        databaseHelper.updateLastLoginDate(profilesList.get(position).getProfID());
                        databaseHelper.resetFailedLoginAttempts(profilesList.get(position).getProfID());

                        dialog.cancel();

                        Bundle bundle = new Bundle();
                        bundle.putString("name", profilesList.get(position).getProfName());
                        Intent intent = new Intent(context, MainMenu.class).putExtras(bundle);
                        activity.startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        remindPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showRemindPINDialog(position);
            }
        });

    }

    public void showLoginBeforeEditDialog(final int position) {
        final TextView login, remindPIN;
        final EditText PIN;
        Button submitLogin;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        login = (TextView) dialog.findViewById(R.id.profile_loginform_text_name);
        PIN = (EditText) dialog.findViewById(R.id.profile_loginform_text_pin);
        remindPIN = (TextView) dialog.findViewById(R.id.profile_loginform_link_remindPIN);
        submitLogin = (Button) dialog.findViewById(R.id.profile_loginform_button_login);

        login.setText(profilesList.get(position).getProfName());

        submitLogin.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String hashSalt = profilesList.get(position).getProfPINSalt();
                String hashedPIN = BCrypt.hashpw(PIN.getText().toString().trim(), hashSalt);

                try {
                    if (!databaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania lub odzyskania hasła. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                        databaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                    } else {
                        databaseHelper.resetFailedLoginAttempts(profilesList.get(position).getProfID());
                        dialog.cancel();
                        showEditDialog(position);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        remindPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showRemindPINDialog(position);
            }
        });

    }

    public void showEditDialog(final int position) {
        final EditText name, newPIN, newPINConfirm, newQuestion, newAnswer;
        Button submitName, submitPIN, submitQandA, submitDeleteProfile;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.profile_editform_text_name);
        newPIN = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN);
        newPINConfirm = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN_confirm);
        newQuestion = (EditText) dialog.findViewById(R.id.profile_editform_text_newHelperQuestion);
        newAnswer = (EditText) dialog.findViewById(R.id.profile_editform_text_newHelperAnswer);

        submitName = (Button) dialog.findViewById(R.id.profile_editform_submit_name);
        submitPIN = (Button) dialog.findViewById(R.id.profile_editform_submit_PIN);
        submitQandA = (Button) dialog.findViewById(R.id.profile_editform_submit_QandA);
        submitDeleteProfile = (Button) dialog.findViewById(R.id.profile_editform_submit_deleteProfile);

        name.setText(profilesList.get(position).getProfName());
        newQuestion.setText(profilesList.get(position).getProfHelperQuestion());

        submitName.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nową nazwę profilu.");
                } else if (!name.getText().toString().matches("[a-zA-Z]{2,20}")) {
                    name.setError("Nazwa profilu powinna składać się z co najmniej dwóch liter. Niedozwolone są cyfry oraz znaki specjalne.");
                } else if (databaseHelper.checkExistingProfileName(name.getText().toString())) {
                    name.setError("Istnieje już profil z podaną nazwą.");
                } else {
                    databaseHelper.updateProfileName(name.getText().toString(), profilesList.get(position).getProfID());
                    profilesList.get(position).setProfName(name.getText().toString());
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitPIN.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                // hashing password
                String newHashSalt = BCrypt.gensalt();
                String hashedPIN = BCrypt.hashpw(newPIN.getText().toString(), newHashSalt);
                String hashedPINConfirm = BCrypt.hashpw(newPINConfirm.getText().toString(), newHashSalt);
                boolean passwordMatches = hashedPIN.equals(hashedPINConfirm);

                if (newPIN.getText().toString().isEmpty() || newPINConfirm.getText().toString().isEmpty()) {
                    newPIN.setError("W celu zmiany kodu PIN podaj nowy kod, a następnie go potwierdź.");
                } else if (newPIN.getText().toString().length() < 6 || newPINConfirm.getText().toString().length() < 6) {
                    newPIN.setError("Kod PIN musi składać się z 6 cyfr.");
                } else if (!passwordMatches) {
                    newPIN.setError("Podane nowe kody PIN nie są identyczne.");
                } else {
                    databaseHelper.updateProfilePIN(hashedPIN, newHashSalt, profilesList.get(position).getProfID());

                    profilesList.get(position).setProfPIN(hashedPIN);
                    profilesList.get(position).setProfPINSalt(newHashSalt);

                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitQandA.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String helperHashSalt = BCrypt.gensalt();
                String helperQuestion = newQuestion.getText().toString().trim();
                String helperHashedAnswer = BCrypt.hashpw(newAnswer.getText().toString().trim(), helperHashSalt);

                if (newQuestion.getText().toString().trim().isEmpty()) {
                    newQuestion.setError("Podaj pytanie pomocnicze.");
                } else if (newAnswer.getText().toString().trim().isEmpty()) {
                    newAnswer.setError("Podaj odpowiedź na pytanie pomocnicze.");
                } else if (!newQuestion.getText().toString().matches("[\\sa-zA-Z0-9_.,-]{2,128}[?]")) {
                    newQuestion.setError("Pytanie pomocnicze powinno składać się z co najmniej dwóch liter. Na końcu musi znajdować się znak zapytania.");
                } else {
                    databaseHelper.updateHelperQuestionAndAnswer(helperQuestion, helperHashedAnswer, helperHashSalt, profilesList.get(position).getProfID());

                    profilesList.get(position).setProfHelperQuestion(helperQuestion);
                    profilesList.get(position).setProfHelperAnswer(helperHashedAnswer);
                    profilesList.get(position).setProfHelperSalt(helperHashSalt);
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitDeleteProfile.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showLoginBeforeDeleteDialog(position);
                }
            });
    }

    public void showRemindPINDialog(final int position) {
        final TextView helperQuestion;
        final EditText helperAnswer;
        Button sumbitRemind;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_remind_pin_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        helperQuestion = (TextView) dialog.findViewById(R.id.profile_remindPIN_text_helperquestion_question);
        helperAnswer = (EditText) dialog.findViewById(R.id.profile_remindPIN_text_helperquestion_answer);
        sumbitRemind = (Button) dialog.findViewById(R.id.profile_remindPIN_submitRemind);

        helperQuestion.setText(profilesList.get(position).getProfHelperQuestion());

        sumbitRemind.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String hashHelperSalt = profilesList.get(position).getProfHelperSalt();
                String newHashPINSalt = BCrypt.gensalt();
                String hashedHelperAnswer = BCrypt.hashpw(helperAnswer.getText().toString().trim(), hashHelperSalt);
                PasswordGenerator pwdGen = new PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(true)
                        .build();
                String newPIN = pwdGen.generate(6);
                String newHashedPIN = BCrypt.hashpw(newPIN, newHashPINSalt);

                    try {
                        if (!databaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                            Toast.makeText(context, "Podjęto zbyt dużo prób logowania lub odzyskania hasła. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                        } else if (helperAnswer.getText().toString().isEmpty()) {
                                helperAnswer.setError("W celu ozyskania hasła podaj odpowiedź na pytanie pomocnicze.");
                        } else if (!hashedHelperAnswer.equals(profilesList.get(position).getProfHelperAnswer())) {
                            helperAnswer.setError("Podano nieprawidłową odpowiedź na pytanie pomocnicze.");
                            databaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                        } else {
                            databaseHelper.resetFailedLoginAttempts(profilesList.get(position).getProfID());
                            databaseHelper.updateProfilePIN(newHashedPIN, newHashPINSalt, profilesList.get(position).getProfID());

                            profilesList.get(position).setProfPIN(newHashedPIN);
                            profilesList.get(position).setProfPINSalt(newHashPINSalt);

                            dialog.cancel();

                            // notify list
                            notifyDataSetChanged();

                            showResetPINDialog(newPIN, position);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
            }
        });

    }

    public void showResetPINDialog(String newPIN, final int position) {
        final TextView name, PIN;
        Button backToTheProfiles;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_show_pin);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.profile_showPIN_text_name);
        PIN = (TextView) dialog.findViewById(R.id.profile_showPIN_text_newPIN);
        backToTheProfiles = (Button) dialog.findViewById(R.id.profile_showPIN_submitRemind);

        name.setText(profilesList.get(position).getProfName());
        PIN.setText(newPIN);

        backToTheProfiles.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

    public void showLoginBeforeDeleteDialog(final int position) {
        final TextView login;
        final EditText PIN;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_predelete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        login = (TextView) dialog.findViewById(R.id.profile_loginform_predelete_text_name);
        PIN = (EditText) dialog.findViewById(R.id.profile_loginform_predelete_text_pin);
        submitDelete = (Button) dialog.findViewById(R.id.profile_loginform_predelete_button_login);

        login.setText(profilesList.get(position).getProfName());

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                String hashSalt = profilesList.get(position).getProfPINSalt();
                String hashedPIN = BCrypt.hashpw(PIN.getText().toString().trim(), hashSalt);

                try {
                    if (!databaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania lub odzyskania hasła. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                        databaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                    } else {
                        if (chosenProfileID == profilesList.get(position).getProfID()) {
                            chosenProfileID = 0;
                        }
                        databaseHelper.deleteProfile(profilesList.get(position).getProfID());
                        profilesList.remove(position);
                        dialog.cancel();

                        // notify list
                        notifyDataSetChanged();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
