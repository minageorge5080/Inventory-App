package com.example.minageorge.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.minageorge.inventoryapp.ProductsData.ProductsContract;
import com.example.minageorge.inventoryapp.ProductsData.ProductsProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.supplier_email)
    EditText email;

    @BindView(R.id.product_title)
    EditText title;

    @BindView(R.id.product_price)
    EditText price;

    @BindView(R.id.product_quantity)
    EditText quantity;

    @BindView(R.id.supplier_name)
    EditText supplier;

    @BindView(R.id.product_image)
    ImageView Image;

    private String proTitle;
    private int proPrice;
    private int proQuantity;
    private String proSupplier;
    private String proEmail;
    private CharSequence way[];
    private Bitmap b;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("New Product");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
    }

    @OnClick(R.id.product_image)
    public void imageAction(View view) {
        way = new CharSequence[]{"camera", "gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select one .");
        builder.setItems(way, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (way[which].equals("camera")) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 100);
                } else {

                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 200);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap b1;
        if (requestCode == 100 && resultCode == RESULT_OK) {
            b = (Bitmap) data.getExtras().get("data");
            Image.setImageBitmap(b);

        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Image.setImageURI(uri);
            try {
                b1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                b = scaleDown(b1, 500, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_submit:
                if (validation()) {
                    insert();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validation() {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Product Title", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proTitle = title.getText().toString();
        }
        if (price.getText().toString().isEmpty()||(Integer.parseInt(price.getText().toString()))<=0) {
            Toast.makeText(getApplicationContext(), "Please Check Product Price", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proPrice = Integer.parseInt(price.getText().toString());
        }
        if (quantity.getText().toString().isEmpty()||(Integer.parseInt(quantity.getText().toString()))<=0) {
            Toast.makeText(getApplicationContext(), "Please Check Product Quantity", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proQuantity = Integer.parseInt(quantity.getText().toString());
        }
        if (supplier.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Product Supplier", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            proSupplier = quantity.getText().toString();
        }
        if (!emailValidation()) {
            return false;
        }
        return true;
    }

    private boolean emailValidation() {
        String Stringemail = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (Stringemail.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Supplier Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Stringemail.matches(emailPattern)) {
            proEmail = Stringemail;
            return true;
        }
        Toast.makeText(getApplicationContext(), "Incorrect Supplier Email", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void insert() {
        ContentValues values = new ContentValues();
        values.put(ProductsContract.DetailsEntity.ProductTitle, proTitle);
        values.put(ProductsContract.DetailsEntity.ProductPrice, proPrice);
        values.put(ProductsContract.DetailsEntity.Quantity, proQuantity);
        values.put(ProductsContract.DetailsEntity.Supplier, proSupplier);
        values.put(ProductsContract.DetailsEntity.SupplierEmail, proEmail);
        if (b != null)
            values.put(ProductsContract.DetailsEntity.Picture, getBitmapAsByteArray(b));
        getContentResolver().insert(new ProductsProvider().ContentUri, values);
        builder.setTitle("Product Inserted")
                .setMessage("Are you want to add again ?? ")
                .setIcon(R.drawable.confirm)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }).show();
    }

    public void clearActivity() {
        email.setText("");
        title.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        Image.setImageDrawable(getResources().getDrawable(R.drawable.pic));
    }
}
