package com.bignerdranch.android.criminalintent

import java.util.Date
import java.util.UUID
import kotlin.random.Random

//Подключение сгенерированных аннотаций библиотеки room
annotation class PrimaryKey
annotation class Entity

//Обновлем класс модели до сущности базы данных
@Entity
data class Crime(@PrimaryKey var id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date: Date = Date(),
                 var isSolved:Boolean = Random.nextBoolean(),
                 var requiredPolice: Int = (0..1).random())


