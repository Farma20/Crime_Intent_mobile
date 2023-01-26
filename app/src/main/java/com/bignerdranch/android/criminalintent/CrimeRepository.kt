package com.bignerdranch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintent.database.CrimeDatabase
import com.bignerdranch.android.criminalintent.database.migration_2_3
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

//Объект для работы с базой данных
class CrimeRepository private constructor(context: Context) {

    //Инициализация базы данных
    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_2_3).build()

    private val crimeDao = database.crimeDao()

    //Создание исполнителя
    private val executor = Executors.newSingleThreadExecutor()

    //функции, которые репозиторий будет вызывать через DAO
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime> = crimeDao.getCrime(id)

    //Оборачиваем функции обновления и вставок в исполнителя,
    //чтобы обработать их в отдельном потоке
    fun updateCrime(crime: Crime){
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime){
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }


    companion object{
        private var INSTANCE:CrimeRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get():CrimeRepository{
            return INSTANCE?:throw IllegalStateException("CrimeRepository must be initialized")
        }

    }
}