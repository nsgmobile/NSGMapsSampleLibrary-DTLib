package com.nsg.nsgmapslibrary.database.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sailaja.ch on 03/09/2019
 */
public class SqlHandler {

	public static final String DATABASE_NAME = "DUBAI_PORTS_DB";
	public static final int DATABASE_VERSION = 2;
	Context context;
	SQLiteDatabase sqlDatabase;
	SqlDbHelper dbHelper;

	public SqlHandler(Context context) {

		dbHelper = new SqlDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
		sqlDatabase = dbHelper.getWritableDatabase();
	}



    public void executeQuery(String query) {
		try {

			if (sqlDatabase.isOpen()) {
				sqlDatabase.close();
			}

			sqlDatabase = dbHelper.getWritableDatabase();
			sqlDatabase.execSQL(query);
			if (sqlDatabase.isOpen()) {
				sqlDatabase.close();
			}
		} catch (Exception e) {

			System.out.println("DATABASE ERROR " + e);
		}

	}

	public Cursor selectQuery(String query) {
		Cursor c1 = null;
		try {

			if (sqlDatabase.isOpen()) {
				sqlDatabase.close();

			}
			sqlDatabase = dbHelper.getWritableDatabase();
			c1 = sqlDatabase.rawQuery(query, null);

		} catch (Exception e) {

			System.out.println("DATABASE ERROR " + e);

		}
		return c1;

	}

    public void closeDataBaseConnection(){
        try {

            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
        } catch (Exception e) {
            System.out.println("DATABASE ERROR " + e);
        }
    }
	public static String getCreateTableSyntax(String tableName, ArrayList<DatabaseColumn> columnMapping){
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(tableName).append("(");
        for(int i=0;i<columnMapping.size();i++){
            DatabaseColumn dbc = columnMapping.get(i);
            sb.append(dbc.getColumnName()).append(" ")
                    .append(dbc.getColumnType().equals("int")?"integer":dbc.getColumnType())
                    .append(" ")
            .append(dbc.isAuto()?"primary key autoincrement":"");
            if(!dbc.isAuto() && !dbc.isNull())sb.append("not null");
            if(i+1<columnMapping.size())sb.append(",");
            else sb.append(");");
        }
        return sb.toString();
    }

/*
    public static int getNextAutoIncrement(String tableName, String fieldName) {
        ArrayList<DatabaseColumn> columnMapping = new ArrayList<DatabaseColumn>();
        DatabaseColumn autoColumn = null;
        for (int i = 0; i < columnMapping.size(); i++) {
            DatabaseColumn column = columnMapping.get(i);
            if (column.getAttributeName().equals(fieldName)) {
                autoColumn = column;
            }
        }
        int maxNumber = 0;
        for (int i = 0; autoColumn != null && columnMapping != null && i < columnMapping.size(); i++) {
            Object obj = columnMapping.get(i);
            Integer intVal = (Integer) reflectionCall(obj, autoColumn.getAttributeName(), null, null);
            if (intVal != null && intVal.intValue() > maxNumber) maxNumber = intVal.intValue();
        }
        return maxNumber + 1;
    }
*/
    public static List getDataRows(ArrayList<DatabaseColumn> columnMapping, Class c, String tableName, String whereClause, SqlHandler handler){
        String sql = "select * from " + tableName;
        if(whereClause!=null && !whereClause.trim().equals("")){
            sql = sql + " WHERE "+whereClause;
        }
        Cursor c1 = handler.selectQuery(sql);
        List list =  getDataRows(columnMapping,c,c1);
        c1.close();
        handler.closeDataBaseConnection();
        return list;
    }

    public static List getDataRows(ArrayList<DatabaseColumn> columnMapping, Class c, Cursor cursor){
    	Log.e("Class Name",c.getName());
        ArrayList list = new ArrayList();
        try {
            if (cursor != null && cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Object obj = c.newInstance();
                        for(int i=0;i<columnMapping.size();i++){
                            DatabaseColumn dbc = columnMapping.get(i);
                            Log.e(dbc.getColumnName(),dbc.getAttributeName());
                            if(dbc.isInt()) {
                                reflectionCall(obj, dbc.getAttributeName(),new Class[]{Integer.class},new Object[]{cursor.getInt(cursor.getColumnIndex(dbc.getColumnName()))});
                            }else{
                                reflectionCall(obj, dbc.getAttributeName(),new Class[]{String.class},new Object[]{cursor.getString(cursor.getColumnIndex(dbc.getColumnName()))});
                            }
                        }
                        list.add(obj);
                    }while (cursor.moveToNext());
                }
            }
        }catch(Exception ex){

        }
        return list;
    }
    public static Object reflectionCall(final Object aninstance, final String amethodname, final Class[] parameterTypes, final Object[] parameters)
    {
        Object res = null;
        try {
            Class aclass = aninstance.getClass();
            //Class[] parameterTypes = new Class[]{String[].class};
            final Method amethod = aclass.getDeclaredMethod(amethodname, parameterTypes);
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    amethod.setAccessible(true);
                    return null; // nothing to return
                }
            });
            res = amethod.invoke(aninstance, parameters);
        } catch (final SecurityException e) {
        		Log.e("SecurityException",e.getMessage());
        } catch (final NoSuchMethodException e) {
        	Log.e("NoSuchMethodException",e.getMessage());
        } catch (final IllegalArgumentException e) {
        	//Log.e("IllegalArgumentException",e.getMessage());
        } catch (final IllegalAccessException e) {
        	Log.e("IllegalAccessException",e.getMessage());
        } catch (final InvocationTargetException e) {
        	//Log.e("InvocationTargetException",e.getMessage());
        }
        return res;
    }



}
