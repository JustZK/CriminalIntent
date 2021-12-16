package com.zk.criminalintent.bean

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.zk.criminalintent.db.CrimeBaseHelper
import java.util.*
import kotlin.collections.ArrayList
import com.zk.criminalintent.bean.CrimeDbSchema.CrimeTable
import com.zk.criminalintent.db.CrimeCursorWrapper
import java.io.File

class CrimeLab {
//    val crimes = ArrayList<Crime>()

    private var mContext: Context? = null
    private var mDataBase: SQLiteDatabase? = null

    companion object {
        val instance: CrimeLab by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CrimeLab()
        }

        private fun getContentValues(crime: Crime): ContentValues {
            val values = ContentValues()
            values.put(CrimeTable.Cols.UUID, crime.id.toString())
            values.put(CrimeTable.Cols.TITLE, crime.title)
            values.put(CrimeTable.Cols.DATE, crime.date.time)
            values.put(CrimeTable.Cols.SOLVED, if (crime.solved) 1 else 0)
            values.put(CrimeTable.Cols.SUSPECT, crime.suspect)

            return values
        }
    }

    fun init(context: Context) {
        if (mContext != null) return
        mContext = context.applicationContext
        mDataBase = CrimeBaseHelper(context).writableDatabase

    }

    init {
    }

    fun getCrimes(): ArrayList<Crime> {
        val crimes = ArrayList<Crime>()

        val cursor = queryCrimes(null, null)
        cursor.use {
            it.moveToFirst()
            while (!it.isAfterLast){
                crimes.add(it.getCrime())
                it.moveToNext()
            }
        }

        return crimes
    }

    fun getCrime(id: UUID): Crime? {
        val cursor = queryCrimes(CrimeTable.Cols.UUID + " = ? ", arrayOf(id.toString()))
        cursor.use {
            if (it.count == 0) return null
            it.moveToFirst()
            return it.getCrime()
        }
    }

    fun addCrime(c: Crime) {
        val values = getContentValues(c)
        mDataBase?.insert(CrimeTable.NAME, null, values)
    }

    fun getPhotoFile(c: Crime): File {
        val fileDir = mContext!!.filesDir
        return File(fileDir, c.getPhotoFileName())
    }

    fun updateCrime(crime: Crime) {
        val uuidStr = crime.id.toString()
        val values = getContentValues(crime)
        mDataBase?.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", arrayOf(uuidStr))
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = mDataBase?.query(
            CrimeTable.NAME,
            null,  // Columns = null -> selects all columns
            whereClause,
            whereArgs,
            null, //groupBy
            null, //having
            null //orderBy
        )
        return CrimeCursorWrapper(cursor)
    }

}