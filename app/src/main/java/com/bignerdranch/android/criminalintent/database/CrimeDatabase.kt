package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent.Crime

//Соединение базы данных с сущностью, которую мы аннотировали
//Аннотация @Database определяет, что данный класс является базой данных приложения
@Database(entities = [Crime::class], version=2)

//Добавление созданных нами конвертеров типов
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase: RoomDatabase() {
    //Подключаем DAO, реализация которой будет генерироваться Room автоматически
    abstract fun crimeDao():CrimeDao
}

//Обновляем версию нашей базы данных (Добавили новое значение в таблицу)
val migration_1_2 =  object: Migration(1, 2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}