package com.bignerdranch.android.criminalintent

import java.util.Date
import java.util.UUID

data class Crime(var id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date: Date = Date(),
                 var isSolved:Boolean = false,
                 var requiredPolice: Int = (0..1).random())