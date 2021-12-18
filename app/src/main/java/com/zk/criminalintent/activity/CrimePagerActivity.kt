package com.zk.criminalintent.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewParent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.zk.criminalintent.R
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.bean.CrimeLab
import com.zk.criminalintent.fragment.CrimeFragment
import java.util.*
import kotlin.collections.ArrayList

class CrimePagerActivity : AppCompatActivity(), CrimeFragment.Callbacks {
    private lateinit var mViewPager: ViewPager
    private lateinit var mCrimes : ArrayList<Crime>

    companion object{
        const val EXTRA_CRIME_ID = "crimeId"

        fun newInstance(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        mViewPager = findViewById(R.id.activity_crime_pager_view_pager)

        mCrimes = CrimeLab.instance.getCrimes()
        val fragmentManger = supportFragmentManager
        mViewPager.adapter = object : FragmentStatePagerAdapter(fragmentManger){
            override fun getCount(): Int {
                return mCrimes.size
            }

            override fun getItem(position: Int): Fragment {
                val crime = mCrimes[position]
                return CrimeFragment.newInstance(crimeId = crime.id)
            }

        }

        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        for (index in mCrimes.indices){
            if (mCrimes[index].id == crimeId){
                mViewPager.currentItem = index
                break
            }
        }
    }

    override fun onCrimeUpdated(crime: Crime) {

    }
}