package com.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel: ViewModel() {
    //Изменяемый список, хранящий экземпляры модели Crime
    val crimes = mutableListOf<Crime>()

    init{
        //заполнение списка моделей фиктивными данными
        for (i in 0 until 100){
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0

            crimes += crime
        }
    }
}