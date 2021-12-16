package com.zk.criminalintent.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.zk.criminalintent.R
import java.util.*

class DatePickerFragment: DialogFragment() {
    private lateinit var mDatePicker: DatePicker

    companion object {
        const val EXTRA_DATE = "com.zk.criminal.intent.dialog.date"
        private const val ARG_DATE = "date"

        fun newInstance(date: Date) = DatePickerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
        }
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val date = arguments!!.getSerializable(ARG_DATE)!! as Date
//        val calendar = Calendar.getInstance()
//        calendar.time = date
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val view = inflater.inflate(R.layout.dialog_date, container)
//
//        mDatePicker = view.findViewById(R.id.dialog_date_picker)
//        mDatePicker.init(year, month, day, null)
//
//        return view
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments!!.getSerializable(ARG_DATE)!! as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_date, null)

        mDatePicker = view.findViewById(R.id.dialog_date_picker)
        mDatePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(android.R.string.ok
            ) { _, _ ->
                val yearTemp = mDatePicker.year
                val monthTemp = mDatePicker.month
                val dayTemp = mDatePicker.dayOfMonth
                val dateTemp = GregorianCalendar(yearTemp, monthTemp, dayTemp).time
                sendResult(Activity.RESULT_OK, dateTemp)
            }
            .create()
    }

    private fun sendResult(resultCode: Int, date: Date){
        if(targetFragment == null) return
        val intent = Intent()
        intent.putExtra(EXTRA_DATE, date)

        targetFragment!!.onActivityResult(targetRequestCode, resultCode, intent)
    }
}