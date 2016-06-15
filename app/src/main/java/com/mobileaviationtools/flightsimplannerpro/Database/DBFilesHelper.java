package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public class DBFilesHelper {

    public static String DatabasePath(Context context)
    {
        String p = context.getFilesDir().getPath() + File.pathSeparator
                + "Databases" + File.pathSeparator;
        File f = new File(p);
        if (!f.exists()) f.mkdir();
        return p;
    }

    public static void CopyDatabases(Context context)
    {
        String dest = DatabasePath(context);
        try {
            CopyFromAssetsToStorage(context, "airspaces.db", dest + "airspaces.db");
            Log.e("DatabaseFile", "airspaces.db copied to: " + dest);
        } catch (IOException e1) {
            Log.e("Filedir", "Error copying map files");
            e1.printStackTrace();
        }
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
