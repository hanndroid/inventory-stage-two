package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlantProvider extends ContentProvider {

    private static final int PLANTS = 100;
    private static final int PLANT_ID = 101;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(PlantContract.CONTENT_AUTHORITY, PlantContract.PATH_PLANTS, PLANTS);

        mUriMatcher.addURI(PlantContract.CONTENT_AUTHORITY, PlantContract.PATH_PLANTS + "/#", PLANT_ID);
    }

    private PlantDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PlantDbHelper((getContext()));
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = mUriMatcher.match(uri);
        switch (match) {
            case PLANTS:
                cursor = database.query(PlantContract.PlantEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLANT_ID:
                selection = PlantContract.PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PlantContract.PlantEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PLANTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Not supported insert for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PLANTS:
                return PlantContract.PlantEntry.CONTENT_LIST_TYPE;
            case PLANT_ID:
                return PlantContract.PlantEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match " + match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String nameProduct = values.getAsString(PlantContract.PlantEntry.COLUMN_PLANT_NAME);
        if (nameProduct == null) {
            throw new IllegalArgumentException("Please add a plant name");
        }

        Integer priceProduct = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_PRICE);
        if (priceProduct != null && priceProduct < 0) {
            throw new IllegalArgumentException("Please add a price");
        }

        Integer quantityProduct = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY);
        if (quantityProduct != null && quantityProduct < 0) {
            throw new IllegalArgumentException("Please add a quantity");
        }

        Integer supplierName = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_NAME);
        if (supplierName == null || !PlantContract.PlantEntry.isValidSupplierName(supplierName)) {
            throw new IllegalArgumentException("Please add the supplier's name");
        }

        Integer supplierPhone = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT);
        if (supplierPhone != null && supplierPhone < 0) {
            throw new IllegalArgumentException("Please add a phone number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(PlantContract.PlantEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PLANTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PLANT_ID:
                selection = PlantContract.PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PLANTS:
                rowsDeleted = database.delete(PlantContract.PlantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLANT_ID:
                selection = PlantContract.PlantEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PlantContract.PlantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PlantContract.PlantEntry.COLUMN_PLANT_NAME)) {
            String nameProduct = values.getAsString(PlantContract.PlantEntry.COLUMN_PLANT_NAME);
            if (nameProduct == null) {
                throw new IllegalArgumentException("Please add a plant name");
            }
        }
        if (values.containsKey(PlantContract.PlantEntry.COLUMN_PLANT_PRICE)) {
            Integer priceProduct = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_PRICE);
            if (priceProduct != null && priceProduct < 0) {
                throw new
                        IllegalArgumentException("Please add a plant price");
            }
        }

        if (values.containsKey(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY)) {
            Integer quantityProduct = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY);
            if (quantityProduct != null && quantityProduct < 0) {
                throw new
                        IllegalArgumentException("Please add a quantity");
            }
        }
        if (values.containsKey(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_NAME)) {
            Integer supplierName = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_NAME);
            if (supplierName == null || !PlantContract.PlantEntry.isValidSupplierName(supplierName)) {
                throw new IllegalArgumentException("Please add the supplier's name");
            }
        }

        if (values.containsKey(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT)) {
            Integer supplierPhone = values.getAsInteger(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new
                        IllegalArgumentException("Please add a phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PlantContract.PlantEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}