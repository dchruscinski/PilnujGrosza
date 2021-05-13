package pl.dchruscinski.pilnujgrosza;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.VISIBLE;
import static java.sql.Types.NULL;
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class ScheduledPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ScheduledPaymentModel> scheduledPaymentsList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;
    final int callbackId = 42;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_SCHEDULED_PAYMENT_VIEW = 1;

    public ScheduledPaymentAdapter(Context context, Activity activity, ArrayList scheduledPaymentsList) {
        this.context = context;
        this.activity  = activity ;
        this.scheduledPaymentsList = scheduledPaymentsList;
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
            emptyText = (TextView) v.findViewById(R.id.schpay_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.schpay_empty_logo);
        }
    }

    public class ViewHolderScheduledPayment extends RecyclerView.ViewHolder {

        public TextView txtHeader, txtFooter, currency, schpayDate, categoryName;
        public View layout;

        public ViewHolderScheduledPayment(View v) {
            super(v);
            layout = v;
            txtHeader = v.findViewById(R.id.schpay_rc_firstLine);
            txtFooter = v.findViewById(R.id.schpay_rc_secondLine);
            currency = v.findViewById(R.id.schpay_rc_currency);
            schpayDate = v.findViewById(R.id.schpay_rc_transDate);
            categoryName = v.findViewById(R.id.schpay_rc_category);
        }
    }

    @Override
    public int getItemCount() {
        return scheduledPaymentsList.size() > 0 ? scheduledPaymentsList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (scheduledPaymentsList.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST;
        } else {
            return VIEW_TYPE_SCHEDULED_PAYMENT_VIEW;
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TYPE_SCHEDULED_PAYMENT_VIEW) {
            v = inflater.inflate(R.layout.schpay_rc_row, parent, false);
            vh = new ScheduledPaymentAdapter.ViewHolderScheduledPayment(v);
        } else {
            v = inflater.inflate(R.layout.schpay_empty_view, parent, false);
            vh = new ScheduledPaymentAdapter.ViewHolderEmpty(v);
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
        databaseHelper = new DatabaseHelper(context);
        BigDecimal transactionValue;

        if (viewType == VIEW_TYPE_SCHEDULED_PAYMENT_VIEW) {
            final ScheduledPaymentModel scheduledPayment = scheduledPaymentsList.get(position);
            ViewHolderScheduledPayment scheduledPaymentHolder = (ViewHolderScheduledPayment) holder;

            transactionValue = BigDecimal.valueOf(scheduledPaymentsList.get(position).getSchpayValue()).divide(BigDecimal.valueOf(100));

            scheduledPaymentHolder.txtHeader.setText(transactionValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(transactionValue) : new DecimalFormat("#.##").format(transactionValue));
            scheduledPaymentHolder.txtFooter.setText(scheduledPayment.getSchpayDescription());
            scheduledPaymentHolder.currency.setText(databaseHelper.getCurrency(chosenProfileID));
            scheduledPaymentHolder.schpayDate.setText(scheduledPayment.getSchpayDate());
            scheduledPaymentHolder.categoryName.setText(databaseHelper.getExpenseCategory(scheduledPayment.getSchpayCatID()).getExpcatName());

            scheduledPaymentHolder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showScheduledPaymentEditDialog(position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            scheduledPaymentHolder.txtFooter.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    try {
                        showScheduledPaymentEditDialog(position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            scheduledPaymentHolder.schpayDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    try {
                        showScheduledPaymentEditDialog(position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            scheduledPaymentHolder.categoryName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    try {
                        showScheduledPaymentEditDialog(position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void displayExpenseCategoriesSpinnerData(Spinner spinner, String searchString) {
        Cursor expenseCategoriesCursorForSpinner = databaseHelper.getExpenseCategoriesForSpinner();

        String[] from = { "expcatName" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter expenseCategoryAdapterForSpinner = new SimpleCursorAdapter(this.context,
                android.R.layout.simple_spinner_item,
                expenseCategoriesCursorForSpinner, from, to, 0);

        expenseCategoryAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(expenseCategoryAdapterForSpinner);
        if (!searchString.equals("")) {
            spinner.setSelection(getIndexForSpinner(spinner, from[0], searchString));
        }
    }

    public void displayBudgetsSpinnerData(Spinner spinner, String searchString) {
        Cursor budgetsCursorForSpinner = databaseHelper.getBudgetsForSpinner();

        String[] from = { "budDates" };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter budgetAdapterForSpinner = new SimpleCursorAdapter(this.context,
                android.R.layout.simple_spinner_item,
                budgetsCursorForSpinner, from, to, 0);

        budgetAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(budgetAdapterForSpinner);
        if (!searchString.equals("")) {
            spinner.setSelection(getIndexForSpinner(spinner, from[0], searchString));
        }
    }

    private int getIndexForSpinner(Spinner spinner, String columnName, String searchString) {

        if (searchString == null || spinner.getCount() == 0) {
            return -1; // not found
        }
        else {
            Cursor cursor = (Cursor)spinner.getItemAtPosition(0);

            int initialCursorPos = cursor.getPosition(); //  remember for later

            int index = -1; // not found
            for (int i = 0; i < spinner.getCount(); i++) {

                cursor.moveToPosition(i);
                String itemText = cursor.getString(cursor.getColumnIndex(columnName));

                if (itemText.equals(searchString)) {
                    index = i; // found!
                    break;
                }
            }

            cursor.moveToPosition(initialCursorPos); // leave cursor as we found it.

            return index;
        }
    }

    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this.activity, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this.activity, permissionsId, callbackId);
    }

    public int getCalendarEventID(Context context, String title) {
        String eventName;
        String eventID;
        int result = 0;

        checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

        Uri.Builder eventsUriBuilder = CalendarContract.Events.CONTENT_URI
                .buildUpon();
        Uri eventsUri = eventsUriBuilder.build();

        String[] eventsColumns = {CalendarContract.Events._ID, CalendarContract.Events.TITLE};

        Cursor cursor = context.getContentResolver().query(eventsUri, eventsColumns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                eventID = cursor.getString(cursor.getColumnIndex(eventsColumns[0]));
                eventName = cursor.getString(cursor.getColumnIndex(eventsColumns[1]));

                if (eventName != null && eventName.contains(title)) {
                    result = Integer.parseInt(eventID);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        return result;
    }

    public void showScheduledPaymentEditDialog(final int position) throws ParseException {
        final EditText description, value;
        final Spinner category, budget;
        final TextView dateTextView, budgetText, currency;
        final CheckBox countToBudget;
        Button submitEdit, submitDelete, createTransaction;
        String selectedCategory, selectedBudget;

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar.setTime(dateFormat.parse(scheduledPaymentsList.get(position).getSchpayDate()));

        BigDecimal scheduledPaymentValue;
        scheduledPaymentValue = BigDecimal.valueOf(scheduledPaymentsList.get(position).getSchpayValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.schpay_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.schpay_editform_text_date);
        value = (EditText) dialog.findViewById(R.id.schpay_editform_text_value);
        currency = (TextView) dialog.findViewById(R.id.schpay_editform_text_currency);
        description = (EditText) dialog.findViewById(R.id.schpay_editform_text_desc);
        category = (Spinner) dialog.findViewById(R.id.schpay_editform_spin_cat);
        countToBudget = (CheckBox) dialog.findViewById(R.id.schpay_editform_checkbox_budget);
        budget = (Spinner) dialog.findViewById(R.id.schpay_editform_spin_budget);
        budgetText = (TextView) dialog.findViewById(R.id.schpay_editform_spin_budget_info);
        submitEdit = (Button) dialog.findViewById(R.id.schpay_editform_submitEdit);
        submitDelete = (Button) dialog.findViewById(R.id.schpay_editform_submitDelete);
        createTransaction = (Button) dialog.findViewById(R.id.schpay_editform_createTransaction);

        dateTextView.setText(scheduledPaymentsList.get(position).getSchpayDate());
        value.setText(scheduledPaymentValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(scheduledPaymentValue) : new DecimalFormat("#.##").format(scheduledPaymentValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(scheduledPaymentsList.get(position).getSchpayDescription());

        String eventTitle = context.getResources().getString(R.string.schpay_calevent_title)
                + " "
                + value.getText().toString().trim()
                + " "
                + currency.getText().toString().trim()
                + "; ID: "
                + scheduledPaymentsList.get(position).getSchpayID();

        if (scheduledPaymentsList.get(position).getSchpayCatID() != 0) {
            selectedCategory = databaseHelper.getExpenseCategory(scheduledPaymentsList.get(position).getSchpayCatID()).getExpcatName();
        } else {
            selectedCategory = "";
        }

        if (scheduledPaymentsList.get(position).getSchpayBudID() != 0) {
            selectedBudget = databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudEndDate();
        } else {
            selectedBudget = "";
        }

        displayExpenseCategoriesSpinnerData(category, selectedCategory);
        displayBudgetsSpinnerData(budget, selectedBudget);

        if (!selectedBudget.equals("")) {
            countToBudget.setChecked(true);
            budget.setVisibility(VISIBLE);
            budgetText.setVisibility(VISIBLE);
        }

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
                new DatePickerDialog(v.getRootView().getContext(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        countToBudget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(countToBudget.isChecked()) {
                    budget.setVisibility(VISIBLE);
                    budgetText.setVisibility(VISIBLE);
                } else {
                    budget.setVisibility(View.GONE);
                    budgetText.setVisibility(View.GONE);
                }
            }
        });

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ScheduledPaymentModel scheduledPaymentModel = new ScheduledPaymentModel();
                int intValue = 0;
                boolean isValueValid = false;

                if (!value.getText().toString().trim().isEmpty()) {
                    String stringValue = value.getText().toString().trim();
                    if (stringValue.contains(".")) {
                        if (stringValue.substring(stringValue.indexOf(".") + 1).length() > 2) {
                            isValueValid = false;
                        } else {
                            isValueValid = true;
                            BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else {
                        isValueValid = true;
                        BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                } else {
                    isValueValid = true;
                }

                try {
                    if (dateFormat.parse(dateTextView.getText().toString()).compareTo(dateFormat.parse(databaseHelper.getNextDayDate())) < 0) {
                        dateTextView.requestFocus();
                        dateTextView.setError("Płatność musi zostać zaplanowana w dniu późniejszym niż dziś.");
                    } else if (value.getText().toString().trim().isEmpty()) {
                        value.setError("Podaj nową wartość wydatku.");
                    } else if (!isValueValid) {
                        value.setError("Podaj nową wartość z dokładnością do dwóch miejsc dziesiętnych.");
                    } else {
                        if (countToBudget.isChecked()) {
                            scheduledPaymentModel.setSchpayBudID((int) budget.getSelectedItemId());
                        } else {
                            scheduledPaymentModel.setSchpayBudID(NULL);
                        }
                        scheduledPaymentModel.setSchpayCatID((int) category.getSelectedItemId());
                        scheduledPaymentModel.setSchpayValue(intValue);
                        scheduledPaymentModel.setSchpayDescription(description.getText().toString());
                        scheduledPaymentModel.setSchpayDate(dateTextView.getText().toString());
                        databaseHelper.updateScheduledPayment(scheduledPaymentsList.get(position).getSchpayID(), scheduledPaymentModel);

                        String[] notificationsTime = databaseHelper.getNotificationsTime(chosenProfileID);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notificationsTime[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(notificationsTime[1]));

                        ContentValues values = new ContentValues();
                        Uri updateUri = null;
                        values.put(CalendarContract.Events.TITLE,
                                context.getResources().getString(R.string.schpay_calevent_title)
                                + " "
                                + value.getText().toString().trim()
                                + " "
                                + currency.getText().toString().trim()
                                + "; ID: "
                                + scheduledPaymentsList.get(position).getSchpayID());
                        values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString().trim());
                        values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
                        values.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis() + 5*60*1000);

                        int eventID = getCalendarEventID(context, eventTitle);
                        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);

                        ContentResolver cr = context.getContentResolver();
                        cr.update(updateUri, values, null, null);

                        // use an intent to edit an event

                        /*
                        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, getCalendarEventID(context, eventTitle));
                        Intent intent = new Intent(Intent.ACTION_EDIT)
                                .setData(uri)
                                .putExtra(CalendarContract.Events.CALENDAR_TIME_ZONE, "Europe/Warsaw")
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis() + 5*60*1000) // five minutes
                                .putExtra(CalendarContract.Events.TITLE,
                                        context.getResources().getString(R.string.schpay_calevent_title)
                                                + " "
                                                + value.getText().toString().trim()
                                                + " "
                                                + currency.getText().toString().trim()
                                                + "; ID: "
                                                + scheduledPaymentsList.get(position).getSchpayID())

                                .putExtra(CalendarContract.Events.DESCRIPTION, description.getText().toString().trim())
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Nie posiadasz aplikacji, która obsługuje wydarzenia.", Toast.LENGTH_SHORT).show();
                        }
                        */

                        scheduledPaymentsList.get(position).setSchpayDate(dateTextView.getText().toString());
                        scheduledPaymentsList.get(position).setSchpayValue(intValue);
                        scheduledPaymentsList.get(position).setSchpayDescription(description.getText().toString());
                        if (countToBudget.isChecked()) {
                            scheduledPaymentsList.get(position).setSchpayBudID((int) budget.getSelectedItemId());
                        } else {
                            scheduledPaymentsList.get(position).setSchpayBudID(NULL);
                        }
                        scheduledPaymentsList.get(position).setSchpayCatID((int) category.getSelectedItemId());

                        // notify list
                        notifyDataSetChanged();
                        dialog.cancel();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDeleteExpenseContinuationDialog(position);
                }
        });

        createTransaction.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showCreateTransactionDialog(position);
            }
        });

    }

    public void showDeleteExpenseContinuationDialog(final int position) {
        final TextView dateTextView, currency, description, value, category, budget;
        Button submitDelete;

        BigDecimal scheduledPaymentValue;
        scheduledPaymentValue = BigDecimal.valueOf(scheduledPaymentsList.get(position).getSchpayValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.schpay_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.schpay_deleteform_text_date);
        value = (TextView) dialog.findViewById(R.id.schpay_deleteform_text_value);
        currency = (TextView) dialog.findViewById(R.id.schpay_deleteform_text_currency);
        description = (TextView) dialog.findViewById(R.id.schpay_deleteform_text_desc);
        category = (TextView) dialog.findViewById(R.id.schpay_deleteform_spin_cat_text);
        budget = (TextView) dialog.findViewById(R.id.schpay_deleteform_spin_budget_text);
        submitDelete = (Button) dialog.findViewById(R.id.schpay_deleteform_submitDelete);

        dateTextView.setText(scheduledPaymentsList.get(position).getSchpayDate());
        value.setText(scheduledPaymentValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(scheduledPaymentValue) : new DecimalFormat("#.##").format(scheduledPaymentValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(scheduledPaymentsList.get(position).getSchpayDescription());
        category.setText(databaseHelper.getExpenseCategory(scheduledPaymentsList.get(position).getSchpayCatID()).getExpcatName());
        if (scheduledPaymentsList.get(position).getSchpayBudID() != 0) {
            String budgetInfoText = databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudEndDate();
            budget.setText(budgetInfoText);
        } else {
            budget.setText("Nie zalicza się do budżetu");
        }

        String eventTitle = context.getResources().getString(R.string.schpay_calevent_title)
                + " "
                + value.getText().toString().trim()
                + " "
                + currency.getText().toString().trim()
                + "; ID: "
                + scheduledPaymentsList.get(position).getSchpayID();

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteScheduledPayment(scheduledPaymentsList.get(position).getSchpayID());
                scheduledPaymentsList.remove(position);

                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

                Uri deleteUri = null;
                int eventID = getCalendarEventID(context, eventTitle);
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);

                ContentResolver cr = context.getContentResolver();
                cr.delete(deleteUri,null, null);

                // notify list
                notifyDataSetChanged();
                dialog.cancel();
            }
        });

    }

    public void showCreateTransactionDialog(final int position) {
        final TextView dateTextView, currency, description, value, category, budget;
        Button submit;

        BigDecimal scheduledPaymentValue;
        scheduledPaymentValue = BigDecimal.valueOf(scheduledPaymentsList.get(position).getSchpayValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.schpay_create_transaction);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateTextView = (TextView) dialog.findViewById(R.id.schpay_create_transaction_text_date);
        value = (TextView) dialog.findViewById(R.id.schpay_create_transaction_text_value);
        currency = (TextView) dialog.findViewById(R.id.schpay_create_transaction_text_currency);
        description = (TextView) dialog.findViewById(R.id.schpay_create_transaction_text_desc);
        category = (TextView) dialog.findViewById(R.id.schpay_create_transaction_spin_cat_text);
        budget = (TextView) dialog.findViewById(R.id.schpay_create_transaction_spin_budget_text);
        submit = (Button) dialog.findViewById(R.id.schpay_create_transaction_submit);

        dateTextView.setText(scheduledPaymentsList.get(position).getSchpayDate());
        value.setText(scheduledPaymentValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(scheduledPaymentValue) : new DecimalFormat("#.##").format(scheduledPaymentValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));
        description.setText(scheduledPaymentsList.get(position).getSchpayDescription());
        category.setText(databaseHelper.getExpenseCategory(scheduledPaymentsList.get(position).getSchpayCatID()).getExpcatName());
        if (scheduledPaymentsList.get(position).getSchpayBudID() != 0) {
            String budgetInfoText = databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudStartDate()
                    + " <> " + databaseHelper.getBudget(scheduledPaymentsList.get(position).getSchpayBudID()).getBudEndDate();
            budget.setText(budgetInfoText);
        } else {
            budget.setText("Nie zalicza się do budżetu");
        }

        String eventTitle = context.getResources().getString(R.string.schpay_calevent_title)
                + " "
                + value.getText().toString().trim()
                + " "
                + currency.getText().toString().trim()
                + "; ID: "
                + scheduledPaymentsList.get(position).getSchpayID();

        submit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                TransactionModel transactionModel = new TransactionModel();
                int intValue = 0;

                String stringValue = value.getText().toString().trim();
                BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();

                transactionModel.setTransBudID(scheduledPaymentsList.get(position).getSchpayBudID());
                transactionModel.setTransCatID(scheduledPaymentsList.get(position).getSchpayCatID());
                transactionModel.setTransValue(intValue);
                transactionModel.setTransDescription(description.getText().toString());
                transactionModel.setTransDate(dateTextView.getText().toString());
                databaseHelper.addExpense(transactionModel);

                databaseHelper.deleteScheduledPayment(scheduledPaymentsList.get(position).getSchpayID());
                scheduledPaymentsList.remove(position);
                dialog.cancel();

                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

                Uri deleteUri = null;
                int eventID = getCalendarEventID(context, eventTitle);
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);

                ContentResolver cr = context.getContentResolver();
                cr.delete(deleteUri,null, null);

                // notify list
                notifyDataSetChanged();
                dialog.cancel();
            }
        });

    }

}
