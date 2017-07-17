package com.example.minageorge.inventoryapp;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minageorge.inventoryapp.Adapters.ProductCursor;
import com.example.minageorge.inventoryapp.ProductsData.ProductsContract;
import com.example.minageorge.inventoryapp.ProductsData.ProductsProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnLongClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    private AlertDialog.Builder builder;
    private ProductCursor cursorAdapter;
    private ProductsContract.DetailsEntity contract;
    public boolean isInAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Products");
        builder = new AlertDialog.Builder(this);
        contract = new ProductsContract.DetailsEntity();
        cursorAdapter = new ProductCursor(this, null);
        listView.setAdapter(cursorAdapter);
        listView.setEmptyView(emptyView);
        getLoaderManager().initLoader(5, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Intent mIntent = new Intent(this, AddActivity.class);
                startActivity(mIntent);
                break;
            case android.R.id.home:
                clearToolbar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                contract.RecordID, contract.ProductTitle, contract.Quantity, contract.ProductPrice, contract.Picture, contract.SupplierEmail, contract.Supplier};
        return new CursorLoader(this,
                ProductsProvider.ContentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onLongClick(View view) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        isInAction = true;
        cursorAdapter.notifyDataSetChanged();
        return true;
    }

    public void deleteProducts(int id) {
        final String[] ids = new String[]{String.valueOf(id)};
        final String url = ProductsProvider.ContentUri.toString() + "/1";
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want delete this ")
                .setIcon(R.drawable.deletee)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deleted = getContentResolver().delete(Uri.parse(url), null, ids);
                        if (deleted > 0) {
                            Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            clearToolbar();
                        } else {
                            Toast.makeText(getBaseContext(), "Sorry Can't Delete this Products", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    public void clearToolbar() {
        isInAction = false;
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        cursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (isInAction) {
            clearToolbar();
        } else {
            super.onBackPressed();
        }
    }
}
