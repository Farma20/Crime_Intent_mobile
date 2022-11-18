package com.bignerdranch.android.criminalintent.database

import androidx.room.TypeConverter
import java.util.*

//Класс конвертирующий сложные типы данных исходной сущности в удобные типы для Room MySql
class CrimeTypeConverters {

    //Преобразование даты в long и обратно для записи и чтения с базы данных
    @TypeConverter
    fun fromDate(date: Date?): Long?{
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSequenceEpoch: Long?): Date?{
        return millisSequenceEpoch?.let{
            Date(it)
        }
    }

    //Преобразование id в string и обратно для записи и чтения с базы данных
    @TypeConverter
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }
}