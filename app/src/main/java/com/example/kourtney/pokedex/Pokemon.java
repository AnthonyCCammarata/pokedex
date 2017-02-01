package com.example.kourtney.pokedex;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Pokemon { // extends AppCompatActivity {

    private int mId;
    private String mName;
    private String mBlurb;
    private String mHeight;
    private String mWeight;
    private String mType1; // TODO encapsulate types in a single int, for memory
    private String mType2;
    private String mDesc ;
    private ArrayList<Integer> mFavorites;
    private File mFilesDir;

    private static Pokemon mInstance = new Pokemon();

    private Pokemon() {
    }

    public static Pokemon getInstance() {
        return mInstance;
    }

    public void setPokemon( int id, String name, String blurb, String height, String weight,
                    String type1, String type2, String desc ) {
        mId = id;
        mName = name;
        mBlurb = blurb;
        mHeight = height;
        mWeight = weight;
        mType1 = type1;
        mType2 = type2;
        mDesc = desc;

    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getBlurb() {
        return mBlurb;
    }

    public String getHeight() {
        return mHeight;
    }

    public String getWeight() {
        return mWeight;
    }

    public String getType1() {
        return mType1;
    }

    public String getType2() {
        return mType2;
    }

    public String getDesc() {
        return mDesc;
    }

    public String toString() {
        String returnString = mId + " - " + mName + ":\n" + mBlurb + " Pokemon\n" + mDesc;
        return returnString;
    }

    public boolean getFav() {
        return mFavorites.contains( mId );
    }
     public void setFav( boolean fav ) {
         if (fav) {
             mFavorites.add( mId );
         }
         else {
             mFavorites.remove( (Integer) mId );
         }
     }

    public void loadFavs( ) {
        mFavorites = new ArrayList<Integer>();

        try {
            FileInputStream fis = new FileInputStream( new File(mFilesDir, "favorites.txt" ) );
            Scanner scan = new Scanner( fis );

            while ( scan.hasNext() ) {
                String nexts = scan.next();
                mFavorites.add( Integer.parseInt( nexts ) );
            }
            scan.close();
            fis.close();
        }
        catch ( FileNotFoundException fnfe ) {
            try {
                FileOutputStream fos = new FileOutputStream(new File (mFilesDir, "favorites.txt" ));
                mFavorites = new ArrayList<Integer>();
                fos.close();
            }
            catch ( FileNotFoundException fnfe2 ) {
                mFavorites = new ArrayList<Integer>();
            }
            catch ( IOException io2 ) {
                // catch?
            }
        }
        catch ( IOException io ) {
            // catch?
        }
    }

    public void updateFavs() {
        try {
            FileOutputStream fos = new FileOutputStream( new File(mFilesDir, "favorites.txt" ) );
            OutputStreamWriter osw = new OutputStreamWriter( fos );
            for ( Integer i : mFavorites ) {
                 osw.write(i.toString() + " ");
            }
            System.out.println("Update successful.");
            osw.close();
            fos.close();
         }
        catch ( FileNotFoundException fnfe ) {
            System.out.println("Update failure." + fnfe);
            //catch?
        }
        catch ( IOException io ) {
            //catch?
        }
    }

    public ArrayList<Integer> getALFavs() {
        return mFavorites;
    }

    public void setFilesDir( File file ) {
        mFilesDir = file;
    }
}