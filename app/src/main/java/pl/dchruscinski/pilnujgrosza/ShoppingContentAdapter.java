package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class ShoppingContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShoppingContentModel> shoppingContentList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public ShoppingContentAdapter(Context context, Activity activity, ArrayList<ShoppingContentModel> shoppingContentList) {
        this.context = context;
        this.activity = activity;
        this.shoppingContentList = shoppingContentList;
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
            emptyText = (TextView) v.findViewById(R.id.shocont_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.shocont_empty_logo);
        }
    }

    public class ViewHolderShoppingContent extends RecyclerView.ViewHolder {
        public TextView shoContName, shoContAmount, shoContUnit, shoContValue, shoContCurrency;
        public CheckBox shoContCheckbox;
        public ImageView editShoCont, deleteShoCont;
        public View layout;

        public ViewHolderShoppingContent(View v) {
            super(v);
            layout = v;
            shoContName = (TextView) v.findViewById(R.id.shocont_rc_name);
            shoContAmount = (TextView) v.findViewById(R.id.shocont_rc_amount);
            shoContUnit = (TextView) v.findViewById(R.id.shocont_rc_unit);
            shoContValue = (TextView) v.findViewById(R.id.shocont_rc_value);
            shoContCurrency = (TextView) v.findViewById(R.id.shocont_rc_currency);
            shoContCheckbox = (CheckBox) v.findViewById(R.id.shocont_rc_checkbox);
            deleteShoCont = (ImageView) v.findViewById(R.id.shocont_rc_deleteicon);
        }
    }

    @Override
    public int getItemCount() {
        return shoppingContentList.size() > 0 ? shoppingContentList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (shoppingContentList.isEmpty()) {
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
            v = inflater.inflate(R.layout.shocont_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderShoppingContent(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.shocont_empty_view, parent, false);
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
        databaseHelper = new DatabaseHelper(context);
        BigDecimal contValue;

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            final ShoppingContentModel shoppingContent = shoppingContentList.get(position);
            ViewHolderShoppingContent shoppingContentHolder = (ViewHolderShoppingContent) holder;

            contValue = BigDecimal.valueOf(shoppingContentList.get(position).getShoContValue()).divide(BigDecimal.valueOf(100));

            shoppingContentHolder.shoContName.setText(shoppingContent.getShoContName());
            shoppingContentHolder.shoContAmount.setText(String.valueOf(shoppingContent.getShoContAmount()));
            shoppingContentHolder.shoContUnit.setText(shoppingContent.getShoContUnit());

            if (shoppingContentList.get(position).isShoContChecked()) {
                shoppingContentHolder.shoContCheckbox.setChecked(true);
                shoppingContentHolder.shoContName.setPaintFlags(shoppingContentHolder.shoContName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                shoppingContentHolder.shoContAmount.setPaintFlags(shoppingContentHolder.shoContAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                shoppingContentHolder.shoContUnit.setPaintFlags(shoppingContentHolder.shoContUnit.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                shoppingContentHolder.shoContValue.setPaintFlags(shoppingContentHolder.shoContValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                shoppingContentHolder.shoContCurrency.setPaintFlags(shoppingContentHolder.shoContCurrency.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            /*
            if (contValue.equals(BigDecimal.valueOf(0))) {
                shoppingContentHolder.shoContValue.setVisibility(View.INVISIBLE);
                shoppingContentHolder.shoContCurrency.setVisibility(View.INVISIBLE);
            } else {
                shoppingContentHolder.shoContValue.setText(new DecimalFormat("#.##").format(contValue));
            }
            */

            shoppingContentHolder.shoContCurrency.setText(databaseHelper.getCurrency(chosenProfileID));

            if (contValue.equals(BigDecimal.valueOf(0))) {
                shoppingContentHolder.shoContValue.setText("");
                shoppingContentHolder.shoContCurrency.setText("");
            } else {
                shoppingContentHolder.shoContValue.setText(new DecimalFormat("#.##").format(contValue));
            }

            shoppingContentHolder.shoContCheckbox.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    if (shoppingContentList.get(position).isShoContChecked()) {
                        shoppingContentHolder.shoContCheckbox.setChecked(false);
                        shoppingContentHolder.shoContName.setPaintFlags(shoppingContentHolder.shoContName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        shoppingContentHolder.shoContAmount.setPaintFlags(shoppingContentHolder.shoContAmount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        shoppingContentHolder.shoContUnit.setPaintFlags(shoppingContentHolder.shoContUnit.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        shoppingContentHolder.shoContValue.setPaintFlags(shoppingContentHolder.shoContValue.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        shoppingContentHolder.shoContCurrency.setPaintFlags(shoppingContentHolder.shoContCurrency.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        databaseHelper.updateShoppingContentStatus(shoppingContentList.get(position).getShoContID(), false);
                        shoppingContentList.get(position).setShoContChecked(false);
                    } else {
                        shoppingContentHolder.shoContCheckbox.setChecked(true);
                        shoppingContentHolder.shoContName.setPaintFlags(shoppingContentHolder.shoContName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        shoppingContentHolder.shoContAmount.setPaintFlags(shoppingContentHolder.shoContAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        shoppingContentHolder.shoContUnit.setPaintFlags(shoppingContentHolder.shoContUnit.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        shoppingContentHolder.shoContValue.setPaintFlags(shoppingContentHolder.shoContValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        shoppingContentHolder.shoContCurrency.setPaintFlags(shoppingContentHolder.shoContCurrency.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        databaseHelper.updateShoppingContentStatus(shoppingContentList.get(position).getShoContID(), true);
                        shoppingContentList.get(position).setShoContChecked(true);
                    }
                    notifyDataSetChanged();
                }
            });

            shoppingContentHolder.shoContName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            shoppingContentHolder.shoContAmount.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            shoppingContentHolder.shoContUnit.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            shoppingContentHolder.shoContValue.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            shoppingContentHolder.shoContCurrency.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            shoppingContentHolder.deleteShoCont.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

    public void showEditDialog(final int position) {
        final EditText name, amount, unit, value;
        final TextView currency;
        Button submitEdit;

        BigDecimal prodValue;
        prodValue = BigDecimal.valueOf(shoppingContentList.get(position).getShoContValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shocont_edit_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (EditText) dialog.findViewById(R.id.shocont_editform_text_name);
        amount = (EditText) dialog.findViewById(R.id.shocont_editform_text_amount);
        unit = (EditText) dialog.findViewById(R.id.shocont_editform_text_unit);
        value = (EditText) dialog.findViewById(R.id.shocont_editform_text_value);
        currency = (TextView) dialog.findViewById(R.id.shocont_editform_info_currency);
        submitEdit = (Button) dialog.findViewById(R.id.shocont_editform_submit);

        name.setText(shoppingContentList.get(position).getShoContName());
        amount.setText(shoppingContentList.get(position).getShoContAmount());
        unit.setText(shoppingContentList.get(position).getShoContUnit());
        value.setText(prodValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(prodValue) : new DecimalFormat("#.##").format(prodValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        submitEdit.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                ShoppingContentModel shoppingContentModel = new ShoppingContentModel();
                int intValue = 0;
                boolean isValueValid = false;

                if (!value.getText().toString().trim().isEmpty()) {
                    String stringValue = value.getText().toString().trim();
                    if (stringValue.contains(".")) {
                        if (stringValue.substring(stringValue.indexOf(".") + 1).length() > 2 || !(value.getText().toString().trim().length() < 8)) {
                            isValueValid = false;
                        } else {
                            isValueValid = true;
                            BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                        }
                    } else if (value.getText().toString().trim().length() < 5) {
                        isValueValid = true;
                        BigDecimal bigDecimalValue = new BigDecimal(stringValue.replaceAll(",", ".")); //.setScale(2, BigDecimal.ROUND_HALF_UP);
                        intValue = (bigDecimalValue.multiply(BigDecimal.valueOf(100))).intValueExact();
                    }
                } else {
                    isValueValid = true;
                }

                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Podaj nazwę produktu.");
                } else if (!name.getText().toString().matches("[\\sa-zA-Z;:\\-,.\\d]{2,25}")) {
                    name.setError("Nazwa produktu powinna składać się z 2-25 znaków.");
                } else if (databaseHelper.checkExistingProductNameInShoppingList(shoppingContentList.get(position).getShoContShoID(), shoppingContentList.get(position).getShoContID(), name.getText().toString())) {
                    name.setError("W liście znajduje się już produkt z podaną nazwą.");
                } else if (amount.getText().toString().trim().isEmpty()) {
                    amount.setError("Podaj ilość/liczbę produktów.");
                } else if (!amount.getText().toString().matches("[.,0-9]{1,4}")) {
                    amount.setError("Ilość/liczbę powinna składać się wyłącznie z liczb.");
                } else if (unit.getText().toString().trim().isEmpty()) {
                    unit.setError("Podaj jednostkę produktu.");
                } else if (!unit.getText().toString().matches("[a-zA-Z;:,.\\-]{1,5}")) {
                    unit.setError("Jednostka produktu powinna składać się wyłącznie z liter.");
                } else if (!isValueValid) {
                    value.setError("Podaj wartość z dokładnością do dwóch miejsc dziesiętnych, maksymalnie 7 znaków.");
                } else {
                    shoppingContentModel.setShoContName(name.getText().toString());
                    shoppingContentModel.setShoContAmount(amount.getText().toString());
                    shoppingContentModel.setShoContUnit(unit.getText().toString());
                    shoppingContentModel.setShoContValue(intValue);
                    databaseHelper.updateShoppingContent(shoppingContentList.get(position).getShoContID(), shoppingContentModel);

                    shoppingContentList.get(position).setShoContName(name.getText().toString());
                    shoppingContentList.get(position).setShoContAmount(amount.getText().toString());
                    shoppingContentList.get(position).setShoContUnit(unit.getText().toString());
                    shoppingContentList.get(position).setShoContValue(intValue);

                    if (context instanceof Shopping) {
                        ((Shopping) context).displayShopping();
                    }
                    notifyDataSetChanged();
                    dialog.cancel();
                }
            }
        });

    }

    public void showDeleteDialog(final int position) {
        final TextView name, amount, unit, value, currency;
        Button submitDelete;
        databaseHelper = new DatabaseHelper(activity);

        BigDecimal prodValue;
        prodValue = BigDecimal.valueOf(shoppingContentList.get(position).getShoContValue()).divide(BigDecimal.valueOf(100));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shocont_delete_form);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.shocont_deleteform_info_name);
        amount = (TextView) dialog.findViewById(R.id.shocont_deleteform_info_amount);
        unit = (TextView) dialog.findViewById(R.id.shocont_deleteform_info_unit);
        value = (TextView) dialog.findViewById(R.id.shocont_deleteform_info_value);
        currency = (TextView) dialog.findViewById(R.id.shocont_deleteform_info_currency);
        submitDelete = (Button) dialog.findViewById(R.id.shocont_deleteform_submit);

        name.setText(shoppingContentList.get(position).getShoContName());
        amount.setText(shoppingContentList.get(position).getShoContAmount());
        unit.setText(shoppingContentList.get(position).getShoContUnit());
        value.setText(prodValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(prodValue) : new DecimalFormat("#.##").format(prodValue));
        currency.setText(databaseHelper.getCurrency(chosenProfileID));

        submitDelete.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                databaseHelper.deleteShoppingContent(shoppingContentList.get(position).getShoContID());
                shoppingContentList.remove(position);

                if (context instanceof Shopping) {
                    ((Shopping) context).displayShopping();
                }
                notifyDataSetChanged();
                dialog.cancel();
            }
        });
    }

}
