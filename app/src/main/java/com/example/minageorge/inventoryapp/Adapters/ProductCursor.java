package com.example.minageorge.inventoryapp.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.minageorge.inventoryapp.DetailsActivity;
import com.example.minageorge.inventoryapp.MainActivity;
import com.example.minageorge.inventoryapp.ProductsData.ProductsContract;
import com.example.minageorge.inventoryapp.ProductsData.ProductsProvider;
import com.example.minageorge.inventoryapp.R;
import java.io.ByteArrayInputStream;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mina George on 7/12/2017.
 */

public class ProductCursor extends android.widget.CursorAdapter implements View.OnClickListener {
    @BindView(R.id.title_text)
    TextView title;

    @BindView(R.id.quantity_text)
    TextView quantity;

    @BindView(R.id.price_text)
    TextView price;

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.delete_image)
    ImageView deleteImage;

    private Context mcontext;
    private MainActivity mainActivity;

    public ProductCursor(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mcontext = context;
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {
        ButterKnife.bind(this, view);
        //-- for load data from cursor
        final int proId = cursor.getInt(cursor.getColumnIndex(ProductsContract.DetailsEntity.RecordID));
        final String proTitle = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.DetailsEntity.ProductTitle));
        final int proPrice = cursor.getInt(cursor.getColumnIndex(ProductsContract.DetailsEntity.ProductPrice));
        final int proQuentity = cursor.getInt(cursor.getColumnIndex(ProductsContract.DetailsEntity.Quantity));
        final String proSupplier = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.DetailsEntity.Supplier));
        final String proEmail = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.DetailsEntity.SupplierEmail));
        final byte[] imgByte = cursor.getBlob(cursor.getColumnIndex(ProductsContract.DetailsEntity.Picture));
        if (imgByte != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imgByte);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(bitmap);
        }
        title.setText(proTitle);
        quantity.setText("Quantity : " + String.valueOf(proQuentity));
        price.setText("Price : " + String.valueOf(proPrice) + " $");
        //-- for details activity
        view.setTag(R.string.view_id, proId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainActivity.isInAction) {
                    Intent i = new Intent(mcontext, DetailsActivity.class);
                    i.putExtra("id", (Integer) v.getTag(R.string.view_id));
                    mcontext.startActivity(i);
                }
            }
        });
        //-- for delete action mode
        if (!mainActivity.isInAction) {
            deleteImage.setVisibility(View.GONE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
        }
        view.setOnLongClickListener(mainActivity);
        deleteImage.setOnClickListener(this);
        deleteImage.setTag(R.string.item_id, proId);
    }

    @Override
    public void onClick(View v) {
        int ID = (Integer) v.getTag(R.string.item_id);
        mainActivity.deleteProducts(ID);
    }
}
