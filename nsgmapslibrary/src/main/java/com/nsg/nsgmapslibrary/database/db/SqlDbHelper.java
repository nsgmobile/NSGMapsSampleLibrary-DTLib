package com.nsg.nsgmapslibrary.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.nsg.nsgmapslibrary.database.dto.GeometryT;
import com.nsg.nsgmapslibrary.database.dto.RouteT;
import com.nsg.nsgmapslibrary.database.dto.UserT;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;


/**
 * Created by sailaja.ch NSGI on 03/09/2019
 */
public class SqlDbHelper extends SQLiteOpenHelper {
	private static final String DROP_SYNTAX="DROP TABLE IF EXISTS ";

	public SqlDbHelper(Context context, String name, CursorFactory factory,
                       int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SqlHandler.getCreateTableSyntax(EdgeDataT.TABLE_NAME, EdgeDataT.MAPPING));
		db.execSQL(SqlHandler.getCreateTableSyntax(UserT.TABLE_NAME, UserT.MAPPING));
		db.execSQL(SqlHandler.getCreateTableSyntax(GeometryT.TABLE_NAME, GeometryT.MAPPING));
		db.execSQL(SqlHandler.getCreateTableSyntax(RouteT.TABLE_NAME, RouteT.MAPPING));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_SYNTAX + EdgeDataT.TABLE_NAME);
		db.execSQL(DROP_SYNTAX + UserT.TABLE_NAME);
		db.execSQL(DROP_SYNTAX + GeometryT.TABLE_NAME);
		db.execSQL(DROP_SYNTAX + RouteT.TABLE_NAME);
		onCreate(db);
	}
}
