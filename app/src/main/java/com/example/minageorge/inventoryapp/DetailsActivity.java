package com.example.minageorge.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.minageorge.inventoryapp.ProductsData.ProductsContract;
import com.example.minageorge.inventoryapp.ProductsData.ProductsProvider;
import java.io.ByteArrayInputStream;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.im)
    ImageView im;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.quantity)
    TextView quantity;

    @BindView(R.id.supplier)
    TextView supplier;

    private String proTitle;
    private int proPrice;
    private int proQuentity;
    private String proSupplier;
    private String proEmail;
    private byte[] imgByte;
    private int id;
    private ProductsContract.DetailsEntity contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        contract = new ProductsContract.DetailsEntity();
        getLoaderManager().initLoader(6, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_edit:
                update();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update() {
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra("id",id );
        i.putExtra("email",proEmail);
        i.putExtra("supplier",proSupplier);
        i.putExtra("price",proPrice);
        i.putExtra("quentity",proQuentity);
        i.putExtra("title",proTitle);
        i.putExtra("image",imgByte);
        startActivity(i);
    }

    public void order(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, proEmail);
        i.putExtra(Intent.EXTRA_TEXT, " we want more from " + proTitle);
        i.putExtra(Intent.EXTRA_SUBJECT, "product order");
        startActivity(Intent.createChooser(i, "Send Email"));
    }

    public void decrease(View view) {
        String[] ids = new String[]{String.valueOf(id)};
        if (proQuentity > 0) {
            ContentValues values = new ContentValues();
            values.put(ProductsContract.DetailsEntity.ProductTitle, proTitle);
            values.put(ProductsContract.DetailsEntity.ProductPrice, proPrice);
            values.put(ProductsContract.DetailsEntity.Quantity, proQuentity - 1);
            values.put(ProductsContract.DetailsEntity.SupplierEmail, proEmail);
            values.put(ProductsContract.DetailsEntity.Supplier, proSupplier);
            values.put(ProductsContract.DetailsEntity.Picture, imgByte);
            String url = ProductsProvider.ContentUri.toString() + "/1";
            getContentResolver().update(Uri.parse(url), values, null, ids);
        } else {
            Toast.makeText(this, "sorry this product not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void increase(View view) {
        String[] ids = new String[]{String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(ProductsContract.DetailsEntity.ProductTitle, proTitle);
        values.put(ProductsContract.DetailsEntity.ProductPrice, proPrice);
        values.put(ProductsContract.DetailsEntity.Quantity, proQuentity + 1);
        values.put(ProductsContract.DetailsEntity.SupplierEmail, proEmail);
        values.put(ProductsContract.DetailsEntity.Supplier, proSupplier);
        values.put(ProductsContract.DetailsEntity.Picture, imgByte);
        String url = ProductsProvider.ContentUri.toString() + "/1";
        getContentResolver().update(Uri.parse(url), values, null, ids);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int Id, Bundle args) {
        String[] ids = new String[]{String.valueOf(id)};
        String[] projection = {contract.ProductTitle, contract.Quantity, contract.ProductPrice, contract.Picture, contract.SupplierEmail, contract.Supplier};
        String url = ProductsProvider.ContentUri.toString() + "/1";
        return new CursorLoader(this, Uri.parse(url), projection, null, ids, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToNext()) {
            proTitle = data.getString(data.getColumnIndexOrThrow(ProductsContract.DetailsEntity.ProductTitle));
            proPrice = data.getInt(data.getColumnIndex(ProductsContract.DetailsEntity.ProductPrice));
            proQuentity = data.getInt(data.getColumnIndex(ProductsContract.DetailsEntity.Quantity));
            proSupplier = data.getString(data.getColumnIndexOrThrow(ProductsContract.DetailsEntity.Supplier));
            proEmail = data.getString(data.getColumnIndexOrThrow(ProductsContract.DetailsEntity.SupplierEmail));
            imgByte = data.getBlob(data.getColumnIndex(ProductsContract.DetailsEntity.Picture));
            if (imgByte != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imgByte);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                im.setImageBitmap(bitmap);
            }
            title.setText(proTitle);
            price.setText("Price : " + String.valueOf(proPrice) + " $");
            quantity.setText("Quantity : " + String.valueOf(proQuentity));
            supplier.setText("Supplier : " + proSupplier);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        title.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        im.setImageDrawable(getResources().getDrawable(R.drawable.product));
    }

    public void delete(View view) {
        final String[] ids = new String[]{String.valueOf(id)};
        final String url = ProductsProvider.ContentUri.toString() + "/1";
        int deleted = getContentResolver().delete(Uri.parse(url), null, ids);
        if (deleted > 0) {
            Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);

        } else {
            Toast.makeText(getBaseContext(), "Sorry Can't Delete this Products", Toast.LENGTH_SHORT).show();
        }
    }
}
