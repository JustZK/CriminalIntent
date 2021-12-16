package com.zk.criminalintent.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.zk.criminalintent.R
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.bean.CrimeLab
import com.zk.criminalintent.dialog.DatePickerFragment
import com.zk.criminalintent.tools.PictureUtils
import java.io.File
import java.util.*

class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime
    private var mPhotoFile: File? = null
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox
    private lateinit var mSuspectButton: Button
    private lateinit var mReportButton: Button
    private lateinit var mPhotoButton: ImageButton
    private lateinit var mPhotoView: ImageView

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"

        private const val REQUEST_DATE = 0x01
        private const val REQUEST_CONTACT = 0x02
        private const val REQUEST_PHOTO = 0x03

        fun newInstance(crimeId: UUID) = CrimeFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.instance.getCrime(uuid)!!
        mPhotoFile = CrimeLab.instance.getPhotoFile(mCrime)
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.instance.updateCrime(mCrime)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_crime, container, false)



        mTitleField = v.findViewById(R.id.crime_title)
        mTitleField.setText(mCrime.title)
        mTitleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mCrime.title = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        mDateButton = v.findViewById(R.id.crime_date)
        updateDate()
//        mDateButton.isEnabled = false
        mDateButton.setOnClickListener {
            fragmentManager?.let { it1 ->
                val dialog = DatePickerFragment.newInstance(mCrime.date)
                dialog.setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                dialog.show(it1, DIALOG_DATE)
            }
        }
        mSolvedCheckBox = v.findViewById(R.id.crime_solved)
        mSolvedCheckBox.isChecked = mCrime.solved
        mSolvedCheckBox.setOnCheckedChangeListener { _, p1 -> mCrime.solved = p1 }

        mReportButton = v.findViewById(R.id.crime_report)
        mReportButton.setOnClickListener {
            var intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            intent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(intent)
        }

        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        mSuspectButton = v.findViewById(R.id.crime_suspect)
        mSuspectButton.setOnClickListener {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if (mCrime.suspect.isNotBlank()) {
            mSuspectButton.text = mCrime.suspect
        }

        //判读是有有通讯录的权限
        val packageManager = activity!!.packageManager
        if (packageManager.resolveActivity(
                pickContact,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            mSuspectButton.isEnabled = false
        }


        mPhotoButton = v.findViewById(R.id.crime_camera)
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto =
            mPhotoFile != null && captureImage.resolveActivity(packageManager) != null
        mPhotoButton.isEnabled = canTakePhoto
        mPhotoButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val uri = FileProvider.getUriForFile(
                    activity!!,
                    "com.zk.criminalintent.fileprovider",
                    mPhotoFile!!
                )
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                val cameraActivities = activity!!.packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                for (ac in cameraActivities) {
                    activity!!.grantUriPermission(
                        ac.activityInfo.packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    startActivityForResult(captureImage, REQUEST_PHOTO)
                }
            }

        })

        mPhotoView = v.findViewById(R.id.crime_photo)
        updatePhotoView()

        return v
    }

    fun returnResult() {
        activity?.setResult(Activity.RESULT_OK, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mCrime.date = date
            updateDate()
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri = data.data!!
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val c = activity!!.contentResolver.query(contactUri, queryFields, null, null, null)
            c.use { it ->
                if (it!!.count == 0) return
                it.moveToFirst()
                val suspect = it.getString(0)
                mCrime.suspect = suspect
                mSuspectButton.text = suspect
            }
        } else if (requestCode == REQUEST_PHOTO){
            val uri = FileProvider.getUriForFile(activity!!, "com.zk.criminalintent.fileprovider", mPhotoFile!!)
            activity!!.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            updatePhotoView()
        }
    }

    private fun updateDate() {
        mDateButton.text = mCrime.date.toString()
    }

    private fun getCrimeReport(): String {
        val solvedString = if (mCrime.solved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dataFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dataFormat, mCrime.date).toString()

        var suspect: String? = mCrime.suspect
        suspect = if (suspect.isNullOrBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect)
        }

        return getString(R.string.crime_report, mCrime.title, dateString, solvedString, suspect)
    }

    private fun updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile!!.exists()){
            mPhotoView.setImageDrawable(null)
        } else {
            val bitmap = PictureUtils.getScaledBitmap(mPhotoFile!!.path, activity!!)
            mPhotoView.setImageBitmap(bitmap)
        }
    }
}