package com.bignerdranch.android.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalintent.Crime
import java.util.UUID

//Data Access Object интерфейс, позволяющий работать с базой данных
//Оборачиваем возвращаемые данные в LiveData для обработки запросов к бд в отдельном потоке
@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime>
}