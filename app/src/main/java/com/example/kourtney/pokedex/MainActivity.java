package com.example.kourtney.pokedex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager mDb;
    private GridLayout pokeList;
    private LinearLayout loadLayout;
    private int mChosen;
    private int mFilterBy = 0;
    private String mTypeToFind;
    private SharedPreferences mPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mSettingsListener;

    private final int BUTTON_HEIGHT = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setPrefs();

        mSettingsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged( SharedPreferences prefs, String key ) {
                setPrefs();
            }
        };
        mPrefs.registerOnSharedPreferenceChangeListener(mSettingsListener);

        loadLayout = (LinearLayout) findViewById( R.id.loading );
        loadLayout.setVisibility(LinearLayout.VISIBLE);
        toggleButtons(false);
        LoadTask lt = new LoadTask();
        lt.execute(this);
    }

    public void setPrefs() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences( this );

        String[] filter_values = getResources( ).
                getStringArray(R.array.sort_type);
        String filterInd = mPrefs.getString("filter_type", filter_values[1]);
        if ( filterInd.equals( filter_values[0] ) ) {
            mFilterBy = 0;
        }
        else if ( filterInd.equals( filter_values[1] ) ) {
            mFilterBy = 1;
        }
        else if ( filterInd.equals( filter_values[2] ) ) {
            mFilterBy = 2;
            String[] type_values = getResources().getStringArray(R.array.types);
            mTypeToFind = mPrefs.getString("pokemon_type_filter", type_values[0]);
        }
        else if ( filterInd.equals( filter_values[3] ) ) {
            mFilterBy = 3;
        }
    }

    public void createButtons( ArrayList<String> list ) {

        //  get reference to main activity layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.db_contents);

        // setup layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // get window width
        Point size = new Point( );
        getWindowManager( ).getDefaultDisplay( ).getSize( size );
        int paddingLeft = layout.getPaddingLeft();
        int paddingRight = layout.getPaddingRight();
        int windowWidth = size.x - paddingLeft - paddingRight;

        // set up GridLayout for letter buttons
        pokeList = new GridLayout(this);
        pokeList.setColumnCount(1); // 3 if we add types

        // add buttons
        if ( list.size() > 0 ) {
            pokeList.setRowCount(list.size());
            for (String entry : list) {
                Button b = new Button(this);
                b.setText(entry);
                b.setOnClickListener(entryClick);
                b.setTextSize(12);

                pokeList.addView(b, (int) (windowWidth / 1.5), BUTTON_HEIGHT);

            /*
            // TODO grab types from database and make these ImageViews
            TextView t = new TextView(this);
            t.setText("Type1");
            t.setTextSize(12);
            pokeList.addView(t, windowWidth/8, BUTTON_HEIGHT );
            TextView t2 = new TextView(this);
            t2.setText("Type2");
            t2.setTextSize(12);
            pokeList.addView(t2, windowWidth/8, BUTTON_HEIGHT );
            */
            }
        }
        else {
            pokeList.setRowCount(1);
            TextView tv = new TextView( this );
            tv.setText( R.string.no_found );
            tv.setTextSize( 30.0f );
            pokeList.addView( tv );
        }
        // add to layout
        pokeList.setLayoutParams(params);

    }

    public void assignButtons() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.db_contents);
        layout.addView(pokeList);
    }

    // entry button listener
    View.OnClickListener entryClick = new View.OnClickListener() {
        public void onClick(View v) {
            toggleButtons( false );
            loadLayout.setVisibility(LinearLayout.VISIBLE);

            Button chosenOne = (Button) v;
            String chosenString = chosenOne.getText().toString();
            chosenString = chosenString.substring(0, chosenString.indexOf("-") - 1);
            mChosen = Integer.parseInt(chosenString);

            loadEntry( v );

            toggleButtons(true);
            loadLayout.setVisibility(LinearLayout.INVISIBLE);
        }
    };

    private void removeButtons() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.db_contents);
        layout.removeAllViews();
    }

    private void toggleButtons( boolean tog ) {
        if ( !(pokeList == null )) {
            for (int i = 0; i < pokeList.getChildCount(); i++) {
                View v = pokeList.getChildAt(i);
                if ( v.getClass().getName().equals( "Button" )) {
                    v.setEnabled(tog);
                }
            }
        }
        Button b = (Button) findViewById( R.id.filter_button );
        b.setEnabled( tog );
        b = (Button) findViewById( R.id.refresh_button );
        b.setEnabled( tog );
        b = (Button) findViewById( R.id.search_button );
        b.setEnabled(tog);
    }

    private void loadEntry( View v ) {
        EntryLoad el = new EntryLoad();
        el.execute();
        loadPokemon(v);
    }

    public void searchPokemon( View v ) {
        toggleButtons(false);
        removeButtons();
        loadLayout.setVisibility(LinearLayout.VISIBLE);
        if ( mFilterBy == 1 ) { // All Pokemon, number ordered
            new FilterNormal().execute();
        }
        else if ( mFilterBy == 0 ) { // Alpha
            new FilterAlpha().execute();
        }
        else if ( mFilterBy == 2 ) { // Type
            new FilterType().execute();
        }
        else { // Favs
            new FilterByFavs().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean readRaw( Context c ) {
        try {
            InputStream is = MainActivity.this.getAssets().open("pokedex.csv");

            FileManager fm = new FileManager();
            fm.getList( mDb, is );

            is.close();
        }
        catch (IOException e) {
            Toast.makeText( c, R.string.error_csv, Toast.LENGTH_LONG ).show();
            return false;
        }
        return true;
    }

    private class LoadTask extends AsyncTask<Context, Integer, Integer> {
        protected Integer doInBackground( Context... c ) {
            publishProgress(0);

            // Load Favorites File
            Pokemon.getInstance().setFilesDir(getFilesDir());
            Pokemon.getInstance().loadFavs();
            publishProgress(20);

            // Instantiate Table
            mDb = new DatabaseManager( c[0] );
            publishProgress(40);

            // Populate Table
            if ( !readRaw( c[0] ) ) { return -1; }
            publishProgress(60);

            ArrayList<String> basicList;

            // Pull Name/Number
            if ( mFilterBy == 0 ) {
                basicList = mDb.selectAlpha();
            }
            else if ( mFilterBy == 1 ) {
                basicList = mDb.selectBasic();
            }
            else if ( mFilterBy == 2 ) {
                basicList = mDb.selectFilter("type", mTypeToFind);
            }
            else if ( mFilterBy == 3 ) {
                ArrayList<Integer> favs = Pokemon.getInstance().getALFavs();
                publishProgress(65);

                // Pull Name/Number
                basicList = mDb.selectBasic();
                publishProgress(75);

                // Filter Out Non-Favorites
                for ( int i = 0; i < basicList.size(); i++ ) {
                    String tempString = basicList.get(i);
                    int temp = Integer.parseInt(tempString.substring(0, tempString.indexOf("-") - 1));
                    if ( !favs.contains( temp ) ) {
                        basicList.remove(i);
                        i--;
                    }
                }
            }
            else {
                basicList = new ArrayList<String>();
            }
            publishProgress(90);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons( true );
        }
    }

    private class RefreshAsync extends AsyncTask< Context, Integer, Integer > {
        protected Integer doInBackground( Context... c ) {
            publishProgress(0);

            // Delete Table
            if ( ( Looper.myLooper() == null ) ) {
                Looper.prepare();
            }
            mDb.deleteTable();
            publishProgress(20);

            // Load Favs File
            Pokemon.getInstance().setFilesDir(getFilesDir());
            Pokemon.getInstance().loadFavs();
            publishProgress(40);

            // Populate Table
            if ( !readRaw( c[0] ) ) { return -1; }
            publishProgress(60);

            // Pull Name/Number
            ArrayList<String> basicList = mDb.selectBasic();
            publishProgress(90);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons( true );
        }
    }

    public void refreshList( View v ) {
        toggleButtons( false );
        removeButtons();
        loadLayout.setVisibility( LinearLayout.VISIBLE );
        RefreshAsync ra = new RefreshAsync();
        ra.execute( this );
    }

    private class EntryLoad extends AsyncTask< Void, Integer, Integer > {
        protected Integer doInBackground( Void... unused ) {

            mDb.selectByID( mChosen );
            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
        }
    }

    private class FilterNormal extends AsyncTask< Context, Integer, Integer > {
        protected Integer doInBackground( Context... c ) {
            if ( ( Looper.myLooper() == null ) ) {
                Looper.prepare();
            }
            publishProgress(0);

            // Pull Name/Number
            ArrayList<String> basicList = mDb.selectBasic();
            publishProgress(50);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons(true);
        }
    }

    private class FilterAlpha extends AsyncTask< Context, Integer, Integer > {
        protected Integer doInBackground( Context... c ) {
            if ( ( Looper.myLooper() == null ) ) {
                Looper.prepare();
            }
            publishProgress(0);

            // Pull Name/Number
            ArrayList<String> basicList = mDb.selectAlpha();
            publishProgress(50);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons(true);
        }
    }

    private class FilterType extends AsyncTask< Context, Integer, Integer > {
        protected Integer doInBackground( Context... c ) {
            if ( ( Looper.myLooper() == null ) ) {
                Looper.prepare();
            }
            publishProgress(0);

            // Pull Name/Number
            ArrayList<String> basicList = mDb.selectFilter("type", mTypeToFind);
            publishProgress(50);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons(true);
        }
    }

    private class FilterByFavs extends AsyncTask< Context, Integer, Integer > {
        protected Integer doInBackground( Context... c ) {
            publishProgress(0);

            // Pull Favs
            ArrayList<Integer> favs = Pokemon.getInstance().getALFavs();
            publishProgress(20);

            // Pull Name/Number
            ArrayList<String> basicList = mDb.selectBasic();
            publishProgress(50);

            // Filter Out Non-Favorites
            for ( int i = 0; i < basicList.size(); i++ ) {
                String tempString = basicList.get(i);
                int temp = Integer.parseInt(tempString.substring(0, tempString.indexOf("-") - 1));
                if ( !favs.contains( temp ) ) {
                    basicList.remove(i);
                    i--;
                }
            }
            publishProgress(80);

            // Create Buttons
            createButtons(basicList);
            publishProgress(100);

            return 0;
        }

        protected void onProgressUpdate( Integer ... progress ) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
            pb.setProgress( progress[0] );
        }

        protected void onPostExecute( Integer result ) {
            loadLayout.setVisibility( LinearLayout.INVISIBLE );
            assignButtons();
            toggleButtons(true);
        }
    }

    //KOURTNEY/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** handler for settings button */
    public void loadSettings(View v){
        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
    }

    public void loadPokemon(View v){
        Intent i = new Intent(getApplicationContext(), DexView.class );
        startActivity(i);
    }
}
