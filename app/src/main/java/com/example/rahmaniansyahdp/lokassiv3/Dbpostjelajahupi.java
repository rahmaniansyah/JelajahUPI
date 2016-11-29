package com.example.rahmaniansyahdp.lokassiv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rahmaniansyah DP on 27/11/2016.
 */

public class Dbpostjelajahupi {

    //class untuk menyimpan record
    public static class Lokasi{
        public String nama_lokasi ;
        public Double left_e ;
        public Double right_e ;
        public Double up_s ;
        public Double down_s ;
    }

    private SQLiteDatabase db ;
    private final OpenHelper dbHelper ;

    public Dbpostjelajahupi (Context c){
        dbHelper = new OpenHelper(c) ;
    }

    public void open(){
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public long insertLokasiJelajahUpi(String nama_lokasi, String left_e, String right_e, String up_s, String down_s){
        ContentValues newValues = new ContentValues() ;
        newValues.put("NM_LOKASI", nama_lokasi);
        newValues.put("LEFT_E", left_e);
        newValues.put("RIGHT_E", right_e);
        newValues.put("UP_S", up_s);
        newValues.put("DOWN_S", down_s);
        return db.insert("POSTJELAJAHUPI", null, newValues) ;
    }

    //ambil data lokasi
    public Lokasi[] getAllLokasi(){
        Cursor cur = null ;

        //kolom yang diambil
        String[] cols = new String[]{"ID","NM_LOKASI","LEFT_E","RIGHT_E","UP_S","DOWN_S"};
        //parameter
        String[] param = {} ;

        cur = db.query("POSTJELAJAHUPI",cols,null,null,null,null,null) ;
        Lokasi[] lokasis = new Lokasi[cur.getCount()] ;

        if(cur.getCount()>0){
            cur.moveToFirst() ;
            int i = 0 ;
            int batas = cur.getCount() ;
                while (i<batas){
                    Lokasi lokasi = new Lokasi() ;
                    lokasi.nama_lokasi = cur.getString(1) ;
                    lokasi.left_e = Double.valueOf(cur.getString(2));
                    lokasi.right_e = Double.valueOf(cur.getString(3));
                    lokasi.up_s = Double.valueOf(cur.getString(4));
                    lokasi.down_s = Double.valueOf(cur.getString(5));
                    lokasis[i] = lokasi ;
                    i = i + 1 ;
                    cur.moveToNext() ;
                }
        }
        return lokasis ;
    }

}
