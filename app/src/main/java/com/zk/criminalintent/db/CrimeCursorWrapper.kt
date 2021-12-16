package com.zk.criminalintent.db

import android.database.Cursor
import android.database.CursorWrapper
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.bean.CrimeDbSchema
import com.zk.criminalintent.bean.CrimeDbSchema.CrimeTable
import java.util.*

class CrimeCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    fun getCrime(): Crime {
        val uuidStr = getString(getColumnIndex(CrimeTable.Cols.UUID))
        val title = getString(getColumnIndex(CrimeTable.Cols.TITLE))
        val date = getLong(getColumnIndex(CrimeTable.Cols.DATE))
        val isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED))
        val suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT))

        val crime = Crime(UUID.fromString(uuidStr))
        crime.title = title
        crime.date = Date(date)
        crime.solved = isSolved != 0
        crime.suspect = suspect

        return crime
    }
}