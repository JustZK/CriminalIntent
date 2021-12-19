package com.zk.criminalintent.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zk.criminalintent.R
import com.zk.criminalintent.activity.CrimePagerActivity
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.bean.CrimeLab

class CrimeListFragment : Fragment() {
    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null
    private var mSubtitleVisible = false
    private var mCallbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeSelected(crime: Crime)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallbacks = context as Callbacks
    }

    companion object {
        private const val REQUEST_CRIME = 0x01

        private const val SAVE_SUBTITLE_VISIBLE = "subtitle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (savedInstanceState != null) mSubtitleVisible =
            savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE)

        updateUI()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.frament_crime_list, menu)

        val subtitleItem = menu.findItem(R.id.show_subtitle)
        if (mSubtitleVisible) subtitleItem.setTitle(R.string.hide_subtitle)
        else subtitleItem.setTitle(R.string.show_subtitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                CrimeLab.instance.addCrime(crime)
//                val intent = CrimePagerActivity.newInstance(activity!!, crimeId = crime.id)
//                startActivity(intent)
                updateUI()
                mCallbacks?.onCrimeSelected(crime)
                true
            }
            R.id.show_subtitle -> {
                mSubtitleVisible = !mSubtitleVisible
                activity?.invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun updateSubtitle() {
        val crimeLab = CrimeLab.instance
        val crimeCount = crimeLab.getCrimes().size
        var subtitle: String? = getString(R.string.subtitle_format, crimeCount)

        if (!mSubtitleVisible) subtitle = null
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    public fun updateUI() {
        val crimeLab = CrimeLab.instance
        val crimes = crimeLab.getCrimes()

        if (mAdapter == null) {
            mAdapter = CrimeAdapter(crimes)
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.crimes = crimes
            mAdapter?.notifyDataSetChanged()
        }

        updateSubtitle()
    }

    private inner class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val mTitleTextView = itemView.findViewById<TextView>(R.id.crime_title)
        private val mDateTextView = itemView.findViewById<TextView>(R.id.crime_date)
        private val mSolvedImageView = itemView.findViewById<ImageView>(R.id.crime_solved)

        private var mCrime: Crime? = null

        fun bind(crime: Crime) {
            mCrime = crime
            mTitleTextView.text = crime.title
            mDateTextView.text = crime.date.toLocaleString()
            mSolvedImageView.visibility = if (crime.solved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
//            Toast.makeText(activity, "${mCrime?.title} clicked!", Toast.LENGTH_SHORT).show()
//            startActivityForResult(
//                CrimePagerActivity.newInstance(activity!!, mCrime?.id!!),
//                REQUEST_CRIME
//            )
            mCallbacks?.onCrimeSelected(mCrime!!)
        }
    }

    private inner class CrimeAdapter(var crimes: ArrayList<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val itemView =
                LayoutInflater.from(activity).inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(itemView)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CRIME) {
            // handle result
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }
}