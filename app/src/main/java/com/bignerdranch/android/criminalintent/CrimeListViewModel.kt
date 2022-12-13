package com.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    //Достаем данные из базы данных
    val crimeListLiveData = crimeRepository.getCrimes()

    //функция для добавления новой информации в бд
    fun addCrime(crime:Crime){
        crimeRepository.addCrime(crime)
    }
}