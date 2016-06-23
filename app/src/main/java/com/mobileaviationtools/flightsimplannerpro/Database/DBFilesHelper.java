package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public class DBFilesHelper {

    public static String DatabasePath(Context context)
    {
        String p = context.getFilesDir().getPath() + "/"
                + "Databases" + "/";
        File f = new File(p);
        if (!f.exists()) f.mkdir();
        return p;
    }

    public static ArrayList<String> CopyDatabases(Context context)
    {
        ArrayList<String> databases = new ArrayList<>();
        String dest = DatabasePath(context);
        try {
            String [] list = context.getAssets().list("");
            for (String f : list) {
                if (f.contains("airspaces")) {
                    databases.add(f);
                    CopyFromAssetsToStorage(context, f, dest + f);
                    Log.e("DatabaseFile", f+ " copied to: " + dest);
                }
            }
        } catch (IOException e1) {
            Log.e("Filedir", "Error copying map files");
            e1.printStackTrace();
        }
        return databases;
    }

    private static void CopyFromAssetsToStorage(Context context, String SourceFile, String DestinationFile) throws IOException {
        InputStream IS = context.getAssets().open(SourceFile);
        OutputStream OS = new FileOutputStream(DestinationFile);
        CopyStream(IS, OS);
        OS.flush();
        OS.close();
        IS.close();
    }

    private static void CopyStream(InputStream In, OutputStream Out) throws IOException {
        byte[] buffer = new byte[5120];
        int lenght = In.read(buffer);
        while (lenght >0){
            Out.write(buffer, 0, lenght);
            lenght = In.read(buffer);
        }

    }
}
