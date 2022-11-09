package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

//Создание UI-фрагмента (контроллера)
class CrimeFragment: Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
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
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        //Настройка виджета кнопки через apply
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view
    }
}