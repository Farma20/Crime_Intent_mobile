package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "dialog_date"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "dialog_time"

//Создание UI-фрагмента (контроллера)
class CrimeFragment: Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox

    //определение интерфейса обратного вызова DatePickerFragment для передачи данных между Fragments
    override fun onDateSelected(date: Date) {
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

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
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

        //Настройка виджета кнопки через apply
//        dateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }

        return view
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