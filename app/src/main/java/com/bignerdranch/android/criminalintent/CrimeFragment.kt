package com.bignerdranch.android.criminalintent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.text.MessageFormat.format
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.format
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.lang.String.format
import java.sql.Time
import java.text.DateFormat
import java.text.MessageFormat.format
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "dialog_date"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "dialog_time"
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1

//Создание UI-фрагмента (контроллера)
class CrimeFragment: Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    //определение интерфейса обратного вызова DatePickerFragment для передачи данных между Fragments
    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    //Ленивая инициализация ViewModel
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy{
        ViewModelProvider(this)[CrimeDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()

        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        Log.d(TAG, "Args bundle crime id is $crimeId")
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(){
        val calendar = Calendar.getInstance()

        calendar.time = crime.date

        val initialYear = calendar.get(Calendar.YEAR).toString()

        val initialMonth = calendar.get(Calendar.MONTH).toString()

        val initialDay = calendar.get(Calendar.DAY_OF_MONTH).toString()

        val hours = calendar.get(Calendar.HOUR)

        val minutes = calendar.get(Calendar.MINUTE)

        titleField.setText(crime.title)
        dateButton.text = "${initialDay}.${initialMonth}.${initialYear}"
        timeButton.text = "${hours}:${minutes}"
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

        if (crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
    }

    //Реализация контроллера происходит в onStart
    override fun onStart() {
        super.onStart()


        //добавление анонимного класса-слушателя для title
        val titleWatcher = object: TextWatcher{
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {

            }
        }
        //Добавление созданного слушателя на titleField
        titleField.addTextChangedListener(titleWatcher)

        //добавление слушателя для чекбокс
        solvedCheckBox.apply{
            setOnCheckedChangeListener{_, isChecked ->
                crime.isSolved = isChecked
            }
        }

        //Добавление слушателя на кнопку date
        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date).apply {

                //обявление CrimeFragment целевым фрагментом (для DatePickerFragment)
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)

                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        //добавление слушателя на time
        timeButton.setOnClickListener{
            TimePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_TIME)
            }
        }

        //Добавление слушателя на reportButton
        //Вызов неявного интента
        reportButton.setOnClickListener{
            Intent(Intent.ACTION_SEND).apply {
                type="text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        //Добавление слушателя на suspectButton
        //Вызов неявного интента
        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
        }
    }

    //переопределение функции отправки представления в host-activity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        //Инициализация виджетов
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.report_button) as Button
        suspectButton = view.findViewById(R.id.suspect_button) as Button

        //Настройка виджета кнопки через apply
//        dateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }

        return view
    }

    //Функция возврата данных в текстовом виде
    private fun getCrimeReport():String{
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format(DATE_FORMAT, crime.date).toString()

        val suspect = if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    //метод отлавливающий результат вызова неявного интента с запросом на возврат данных
    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when{
            resultCode != Activity.RESULT_OK ->
                return

            data != null && requestCode == REQUEST_CONTACT -> {
                val contactUri: Uri? = data.data

                //Укзать для каких полей ваш запрос должен возвращать знечения
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                if (contactUri != null){
                    val cursor = requireActivity().contentResolver.query(
                        contactUri,
                        queryFields,
                        null,
                        null,
                        null
                    )
                    cursor?.use {
                        if (it.count == 0)
                            return

                        //первый столбец первой строки данных и есть имя подозреваемого
                        it.moveToFirst()
                        val suspect = it.getString(0)
                        crime.suspect = suspect
                        crimeDetailViewModel.saveCrime(crime)
                        suspectButton.text = suspect
                    }

                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    companion object{
        fun newInstance(crimeId: UUID): CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}