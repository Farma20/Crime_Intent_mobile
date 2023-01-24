package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
import kotlin.random.Random

//Обновлем класс модели до сущности базы данных
@Entity
data class Crime(@PrimaryKey var id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date: Date = Date(),
                 var isSolved:Boolean = false,
                 var suspect: String = ""
                 )
//                 var requiredPolice: Int = (0..1).random())


