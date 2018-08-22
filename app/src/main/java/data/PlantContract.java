package data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class PlantContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory_stage_two";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLANTS = "plants";

    private PlantContract() {
    }

    public static final class PlantEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANTS;
        public final static String TABLE_NAME = "plants";
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PLANT_NAME = "name";
        public final static String COLUMN_PLANT_PRICE = "price";
        public final static String COLUMN_PLANT_QUANTITY = "quantity";
        public final static String COLUMN_PLANT_SUPPLIER_NAME = "supplierName";
        public final static String COLUMN_PLANT_SUPPLIER_CONTACT = "supplierContact";


        public final static int SUPPLIER_UNKNOWN = 0;
        public final static int SUPPLIER_PATCH = 1;
        public final static int SUPPLIER_THE_JUNGLE = 2;
        public final static int SUPPLIER_CROCUS = 3;

        public static boolean isValidSupplierName(Integer supplierName) {
            if (supplierName == SUPPLIER_UNKNOWN || supplierName == SUPPLIER_PATCH ||
                    supplierName == SUPPLIER_THE_JUNGLE || supplierName == SUPPLIER_CROCUS) {
                return true;
            }
            return false;
        }
    }
}