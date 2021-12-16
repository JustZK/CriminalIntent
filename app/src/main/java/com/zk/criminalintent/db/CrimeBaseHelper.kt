package com.zk.criminalintent.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zk.criminalintent.bean.CrimeDbSchema

class CrimeBaseHelper : SQLiteOpenHelper {

    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "crimeBase.db"
    }

    constructor(context: Context) : this(context, DATABASE_NAME, VERSION)

    constructor(context: Context, name: String, version: Int) : super(context, name, null, version)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table ${CrimeDbSchema.CrimeTable.NAME} (_id integer primary key autoincrement, " +
                "${CrimeDbSchema.CrimeTable.Cols.UUID}, " +
                "${CrimeDbSchema.CrimeTable.Cols.TITLE}, " +
                "${CrimeDbSchema.CrimeTable.Cols.DATE}, " +
                "${CrimeDbSchema.CrimeTable.Cols.SOLVED}, " +
                "${CrimeDbSchema.CrimeTable.Cols.SUSPECT})")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


}