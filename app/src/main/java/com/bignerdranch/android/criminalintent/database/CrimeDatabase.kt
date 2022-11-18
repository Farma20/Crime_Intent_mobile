package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent.Crime

//Соединение базы данных с сущностью, которую мы аннотировали
//Аннотация @Database определяет, что данный класс является базой данных приложения
@Database(entities = [Crime::class], version=1)

//Добавление созданных нами конвертеров типов
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase: RoomDatabase() {
    //Подключаем DAO, реализация которой будет генерироваться Room автоматически
    abstract fun crimeDao():CrimeDao
}