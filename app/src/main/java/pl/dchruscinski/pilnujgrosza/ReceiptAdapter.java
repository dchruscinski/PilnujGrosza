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

public class ReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReceiptModel> receiptList;
    private Context context;
    private Activity activity;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public ReceiptAdapter(Context context, Activity activity, ArrayList<ReceiptModel> receiptList) {
        this.context = context;
        this.activity  = activity ;
        this.receiptList = receiptList;
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
            emptyText = (TextView) v.findViewById(R.id.receiptlist_empty_text);
            emptyIcon = (ImageView) v.findViewById(R.id.receiptlist_empty_logo);
        }
    }

    public class ViewHolderReceipt extends RecyclerView.ViewHolder {
        public TextView recName, recDate, recDesc;
        public View layout;

        public ViewHolderReceipt(View v) {
            super(v);
            layout = v;
            recName = (TextView) v.findViewById(R.id.receipt_rc_name);
            recDate = (TextView) v.findViewById(R.id.receipt_rc_date);
            recDesc = (TextView) v.findViewById(R.id.receipt_rc_desc);
        }
    }

    @Override
    public int getItemCount() {
        return receiptList.size() > 0 ? receiptList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (receiptList.isEmpty()) {
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
            v = inflater.inflate(R.layout.receipt_rc_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderReceipt(v);
        } else {
            // create a new view
            v = inflater.inflate(R.layout.receipt_empty_view, parent, false);
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
            final ReceiptModel receipt = receiptList.get(position);
            ViewHolderReceipt receiptHolder = (ViewHolderReceipt) holder;

            receiptHolder.recName.setText(receipt.getRecName());
            receiptHolder.recDate.setText(receipt.getRecDate());
            receiptHolder.recDesc.setText(receipt.getRecDesc());

            receiptHolder.recName.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Receipt.class)
                            .putExtra("recID", receiptList.get(position).getRecID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            receiptHolder.recDate.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Receipt.class)
                            .putExtra("recID", receiptList.get(position).getRecID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

            receiptHolder.recDesc.setOnClickListener(new View.OnClickListener() {;
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Receipt.class)
                            .putExtra("recID", receiptList.get(position).getRecID())
                            .putExtra("position", position);
                    activity.startActivity(intent);
                }
            });

        } else {
            ViewHolderEmpty emptyHolder = (ViewHolderEmpty) holder;
        }
    }

}
