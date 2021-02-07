package com.example.pilnujgrosza;

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

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private ArrayList<ProfileModel> profilesList;
    private Context context;
    private Activity activity;
    private ProfileDatabaseHelper profileDatabaseHelper;
    static int chosenProfileID;

    public ProfileAdapter(Context context, Activity activity, ArrayList<ProfileModel> profilesList) {
        this.context = context;
        this.activity  = activity ;
        this.profilesList = profilesList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter;
        public ImageView editProfile, delete;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.prof_rc_firstLine);
            txtFooter = (TextView) v.findViewById(R.id.prof_rc_secondLine);
            editProfile = (ImageView) v.findViewById(R.id.prof_rc_editicon);

            /*
            delete = (ImageView) v.findViewById(R.id.prof_rc_deleteIcon);
            */
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.profile_rc_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        /*
            - get element from your dataset at this position
            - replace the contents of the view with that element
         */
        final ProfileModel profile = profilesList.get(position);
        holder.txtHeader.setText(profile.getProfName());

        String lastLoginDate = (profilesList.get(position).getProfLastLoginDate() == null) ?
                "" : "Ostatnie logowanie: " + profilesList.get(position).getProfLastLoginDate();
        holder.txtFooter.setText(lastLoginDate);

        holder.txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog(position);
            }
        });

        holder.editProfile.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                showLoginBeforeEditDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profilesList.size();
    }

    public void showLoginDialog(final int position) {
        final TextView login, remindPIN;
        final EditText PIN;
        Button submitLogin;
        profileDatabaseHelper = new ProfileDatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
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
                String hashedPIN = org.mindrot.jbcrypt.BCrypt.hashpw(PIN.getText().toString(), hashSalt);


                try {
                    if (!profileDatabaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                        profileDatabaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                    } else {
                        chosenProfileID = position;

                        profileDatabaseHelper.updateLastLoginDate(profilesList.get(position).getProfID());
                        profileDatabaseHelper.resetFailedLoginAttempts(profilesList.get(position).getProfID());

                        dialog.cancel();

                        Bundle bundle = new Bundle();
                        bundle.putString("name", profilesList.get(position).getProfName());
                        Intent intent = new Intent(context, MainActivity.class).putExtras(bundle);
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
                // <-- TO DO -->
                // showRemindPINDialog(position);
            }
        });

    }

    public void showLoginBeforeEditDialog(final int position) {
        final TextView login, remindPIN;
        final EditText PIN;
        Button submitLogin;
        profileDatabaseHelper = new ProfileDatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
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
                String hashedPIN = org.mindrot.jbcrypt.BCrypt.hashpw(PIN.getText().toString().trim(), hashSalt);

                try {
                    if (!profileDatabaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                        profileDatabaseHelper.addFailedLoginAttempt(profilesList.get(position).getProfID());
                    } else if (!profileDatabaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else {
                        profileDatabaseHelper.resetFailedLoginAttempts(profilesList.get(position).getProfID());
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
                // <-- TO DO -->
                // showRemindPINDialog(position);
            }
        });

    }

    public void showEditDialog(final int position) {
        final EditText name, email, newPIN, newPINConfirm;
        Button submitName, submitEmail, submitPIN, submitDeleteProfile;

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
        email = (EditText) dialog.findViewById(R.id.profile_editform_text_email);
        newPIN = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN);
        newPINConfirm = (EditText) dialog.findViewById(R.id.profile_editform_text_newPIN_confirm);

        submitName = (Button) dialog.findViewById(R.id.profile_editform_submit_name);
        submitEmail = (Button) dialog.findViewById(R.id.profile_editform_submit_email);
        submitPIN = (Button) dialog.findViewById(R.id.profile_editform_submit_PIN);
        submitDeleteProfile = (Button) dialog.findViewById(R.id.profile_editform_submit_deleteProfile);

        name.setText(profilesList.get(position).getProfName());
        email.setText(profilesList.get(position).getProfEmail());

        submitName.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nową nazwę profilu.");
                } else if (!name.getText().toString().matches("[a-zA-Z]{2,20}")) {
                    name.setError("Nazwa profilu powinna składać się z co najmniej dwóch liter. Niedozwolone są cyfry oraz znaki specjalne.");
                } else if (profileDatabaseHelper.checkExistingName(name.getText().toString())) {
                    name.setError("Istnieje już profil z podaną nazwą.");
                } else {
                    profileDatabaseHelper.updateProfileName(name.getText().toString(), profilesList.get(position).getProfID());
                    profilesList.get(position).setProfName(name.getText().toString());
                    dialog.cancel();

                    // notify list
                    notifyDataSetChanged();
                }
            }
        });

        submitEmail.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().isEmpty()) {
                    email.setError("Podaj adres e-mail.");
                } else if (!email.getText().toString().matches("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)")) {
                    email.setError("Nieprawidłowy format adresu e-mail.");
                } else if (profileDatabaseHelper.checkExistingEmail(email.getText().toString())) {
                    email.setError("Istnieje już profil z podanym adresem e-mail.");
                } else {
                    profileDatabaseHelper.updateProfileEmail(email.getText().toString(), profilesList.get(position).getProfID());
                    profilesList.get(position).setProfEmail(email.getText().toString());
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
                String newHashSalt = org.mindrot.jbcrypt.BCrypt.gensalt();
                String hashedPIN = org.mindrot.jbcrypt.BCrypt.hashpw(newPIN.getText().toString(), newHashSalt);
                String hashedPINConfirm = org.mindrot.jbcrypt.BCrypt.hashpw(newPINConfirm.getText().toString(), newHashSalt);
                boolean passwordMatches = hashedPIN.equals(hashedPINConfirm);

                if (newPIN.getText().toString().isEmpty() || newPINConfirm.getText().toString().isEmpty()) {
                    newPIN.setError("W celu zmiany kodu PIN podaj nowy kod, a następnie go potwierdź.");
                } else if (newPIN.getText().toString().length() < 6 || newPINConfirm.getText().toString().length() < 6) {
                    newPIN.setError("Kod PIN musi składać się z 6 cyfr.");
                } else if (!passwordMatches) {
                    newPIN.setError("Podane nowe kody PIN nie są identyczne.");
                } else {
                    profileDatabaseHelper.updateProfilePIN(hashedPIN, newHashSalt, profilesList.get(position).getProfID());

                    profilesList.get(position).setProfPIN(hashedPIN);
                    profilesList.get(position).setProfPINSalt(newHashSalt);

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

    public void showLoginBeforeDeleteDialog(final int position) {
        final TextView login;
        final EditText PIN;
        Button submitDelete;
        profileDatabaseHelper = new ProfileDatabaseHelper(context);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_login_predelete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
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
                String hashedPIN = org.mindrot.jbcrypt.BCrypt.hashpw(PIN.getText().toString().trim(), hashSalt);

                try {
                    if (!profileDatabaseHelper.compareLoginDateTime(profilesList.get(position).getProfID())) {
                        Toast.makeText(context, "Podjęto zbyt dużo prób logowania. Spróbuj ponownie za jakiś czas.", Toast.LENGTH_SHORT).show();
                    } else if (!hashedPIN.equals(profilesList.get(position).getProfPIN())) {
                        PIN.setError("Podałeś nieprawidłowy kod PIN.");
                    } else {
                        if (chosenProfileID == profilesList.get(position).getProfID()) {
                            chosenProfileID = 0;
                        }
                        profileDatabaseHelper.deleteProfile(profilesList.get(position).getProfID());
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
