package com.zk.criminalintent.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zk.criminalintent.R
import com.zk.criminalintent.base.SingleFragmentActivity
import com.zk.criminalintent.fragment.CrimeFragment
import java.util.*

class CrimeActivity : SingleFragmentActivity() {

    companion object{
        const val EXTRA_CRIME_ID = "crimeId"

        fun newInstance(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimeActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    override fun createFragment(): Fragment {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        return CrimeFragment.newInstance(crimeId)
    }
}