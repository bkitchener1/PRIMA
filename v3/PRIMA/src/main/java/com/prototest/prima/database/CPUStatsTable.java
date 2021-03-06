package com.prototest.prima.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class CPUStatsTable {
   public static final String TAG = "CPU Stats Data";

   public static final String TABLE_CPU = "cpu_stats";
   public static final String COLUMN_ID = BaseColumns._ID;
   public static final String COLUMN_USED = "used";
   public static final String COLUMN_FREE = "free";
   public static final String COLUMN_PERCENT_USED = "percent_used";
   private static final String NOT_NULL = "NOT NULL";

   private static final String CREATE_TABLE = String.format("CREATE TABLE %s "
         + "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INT %s, %s INT %s, %s REAL %s)", TABLE_CPU,
         COLUMN_ID, COLUMN_USED, NOT_NULL, COLUMN_FREE, NOT_NULL, COLUMN_PERCENT_USED, NOT_NULL);

   public static void onCreate(SQLiteDatabase db) {
      Log.d(TAG, "onCreate with sql" + CREATE_TABLE);
      db.execSQL(CREATE_TABLE);
   }

   public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.d(TAG, "onUpgrade");

      // Usually an ALTER TABLE statement for migrations

      // below is a hack to just drop the table instead of actually migrating
      dropTable(db);
      onCreate(db);
   }

   public static void dropTable(SQLiteDatabase db) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CPU);
   }
}
