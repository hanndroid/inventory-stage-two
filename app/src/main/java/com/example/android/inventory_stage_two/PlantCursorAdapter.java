package com.example.android.inventory_stage_two;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import data.PlantContract;
import data.PlantContract.PlantEntry;

public class PlantCursorAdapter extends CursorAdapter {

    public PlantCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        // Find the columns of attributes that we're interested in
        final int columnID = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_PLANT_QUANTITY);

        final String plantID = cursor.getString(columnID);
        final String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);

        // Read the attributes from the Cursor for the current plant
        String name = cursor.getString(nameColumnIndex);
        priceTextView.setText(context.getString(R.string.plant_price) + "£" + price);
        quantityTextView.setText(context.getString(R.string.plant_quantity) + quantity);

        nameTextView.setText(name);


        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CatalogActivity Activity = (CatalogActivity) context;
                Activity.plantSale(Integer.valueOf(plantID), Integer.valueOf(quantity));
            }
        });
    }
}