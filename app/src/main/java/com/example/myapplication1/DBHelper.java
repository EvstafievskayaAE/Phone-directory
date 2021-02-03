package com.example.myapplication1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "android.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "PhoneDirectiry";

    // поля таблицы для хранения ФИО, Должности и Телефона (id формируется автоматически)
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LastName = "LastName";
    public static final String COLUMN_Name = "Name";
    public static final String COLUMN_MiddleName = "MiddleName";
    public static final String COLUMN_Phone = "Phone";

    private SQLiteDatabase db;
    private ContentValues values;

    // формируем запрос для создания базы данных
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_LastName
            + " text not null, " + COLUMN_Name + " text not null,"
            + COLUMN_MiddleName + " text ,"  + COLUMN_Phone + " text not null" + ");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**Создание таблицы БД*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /**Обновление БД до новой версии*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PhoneDirectiry");
        onCreate(db);
    }

    /** Создание нового контакта*/
    public void createNewContact(String lastName, String name, String middleName, String phone) {
        db = this.getWritableDatabase();
        values = createContentValues(lastName, name, middleName, phone);
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    /** Описание структуры данных */
    private ContentValues createContentValues( String lastName, String name,
                                               String middleName, String phone) {
        values = new ContentValues();
        values.put(COLUMN_LastName, lastName);
        values.put(COLUMN_Name, name);
        values.put(COLUMN_MiddleName, middleName);
        values.put(COLUMN_Phone, phone);
        return values;
    }

   /** Получение списка всех контактов - фио и номер телефона*/
    public List<Person> getAllData() {
        String query, lastName, name, middleName, phone;
        List<Person> personList;
        Person person;
        Cursor cursor;
        int idPerson, idPersonColumnIndex, lastNameColumnIndex,
                nameColumnIndex, middleNameColumnIndex, phoneColumnIndex;

        db = this.getReadableDatabase();
        personList = new ArrayList<>();
        query = "SELECT  * FROM " + DATABASE_TABLE;
        cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            lastNameColumnIndex = cursor.getColumnIndex(COLUMN_LastName);
            nameColumnIndex = cursor.getColumnIndex(COLUMN_Name);
            middleNameColumnIndex = cursor.getColumnIndex(COLUMN_MiddleName);
            phoneColumnIndex = cursor.getColumnIndex(COLUMN_Phone);
            idPersonColumnIndex = cursor.getColumnIndex(COLUMN_ID);
            lastName= cursor.getString(lastNameColumnIndex);
            name = cursor.getString(nameColumnIndex);
            middleName = cursor.getString(middleNameColumnIndex);
            phone = cursor.getString(phoneColumnIndex);
            idPerson = cursor.getInt(idPersonColumnIndex);
            person = new Person(idPerson, lastName, name, middleName, phone);
            personList.add(person);
        }
        return personList;
    }

    /** Удаление контакта */
    public void deleteContact(long idPerson) {
        db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, COLUMN_ID + "=" + idPerson, null);
        db.close();
    }

    /** Обновление контакта */
    public void updateContact(int editIdPerson, String lastName, String name,
                              String middleName, String phone) {
        db = this.getWritableDatabase();
        values = createContentValues(lastName, name, middleName, phone);
        db.update(DATABASE_TABLE, values, COLUMN_ID + " = " + editIdPerson, null);
        db.close();
    }

    /** Получение списка номеров телефонов*/
    public List<String> getAllPhones() {
        String query, phone;
        List<String> phoneList;
        Cursor cursor;
        int phoneIndex;

        db = this.getReadableDatabase();
        phoneList = new ArrayList<>();
        query = "SELECT  * FROM " + DATABASE_TABLE;
        cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            phoneIndex = cursor.getColumnIndex(COLUMN_Phone);
            phone = cursor.getString(phoneIndex);
            phoneList.add(phone);
        }
        return phoneList;
    }
}

