package com.example.android.inventory_stage_two;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import data.PlantContract;
import data.PlantContract.PlantEntry;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLANT_LOADER = 0;

    PlantCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView plantListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        plantListView.setEmptyView(emptyView);

        mCursorAdapter = new PlantCursorAdapter(this, null);
        plantListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        plantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPlantUri = ContentUris.withAppendedId(PlantEntry.CONTENT_URI, id);
                intent.setData(currentPlantUri);
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PLANT_LOADER, null, this);
    }

    private void insertPlant() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's plant attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_PLANT_NAME, "Fleur");
        values.put(PlantContract.PlantEntry.COLUMN_PLANT_PRICE, "7");
        values.put(PlantEntry.COLUMN_PLANT_SUPPLIER_NAME, PlantEntry.SUPPLIER_CROCUS);
        values.put(PlantEntry.COLUMN_PLANT_QUANTITY, 3);
        values.put(PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT, 569001578);

        // Insert a new row for Fleur into the provider using the ContentResolver.
        // Use the {@link PlantEntry#CONTENT_URI} to indicate that we want to insert
        // into the plants database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PlantContract.PlantEntry.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPlant();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                confirmDelete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_PLANT_NAME,
                PlantEntry.COLUMN_PLANT_PRICE,
                PlantEntry.COLUMN_PLANT_QUANTITY};

        return new CursorLoader(this,
                PlantContract.PlantEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PlantCursorAdapter} with this new cursor containing updated plant data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public void plantSale(int plantID, int quantity) {
        quantity = quantity - 1;
        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(PlantContract.PlantEntry.CONTENT_URI, plantID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Quantity is already zero", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAllPlants() {
        int rowsDeleted = getContentResolver().delete(PlantContract.PlantEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + " " + getString(R.string.deleted),
                Toast.LENGTH_SHORT).show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_deletion);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllPlants();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}