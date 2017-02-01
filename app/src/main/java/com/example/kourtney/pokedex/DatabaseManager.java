package com.example.kourtney.pokedex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PokeDex";
    public static final int DATABASE_VERSION = 1;
    public static final String DEX_TABLE = "dexTable";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String BLURB = "blurb";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String TYPE1 = "type1";
    public static final String TYPE2 = "type2";
    public static final String DESC = "description";
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";

    private Context mContext;

    public DatabaseManager( Context c ) {
        super( c, DATABASE_NAME, null, DATABASE_VERSION );
        mContext = c;
    }

    public void onCreate( SQLiteDatabase db ) {
        String sqlCreate = "create table " + DEX_TABLE + "( "
                +  ID + " integer primary key," // autoincrement, "
                + NAME + " text, "
                + BLURB + " text, "
                + HEIGHT + " text, "
                + WEIGHT + " text, "
                + TYPE1 + " text, "
                + TYPE2 + " text, "
                + DESC + " real, "
                + IMAGE + " text, "
                + AUDIO + " text "
                +  " )";
        try {
            db.execSQL( sqlCreate );
        }
        catch ( SQLException se ) {
            Toast.makeText(mContext, se.getMessage(),
                    Toast.LENGTH_LONG).show( );
        }
    }

    public void onUpgrade( SQLiteDatabase db, int oldVersion,
                           int newVersion) {
    }

    public void insert( int id,
                        String name,
                        String blurb,
                        String height,
                        String weight,
                        String type1,
                        String type2,
                        String desc,
                        String image,
                        String audio ) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            // build column value
            ContentValues values = new ContentValues( );
            values.put( ID, id );
            values.put( NAME, name );
            values.put( BLURB, blurb );
            values.put( HEIGHT, height );
            values.put( WEIGHT, weight );
            values.put( TYPE1, type1 );
            values.put( TYPE2, type2 );
            values.put( DESC, desc );
            values.put( IMAGE, image );
            values.put( AUDIO, audio );


            long newId = db.insert(DEX_TABLE, null, values );

            if ( newId != (long) id ) {
                Toast.makeText(mContext, R.string.wr_error_save,
                        Toast.LENGTH_LONG ).show( );
            } // else {
                //  Toast.makeText(mContext, R.string.wr_success_save,
                //          Toast.LENGTH_LONG ).show( );
            // }
            db.close( );
        }
        catch ( SQLiteException se ) {
            Toast.makeText(mContext, se.getMessage(),
                    Toast.LENGTH_LONG ).show( );
        }
    }


    public ArrayList<String> selectBasic( ) {
        ArrayList<String> pokeList = new ArrayList<String> ();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sqlQuery = "select * from " + DEX_TABLE;

            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String oneRecord = cursor.getString(0) + " - " // id
                        + cursor.getString(1);         // name
                pokeList.add(oneRecord); // add to ArrayList
                cursor.moveToNext(); // move to next row
            }
            cursor.close( );
            db.close();
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        return pokeList;
    }

    public ArrayList<String> selectAlpha( ) {
        ArrayList<String> pokeList = new ArrayList<String> ();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sqlQuery = "select * from " + DEX_TABLE + " ORDER BY " + NAME + " ASC";

            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String oneRecord = cursor.getString(0) + " - " // id
                        + cursor.getString(1);         // name
                pokeList.add(oneRecord); // add to ArrayList
                cursor.moveToNext(); // move to next row
            }
            cursor.close( );
            db.close();
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        return pokeList;
    }

    public ArrayList<String> selectFilter( String filter, String criteria ) {
        ArrayList<String> pokeList = new ArrayList<String> ();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sqlQuery = "select * from " + DEX_TABLE + " where ";
            if ( !filter.equals( "type" )) {
                sqlQuery += filter + " = " + criteria;
            }
            else {
                if ( criteria.length() > 0 ) {
                    criteria = criteria.substring(0, 1).toUpperCase() + criteria.substring(1, criteria.length());
                    System.out.println(criteria);
                    sqlQuery += "(" + TYPE1 + " like '" + criteria + "' or " + TYPE2 + " like '" + criteria + "')";
                }
            }

            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String oneRecord = cursor.getString(0) + " - " // id
                        + cursor.getString(1);         // name
                pokeList.add(oneRecord); // add to ArrayList
                cursor.moveToNext(); // move to next row
            }
            cursor.close( );
            db.close();
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        return pokeList;
    }

    public boolean idExists( int id ) {
        boolean rt = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sqlQuery = "select * from " + DEX_TABLE + " where id = " + id;

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if ( cursor != null && cursor.getCount()>0 ) {
                rt = true;
            }
            cursor.close( );
            db.close();
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (NullPointerException npe ) {
            Toast.makeText(mContext, npe.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }

        return rt;
    }

    public void selectByID( int id ) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String sqlQuery = "select * from " + DEX_TABLE + " where id = " + id;

            ArrayList<String> returns = new ArrayList<String>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            for ( int i = 1; i < cursor.getColumnCount(); i++ ) {
                String pokeCell = cursor.getString(i);
                returns.add(pokeCell); // add to ArrayList
            }
            Pokemon.getInstance().setPokemon( id, returns.get(0), returns.get(1), returns.get(2),
                    returns.get(3), returns.get(4), returns.get(5), returns.get(6) );
            cursor.close( );
            db.close();
            return;
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
    }

    /*
    public ArrayList<String> selectByColumn( String column, String columnValue ) {
        ArrayList<String> dataList = new ArrayList<String>( );
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(DEX_TABLE, // table name
                    null,  // columns to return - > all
                    column + "= ?", //where clause
                    new String[] {columnValue},  // substitution for where
                    null, // group by
                    null, // having
                    null, // order by
                    null);  // limit

            if (cursor.getCount() == 0 ) {
                Toast.makeText(mContext, R.string.rd_error_no_data, Toast.LENGTH_LONG).show();
                return dataList;
            }
            cursor.moveToFirst();
            while (! cursor.isAfterLast( )) {
                String oneRecord = cursor.getString(0) + " - "
                        + cursor.getString(1) + ", "
                        + cursor.getString(2) + ":\n "
                        + cursor.getString(3);
                dataList.add(oneRecord);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();

        } catch (SQLException se) {
            Toast.makeText(mContext, se.getMessage(), Toast.LENGTH_LONG).show();
        }
        return dataList;
    }

    public ArrayAdapter<String> fillAutoCompleteTextFields( Context context,
                                                            String column) {
        ArrayAdapter<String> adapter = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(true, DEX_TABLE, new String[]{column},
                    null, null, null, null, null, null);

            int numberOfRecords = cursor.getCount();
            // build array from records returned
            if (numberOfRecords > 0 && cursor.moveToFirst()) {
                String[] options = new String[numberOfRecords];
                for (int i = 0; i < numberOfRecords; i++) {
                    options[i] = cursor.getString(0);
                    cursor.moveToNext();
                }

                adapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        options);
            }
            db.close();
        }
        catch ( SQLiteException sle ) {
            Toast.makeText(mContext, sle.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.toString(),
                    Toast.LENGTH_LONG ).show( );
        }
        return adapter;
    }

    */

    public void deleteTable() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String sqlQuery = "drop table " + DEX_TABLE;
            db.execSQL( sqlQuery );

            String sqlCreate = "create table " + DEX_TABLE + "( "
                    +  ID + " integer primary key," // autoincrement, "
                    + NAME + " text, "
                    + BLURB + " text, "
                    + HEIGHT + " text, "
                    + WEIGHT + " text, "
                    + TYPE1 + " text, "
                    + TYPE2 + " text, "
                    + DESC + " real, "
                    + IMAGE + " text, "
                    + AUDIO + " text "
                    +  " )";
            try {
                db.execSQL( sqlCreate );
            }
            catch ( SQLException se2 ) {
                Toast.makeText(mContext, se2.getMessage(),
                        Toast.LENGTH_LONG).show( );
            }

            db.close();
        }
        catch ( SQLException se ) {
            Toast.makeText(mContext, se.getMessage(),
                    Toast.LENGTH_LONG).show( );
        }
    }
}
