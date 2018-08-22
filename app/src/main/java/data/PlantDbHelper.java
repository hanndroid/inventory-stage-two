package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import data.PlantContract.PlantEntry;

public class PlantDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "plants.db";

    private static final int DATABASE_VERSION = 1;

    public PlantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table
        String SQL_CREATE_PLANT_TABLE = "CREATE TABLE " + PlantEntry.TABLE_NAME + " ("
                + PlantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlantEntry.COLUMN_PLANT_NAME + " TEXT NOT NULL, "
                + PlantEntry.COLUMN_PLANT_PRICE + " INTEGER NOT NULL, "
                + PlantEntry.COLUMN_PLANT_QUANTITY + " INTEGER NOT NULL, "
                + PlantEntry.COLUMN_PLANT_SUPPLIER_NAME + " TEXT NOT NULL DEFAULT 0, "
                + PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT + " TEXT );";

        db.execSQL(SQL_CREATE_PLANT_TABLE);

        Log.d("successful", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}