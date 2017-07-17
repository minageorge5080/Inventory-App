package com.example.minageorge.inventoryapp.ProductsData;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mina George on 7/11/2017.
 */

public class ProductsDB {
    static final String dataBase_Name = "inventory";
    static final int dataBase_Version = 1;

    public static class productDetails extends SQLiteOpenHelper {

        public productDetails(Context context) {
            super(context, dataBase_Name, null, dataBase_Version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(ProductsContract.DetailsEntity.CreateTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(ProductsContract.DetailsEntity.DropTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
