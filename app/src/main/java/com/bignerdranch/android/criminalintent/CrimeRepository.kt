package com.bignerdranch.android.criminalintent

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.criminalintent.database.CrimeDatabase
import java.util.UUID

private const val DATABASE_NAME = "crime-database"

//Объект для работы с базой данных
class CrimeRepository private constructor(context: Context) {

    //Инициализация базы данных
    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()

    //функции, которые репозиторий будет вызывать через DAO
    fun getCrimes(): List<Crime> = crimeDao.getCrimes()

    fun getCrime(id: UUID): Crime? = crimeDao.getCrime(id)

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