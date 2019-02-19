package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DatabaseHelper;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DbConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    DatabaseHelper manager;
    SQLiteDatabase db;
    Cursor c;
    static int position;
    EditText edgame1, edgame2, edgame3, edname;
    Button btinsert, btupdate, btdelect;
    ListView lv;
    Spinner spinner;

    static boolean initSpinner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        initial();
        requery();
        spinner.setOnItemSelectedListener(this);
        lv.setOnItemClickListener(this);

    }

    private void initial() {
        spinner = (Spinner) findViewById(R.id.spinner);
        lv = (ListView) this.findViewById(R.id.listView1);
        edname = (EditText) this.findViewById(R.id.edname);
        edgame1 = (EditText) this.findViewById(R.id.edgame1);
        edgame2 = (EditText) this.findViewById(R.id.edgame2);
        edgame3 = (EditText) this.findViewById(R.id.edgame3);
        btinsert = (Button) this.findViewById(R.id.btn_insert);
        btupdate = (Button) this.findViewById(R.id.btn_update);
        btdelect = (Button) this.findViewById(R.id.btn_delete);
        manager = new DatabaseHelper(this, DbConstants.creatTableScore);
    }

    public void Oninsertorupdate(View view) {
        String name = edname.getText().toString().trim();
        String game1 = edgame1.getText().toString().trim();
        String game2 = edgame2.getText().toString().trim();
        String game3 = edgame3.getText().toString().trim();

        if (name.length() == 0 || game1.length() == 0
                || game2.length() == 0 || game3.length() == 0) {
            Toast.makeText(getApplicationContext(), "請不要有空白", Toast.LENGTH_SHORT).show();
            return;
        }

        if (view.getId() == R.id.btn_update) {
            initSpinner = false;
            updater(name, game1, game2, game3);
        } else {
            initSpinner = false;
            Calendar mCal = Calendar.getInstance();
            String dateformat = "yyyyMMdd";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            String today = df.format(mCal.getTime());
            addData(today, name, game1, game2, game3, "false");
        }

        edname.setText("");
        edgame1.setText("");
        edgame2.setText("");
        edgame3.setText("");

        requery();


    }

    public void OnDelete(View v) {
        initSpinner = false;
        db = manager.getWritableDatabase();
        c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
        c.moveToPosition(position);
//		int test =arg2+1;
        db.delete(DbConstants.TABLE_SENIORSCORE, "_id=" + c.getInt(0), null);
        edname.setText("");
        edgame1.setText("");
        edgame2.setText("");
        edgame3.setText("");
        requery();

    }

    private void requery() {
        db = manager.getWritableDatabase();
        c = db.rawQuery(" SELECT * FROM  " + DbConstants.TABLE_SENIORSCORE, null);
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        String today = df.format(mCal.getTime());
        Log.d("Day", "today is " + today);
        if (c.getCount() == 0) {
            addData(today, "人員1", "null,null,null,null", "null,null,null,null", "null,null,null,null", "false");
            addData(today, "人員2", "null,null,null,null", "null,null,null,null", "null,null,null,null", "true");
            addData(today, "人員3", "null,null,null,null", "null,null,null,null", "null,null,null,null", "false");
            addData(today, "人員4", "null,null,null,null", "null,null,null,null", "null,null,null,null", "false");
            addData(today, "人員5", "null,null,null,null", "null,null,null,null", "null,null,null,null", "false");
            addData(today, "人員6", "null,null,null,null", "null,null,null,null", "null,null,null,null", "false");

        }

        ArrayList<HashMap<String, String>> listData = fillList();
        SimpleAdapter adapter = fillAdapter(listData);
        lv.setAdapter(adapter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nameList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dataAdapter);
        btupdate.setEnabled(false);
        btdelect.setEnabled(false);
    }

    private void addData(String date, String name, String game1, String game2, String game3, String state) {
        SQLiteDatabase db = manager.getWritableDatabase();
        ContentValues values = new ContentValues(4);
        values.put("date", date);
        values.put("user", name);
        values.put("game1", game1);
        values.put("game2", game2);
        values.put("game3", game3);
        values.put("state", state);

        db.insert(DbConstants.TABLE_SENIORSCORE, null, values);
    }

    private void updater(String name, String game1, String game2, String game3) {
        Log.e("updater", "1");
        db = manager.getWritableDatabase();
        Log.e("updater", "2");
        c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
        Log.e("updater", "3");
        c.moveToPosition(position);
        Log.e("updater", "4");
        ContentValues values = new ContentValues();

        values.put(DbConstants.DATE, c.getString(c.getColumnIndex(DbConstants.DATE)));
        values.put(DbConstants.USER, name);
        values.put(DbConstants.GAME1, game1);
        values.put(DbConstants.GAME2, game2);
        values.put(DbConstants.GAME3, game3);
        values.put(DbConstants.STATE, c.getString(c.getColumnIndex(DbConstants.STATE)));
        db.update(DbConstants.TABLE_SENIORSCORE, values, "_id=" + c.getInt(0), null);
    }

    //spinner name
    public List<String> nameList() {
        List<String> list = new ArrayList<String>();

        SQLiteDatabase db = manager.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            if (c.moveToFirst()) {

                do {
                    String name = c.getString(c.getColumnIndex(DbConstants.USER));
                    list.add(name);
                } while (c.moveToNext());

            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (db.isOpen())
                db.close();
        }
        return list;
    }

    //all database
    public ArrayList<HashMap<String, String>> fillList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = manager.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

                    String date = c.getString(c.getColumnIndex(DbConstants.DATE));
                    String name = c.getString(c.getColumnIndex(DbConstants.USER));
                    String game1 = c.getString(c.getColumnIndex(DbConstants.GAME1));
                    String game2 = c.getString(c.getColumnIndex(DbConstants.GAME2));
                    String game3 = c.getString(c.getColumnIndex(DbConstants.GAME3));
                    String state = c.getString(c.getColumnIndex(DbConstants.STATE));
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(DbConstants.DATE, date);
                    map.put(DbConstants.USER, name);
                    map.put(DbConstants.GAME1, game1);
                    map.put(DbConstants.GAME2, game2);
                    map.put(DbConstants.GAME3, game3);
                    map.put(DbConstants.STATE, state);
                    dataList.add(map);

                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db.isOpen())
                db.close();
        }
        return dataList;
    }

    public SimpleAdapter fillAdapter(ArrayList<HashMap<String, String>> listData) {

        SimpleAdapter adapter = new SimpleAdapter(this, listData,
                R.layout.items, new String[]{DbConstants.DATE, DbConstants.USER,
                DbConstants.GAME1, DbConstants.GAME2, DbConstants.GAME3, DbConstants.STATE}, new int[]{
                R.id.tvdate, R.id.tvname, R.id.tvgame1, R.id.tvgame2, R.id.tvgame3, R.id.tvstate});

        return adapter;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        db = manager.getReadableDatabase();
        c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
        c.moveToPosition(position);
        edname.setText(c.getString(c.getColumnIndex(DbConstants.USER)));
        edgame1.setText(c.getString(c.getColumnIndex(DbConstants.GAME1)));
        edgame2.setText(c.getString(c.getColumnIndex(DbConstants.GAME2)));
        edgame3.setText(c.getString(c.getColumnIndex(DbConstants.GAME3)));
        btupdate.setEnabled(true);
        btdelect.setEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
        if (initSpinner) {
            Toast.makeText(getApplication(), "你選的是" + adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            db = manager.getWritableDatabase();
            try {
                c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
                c.moveToFirst();
                if (c.moveToFirst()) {
                    do {
                        String name1 = c.getString(c.getColumnIndex(DbConstants.USER));
                        ContentValues values = new ContentValues();
                        if (adapterView.getSelectedItem().toString().equals(name1)) {
                            Log.e("Change", "Yes");
                            String name = c.getString(c.getColumnIndex(DbConstants.USER));
                            String date = c.getString(c.getColumnIndex(DbConstants.DATE));
                            String game1 = c.getString(c.getColumnIndex(DbConstants.GAME1));
                            String game2 = c.getString(c.getColumnIndex(DbConstants.GAME2));
                            String game3 = c.getString(c.getColumnIndex(DbConstants.GAME3));
                            String state = "true";
                            values.put(DbConstants.DATE, date);
                            values.put(DbConstants.USER, name);
                            values.put(DbConstants.GAME1, game1);
                            values.put(DbConstants.GAME2, game2);
                            values.put(DbConstants.GAME3, game3);
                            values.put(DbConstants.STATE, state);
                        } else {
                            Log.e("Change", "No");
                            String name = c.getString(c.getColumnIndex(DbConstants.USER));
                            String date = c.getString(c.getColumnIndex(DbConstants.DATE));
                            String game1 = c.getString(c.getColumnIndex(DbConstants.GAME1));
                            String game2 = c.getString(c.getColumnIndex(DbConstants.GAME2));
                            String game3 = c.getString(c.getColumnIndex(DbConstants.GAME3));
                            String state = "false";
                            values.put(DbConstants.DATE, date);
                            values.put(DbConstants.USER, name);
                            values.put(DbConstants.GAME1, game1);
                            values.put(DbConstants.GAME2, game2);
                            values.put(DbConstants.GAME3, game3);
                            values.put(DbConstants.STATE, state);

                        }
                        db.update(DbConstants.TABLE_SENIORSCORE, values, "_id=" + c.getInt(0), null);

                    } while (c.moveToNext());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                ArrayList<HashMap<String, String>> listData = fillList();
                SimpleAdapter adapter = fillAdapter(listData);
                lv.setAdapter(adapter);
                if (db.isOpen())
                    db.close();
            }

        } else {

            initSpinner = true;
        }


    }

    @Override
    public void onNothingSelected(AdapterView adapterView) {

    }
}
