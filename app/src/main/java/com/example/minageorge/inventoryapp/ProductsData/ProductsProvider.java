package com.example.minageorge.inventoryapp.ProductsData;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Mina George on 7/11/2017.
 */

public class ProductsProvider extends ContentProvider {
    static final String ProviderName = "com.example.inventory";
    static final String Product = "products";
    static final String Url = "content://" + ProviderName + "/" + Product;
    public static final Uri ContentUri = Uri.parse(Url);
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ProviderName + "/" + Product;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ProviderName + "/" + Product;
    static final int product = 1;
    static final int products = 2;
    static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ProviderName, "products", products);
        URI_MATCHER.addURI(ProviderName, "products/#", product);
    }

    private SQLiteDatabase db;
    private ProductsDB.productDetails details;
    private ProductsContract.DetailsEntity contract;

    @Override
    public boolean onCreate() {
        contract = new ProductsContract.DetailsEntity();
        details = new ProductsDB.productDetails(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        db = details.getReadableDatabase();
        Cursor cursor;
        int urMatch = URI_MATCHER.match(uri);
        switch (urMatch) {
            case product:
                selection = contract.RecordID + "=?";
                cursor = db.query(contract.table_Name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case products:
                cursor = db.query(contract.table_Name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Not found" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        db = details.getWritableDatabase();
        Long rowId = db.insert(contract.table_Name, null, values);
        if (rowId > 0) {
            Uri uri1 = ContentUris.withAppendedId(ContentUri, rowId);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }
        throw new SQLException("failed to insert record" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted;
        int urMatch = URI_MATCHER.match(uri);

        switch (urMatch) {
            case product:
                selection = contract.RecordID + "=?";
                deleted = db.delete(contract.table_Name, selection, selectionArgs);
                break;
            case products:
                deleted = db.delete(contract.table_Name, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Not deleted" + uri);
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int Updated;
        int urMatch = URI_MATCHER.match(uri);
        switch (urMatch) {
            case product:
                selection = contract.RecordID + "=?";
                Updated = db.update(contract.table_Name, values, selection, selectionArgs);
                break;
            case products:
                Updated = db.update(contract.table_Name, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Not Updated" + uri);
        }
        if (Updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Updated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int UrMatch = URI_MATCHER.match(uri);
        switch (UrMatch) {
            case products:
                return CONTENT_LIST_TYPE;
            case product:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Not found" + uri);
        }
    }
}
