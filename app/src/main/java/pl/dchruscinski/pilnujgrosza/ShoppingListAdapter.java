package pl.dchruscinski.pilnujgrosza;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShoppingModel> shoppingList;
    private Context context;
    private Activity activity;
    private DatabaseHelper databaseHelper;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public ShoppingListAdapter(Context context, Activity activity, ArrayList<ShoppingModel> shoppingList) {
        this.context = context;
        this.activity  = activity ;
        this.shoppingList = shoppingList;
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
            emptyText = (TextView) v.findViewById(R.id.sholist_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.sholist_empty_logo);
        }
    }

    public class ViewHolderShopping extends RecyclerView.ViewHolder {
        public TextView shoListName, shoListDesc, shoListDate, shoListValue, shoListNumOfProd, sholistCurrency;
        public View layout;

        public ViewHolderShopping(View v) {
            super(v);
            layout = v;
            shoListName = (TextView) v.findViewById(R.id.sholist_rc_name);
            shoListDesc = (TextView) v.findViewById(R.id.sholist_rc_desc);
            shoListDate = (TextView) v.findViewById(R.id.sholist_rc_date);
            shoListValue = (TextView) v.findViewById(R.id.sholist_rc_value);
            shoListNumOfProd = (TextView) v.findViewById(R.id.sholist_rc_num_of_prod);
            sholistCurrency = (TextView) v.findViewById(R.id.sholist_rc_currency);
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size() > 0 ? shoppingList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (shoppingList.isEmpty()) {
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
            v = inflater.inflate(R.layout.sholist_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderShopping(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.sholist_empty_view, parent, false);
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
        BigDecimal prodValue;

        if (viewType == VIEW_TYPE_OBJECT_VIEW) {
            final ShoppingModel shopping = shoppingList.get(position);
            ViewHolderShopping shoppingHolder = (ViewHolderShopping) holder;

            prodValue = BigDecimal.valueOf(shoppingList.get(position).getShoValue()).divide(BigDecimal.valueOf(100));

            shoppingHolder.shoListName.setText(shopping.getShoName());
            shoppingHolder.shoListDesc.setText(shopping.getShoDesc());
            shoppingHolder.shoListDate.setText(shopping.getShoDate());
            shoppingHolder.shoListValue.setText(prodValue.equals(BigDecimal.valueOf(0)) ? new DecimalFormat("0").format(prodValue) : new DecimalFormat("#.##").format(prodValue));
            shoppingHolder.shoListNumOfProd.setText(String.valueOf(databaseHelper.getNumberOfProductsInShoppingList(shoppingList.get(position).getShoID())));
            shoppingHolder.sholistCurrency.setText(databaseHelper.getCurrency(chosenProfileID));

            shoppingHolder.shoListName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            shoppingHolder.shoListDesc.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            shoppingHolder.shoListDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            shoppingHolder.shoListValue.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            shoppingHolder.shoListNumOfProd.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            shoppingHolder.sholistCurrency.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Shopping.class)
                            .putExtra("shoID", shoppingList.get(position).getShoID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

}
