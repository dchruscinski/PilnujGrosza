package pl.dchruscinski.pilnujgrosza;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhoneImagesGalleryAdapter extends BaseAdapter {
    Context context;
    Cursor imagesCursor;

    PhoneImagesGalleryAdapter(Context context, Cursor imagesCursor) {
        this.context = context;
        this.imagesCursor = imagesCursor;
    }

    @Override
    public int getCount() {
        return imagesCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        imagesCursor.moveToPosition(position);
        String path = imagesCursor.getString(0);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ImageView thumbnail = new ImageView(context);
        thumbnail.setImageBitmap(bitmap);
        thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        thumbnail.setLayoutParams(new ViewGroup.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, 300));
        return thumbnail;
    }
}
