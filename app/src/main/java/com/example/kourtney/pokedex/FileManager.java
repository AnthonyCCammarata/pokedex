package com.example.kourtney.pokedex;

import android.database.sqlite.SQLiteException;

import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FileManager {

    public FileManager() {
    }

    public void getList( DatabaseManager db, InputStream is ) {

        try {
            Scanner scan = new Scanner(is);
            scan.useDelimiter( ";" );
            scan.nextLine(); // Jump past header.
            while (scan.hasNextLine()) {
                String id = scan.next();
                if (!db.idExists( Integer.parseInt(id) )) {
                    db.insert(Integer.parseInt(id), scan.next(), scan.next(), scan.next(),
                            scan.next(), scan.next(), scan.next(), scan.skip(";").nextLine(),
                            id + ".gif", id + ".wav");
                }
                else {
                    scan.nextLine(); // Skip whole line
                }
            }
        }
        catch( NullPointerException npe ) {
            // TODO catch me!
        }
        catch( InputMismatchException ime ) {
            // TODO catch me!
        }
        catch( SQLiteException sqe ) {
            // TODO catch me!
        }

    }
}
