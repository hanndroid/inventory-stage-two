package com.example.android.inventory_stage_two;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import data.PlantContract;
import data.PlantContract.PlantEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PLANT_LOADER = 0;
    private Uri mCurrentPlantUri;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private Button increase;
    private Button decrease;
    private Button order;
    private EditText mPriceEditText;
    private Spinner mSupplierNameSpinner;
    private EditText mSupplierContactEditText;

    private int plantQuantity = 0;

    private int mSupplierName = PlantEntry.SUPPLIER_UNKNOWN;
    private boolean mPlantHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPlantHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentPlantUri = intent.getData();

        if (mCurrentPlantUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_plant));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_plant));

            getLoaderManager().initLoader(EXISTING_PLANT_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.plant_name_edit_text);
        mPriceEditText = findViewById(R.id.plant_price_edit_text);
        mQuantityEditText = findViewById(R.id.plant_quantity_edit_text);
        increase = findViewById(R.id.increase_button);
        decrease = findViewById(R.id.decrease_button);
        order = findViewById(R.id.call_button);
        mSupplierNameSpinner = findViewById(R.id.plant_supplier_name_spinner);
        mSupplierContactEditText = findViewById(R.id.plant_supplier_contact_edit_text);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mTouchListener);
        mSupplierContactEditText.setOnTouchListener(mTouchListener);

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuantityEditText.setText(Integer.toString(++plantQuantity));
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantQuantity > 0) {
                    mQuantityEditText.setText(Integer.toString(--plantQuantity));
                } else {
                    Toast.makeText(mQuantityEditText.getContext(), getString(R.string.quantity_cannot_be_zero), Toast.LENGTH_SHORT).show();
                }

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = String.valueOf(R.string.supplierContact);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }

        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSupplierNameSpinner.setAdapter(supplierSpinnerAdapter);

        mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.plant_supplier_patch))) {
                        mSupplierName = PlantContract.PlantEntry.SUPPLIER_PATCH;
                    } else if (selection.equals(getString(R.string.plant_supplier_the_jungle))) {
                        mSupplierName = PlantContract.PlantEntry.SUPPLIER_THE_JUNGLE;
                    } else if (selection.equals(getString(R.string.plant_supplier_crocus))) {
                        mSupplierName = PlantContract.PlantEntry.SUPPLIER_CROCUS;
                    } else {
                        mSupplierName = PlantContract.PlantEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = PlantContract.PlantEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    private void savePlant() {
        String plantNameString = mNameEditText.getText().toString().trim();
        String plantQuantityString = mQuantityEditText.getText().toString().trim();
        String plantPriceString = mPriceEditText.getText().toString().trim();
        String plantSupplierContactString = mSupplierContactEditText.getText().toString().trim();
        if (mCurrentPlantUri == null) {
            if (TextUtils.isEmpty(plantNameString)) {
                Toast.makeText(this, getString(R.string.please_add_plant_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantPriceString)) {
                Toast.makeText(this, getString(R.string.please_add_a_price), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantQuantityString)) {
                Toast.makeText(this, getString(R.string.please_add_a_quantity), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSupplierName == PlantEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.please_add_the_supplier_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantSupplierContactString)) {
                Toast.makeText(this, getString(R.string.please_add_supplier_contact), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(PlantEntry.COLUMN_PLANT_NAME, plantNameString);
            values.put(PlantEntry.COLUMN_PLANT_PRICE, plantPriceString);
            values.put(PlantEntry.COLUMN_PLANT_QUANTITY, plantQuantityString);
            values.put(PlantEntry.COLUMN_PLANT_SUPPLIER_NAME, mSupplierName);
            values.put(PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT, plantSupplierContactString);

            Uri newUri = getContentResolver().insert(PlantEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {

            if (TextUtils.isEmpty(plantNameString)) {
                Toast.makeText(this, getString(R.string.please_add_plant_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantQuantityString)) {
                Toast.makeText(this, getString(R.string.please_add_a_quantity), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantPriceString)) {
                Toast.makeText(this, getString(R.string.please_add_a_price), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSupplierName == PlantEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.please_add_the_supplier_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(plantSupplierContactString)) {
                Toast.makeText(this, getString(R.string.please_add_supplier_contact), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(PlantContract.PlantEntry.COLUMN_PLANT_NAME, plantNameString);
            values.put(PlantContract.PlantEntry.COLUMN_PLANT_PRICE, plantPriceString);
            values.put(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY, plantQuantityString);
            values.put(PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_NAME, mSupplierName);
            values.put(PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT, plantSupplierContactString);

            int rowsAffected = getContentResolver().update(mCurrentPlantUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                savePlant();
                return true;
            case android.R.id.home:
                if (!mPlantHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mPlantHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PlantContract.PlantEntry._ID,
                PlantContract.PlantEntry.COLUMN_PLANT_NAME,
                PlantContract.PlantEntry.COLUMN_PLANT_PRICE,
                PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY,
                PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_NAME,
                PlantContract.PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT
        };
        return new CursorLoader(this,
                mCurrentPlantUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_SUPPLIER_CONTACT);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
            final int currentSupplierContact = cursor.getInt(supplierPhoneColumnIndex);

            final int idColumnIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
            final int plantQuantity = cursor.getInt(quantityColumnIndex);


            mNameEditText.setText(currentName);
            mPriceEditText.setText(Integer.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(plantQuantity));

            mSupplierContactEditText.setText(Integer.toString(currentSupplierContact));

            switch (currentSupplierName) {
                case PlantEntry.SUPPLIER_PATCH:
                    mSupplierNameSpinner.setSelection(1);
                    break;
                case PlantEntry.SUPPLIER_THE_JUNGLE:
                    mSupplierNameSpinner.setSelection(2);
                    break;
                case PlantEntry.SUPPLIER_CROCUS:
                    mSupplierNameSpinner.setSelection(3);
                    break;
                default:
                    mSupplierNameSpinner.setSelection(0);
                    break;
            }

            Button productIncreaseButton = findViewById(R.id.increase_button);
            productIncreaseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    increaseCount(idColumnIndex, plantQuantity);
                }
            });

            Button productDecreaseButton = findViewById(R.id.decrease_button);
            productDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseCount(idColumnIndex, plantQuantity);
                }
            });

            Button orderButton = findViewById(R.id.call_button);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(currentSupplierContact);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.getText().clear();
        mPriceEditText.getText().clear();
        mQuantityEditText.getText().clear();
        mSupplierContactEditText.getText().clear();
        mSupplierNameSpinner.setSelection(0);
    }

    public void decreaseCount(int plantID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            updatePlant(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_changed_toast), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.quantity_completed_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseCount(int plantID, int productQuantity) {
        productQuantity = productQuantity + 1;
        if (productQuantity >= 0) {
            updatePlant(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_changed_toast), Toast.LENGTH_SHORT).show();

        }
    }

    private void updatePlant(int productQuantity) {

        if (mCurrentPlantUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_PLANT_QUANTITY, productQuantity);

        if (mCurrentPlantUri == null) {
            Uri newUri = getContentResolver().insert(PlantContract.PlantEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentPlantUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePlant() {
        if (mCurrentPlantUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentPlantUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteEntry);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePlant();
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