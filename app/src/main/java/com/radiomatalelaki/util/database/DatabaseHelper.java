package com.radiomatalelaki.util.database;

/**
 * Created by rianpradana on 5/26/17.
 */


import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // db path and name
    public String DB_PATH;
    public static final String DB_NAME = "events.sqlite";

    // DB instance
    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH = context.getApplicationContext().getFilesDir().getAbsolutePath()+"/";
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
            // or copy, if you want to refresh
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch(SQLiteException e){
            //database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * Create the database instance
     * @throws SQLException
     */
    public void openDataBase() throws SQLException{
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Does the query and gets all events that satisfies the parameters
     * @param centerLat the center location latitude
     * @param centerLon the center location longitude
     * @param distance the max distance
     * @param search the search text
     * @param calculateDistance calculate the distance for every point of interest
     * @return all events, depending on query parameters
     * @throws SQLiteException
     */
    public ArrayList<Events> getAllEvents(double centerLat, double centerLon, int distance, String search, boolean calculateDistance) throws SQLiteException {
        ArrayList<Events> eventi = new ArrayList<Events>();

		/* If a searchtext is defined */
        String searchText = "";
        if (search!=null && !search.isEmpty()) {
            searchText = searchText+" WHERE (title LIKE('%"+search+"%') OR address LIKE('%"+search+"%')) ";
        }

		/* Make a square filter to speedup the query. Useful for large database. */
        PointF center = new PointF((float) centerLat, (float) centerLon);
        if (distance>=0) {
            final double mult = 1.1; // mult = 1.1; is more reliable
            PointF p1 = Utils.calculateDerivedPosition(center, mult * distance * 1000, 0);
            PointF p2 = Utils.calculateDerivedPosition(center, mult * distance * 1000, 90);
            PointF p3 = Utils.calculateDerivedPosition(center, mult * distance * 1000, 180);
            PointF p4 = Utils.calculateDerivedPosition(center, mult * distance * 1000, 270);

            if (!searchText.isEmpty())
                searchText += " AND ";
            else
                searchText += " WHERE ";
            searchText += " lat > " + p3.x + " AND "
                    + " lat < " + p1.x + " AND "
                    + " lng < " + p2.y + " AND "
                    + " lng > " + p4.y;
        }

		/* Scan the cursor to populate the events list */
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM events " + searchText, new String[] {  });
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Events ev = cursorToEvent(cursor);
            if (calculateDistance) {
                ev.setDistance(Utils.getDistanceBetweenTwoPoints(center, new PointF((float) ev.getLat(),
                        (float) ev.getLng())));
            }

            if (distance>=0 && ev.getDistance() < distance * 1000) {
                eventi.add(ev);
            } else if (distance < 0) {
                eventi.add(ev);
            }

            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();

        if(calculateDistance) {
            // sort by distance
            Collections.sort(eventi, new EventDistanceComparator());
        } else {
            // sort by title
            Collections.sort(eventi, new EventTitleComparator());
        }

        return eventi;
    }

    /**
     * Gets the event by id.
     * @param id The id of event in the database
     * @return the event
     */
    public Events getEvent(int id) {
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM events WHERE id="+id, new String[] {  });
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Events ev = cursorToEvent(cursor);
            cursor.close();
            return ev;
        }
        // Make sure to close the cursor
        cursor.close();
        return null;
    }

    /* Instantiate a Event from the cursor, reading the columns values */
    private Events cursorToEvent (Cursor cursor) {
        Events e = new Events();
        e.setId("" + cursor.getInt(0));
        e.setTitle(cursor.getString(1));
        e.setAddress(cursor.getString(2));
        e.setBrief_description(cursor.getString(3));
        e.setDescription(cursor.getString(4));
        e.setLat(cursor.getDouble(5));
        e.setLng(cursor.getDouble(6));
        e.setPhone(cursor.getString(7));
        e.setEmail(cursor.getString(8));
        e.setUrl(cursor.getString(9));
        e.setStart_date_time(cursor.getString(10));
        e.setEnd_date_time(cursor.getString(11));

        return e;
    }

}