package com.zk.criminalintent.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zk.criminalintent.base.SingleFragmentActivity
import com.zk.criminalintent.bean.CrimeLab
import com.zk.criminalintent.fragment.CrimeListFragment

class CrimeListActivity : SingleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CrimeLab.instance.init(this)
    }

    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }

}