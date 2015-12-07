package han.recyclerviewdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by xinchi on 2015/12/2.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private static String LOG_TAG = DataBaseHelper.class.getSimpleName();

    private Context context;

    public static String DATABASE_NAME = "database";
    public static String TABLE_NAME = "tableinterested";
    public static String TABLE_NAME_SAVED = "tablesaved";
    public static String ITEM_COLUMN_ID = "place_id";
    public static String ITEM_COLUMN_ID_SAVED = "saved_id";
    public static String ITEM_COLUMN_DETAIL = "place_detail";
    public static String ITEM_COLUMN_NAME = "saved_name";


    public static int VERSION = 1;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ITEM_COLUMN_ID + " TEXT PRIMARY KEY, " +
                ITEM_COLUMN_DETAIL + " TEXT)";
        Log.v(LOG_TAG, "createTableQuery: " + createTableQuery);

        Log.v(LOG_TAG, "onCreate(), DatabaseHelper");

        db.execSQL(createTableQuery);

        final String createSavedTable = "CREATE TABLE " + TABLE_NAME_SAVED + " (" +
                ITEM_COLUMN_ID_SAVED + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_COLUMN_NAME + " TEXT)";

        db.execSQL(createSavedTable);

        ContentValues cv = new ContentValues();
        cv.put(ITEM_COLUMN_NAME, "food");
        db.insert(TABLE_NAME_SAVED, null,cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String upgradeTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(upgradeTableQuery);
        String upgradeSavedTable = "DROP TABLE IF EXISTS " + TABLE_NAME_SAVED;
        db.execSQL(upgradeSavedTable);
    }

    public boolean insertDetail(String id,String detail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_COLUMN_ID, id);
        cv.put(ITEM_COLUMN_DETAIL, detail);
        db.insert(TABLE_NAME, null, cv);
        return true;
    }

    public boolean insertName(String tag_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_COLUMN_NAME, tag_name);
        db.insert(TABLE_NAME_SAVED, null, cv);
        return true;
    }

    public void deleteDetail(String id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,
                TABLE_NAME + "." + ITEM_COLUMN_ID + " = ? ",
                new String[]{id});
    }

    public void deleteName(String name) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_SAVED,
                TABLE_NAME_SAVED + "." + ITEM_COLUMN_NAME + " = ? ",
                new String[]{name});
    }
    public Cursor getAllDetails(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tableinterested",null);
        return res;
    }
    public Cursor getDetail(String id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tableinterested where place_id ='"+id+"'",null);
        return res;
    }

    public Cursor getName(String id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tablesaved where saved_name ='"+id+"'",null);
        return res;
    }
    public Cursor getAllNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =db.rawQuery("select "+ITEM_COLUMN_NAME+" from tablesaved",null);
        return res;
    }
}
