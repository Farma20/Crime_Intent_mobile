package com.bignerdranch.android.criminalintent

import android.app.Application

//Подкласс приложения, который может отслеживать жизненный цикл самого приложения
//Инициализируем CrimeRepository при создании приложения
class CriminalIntentApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}