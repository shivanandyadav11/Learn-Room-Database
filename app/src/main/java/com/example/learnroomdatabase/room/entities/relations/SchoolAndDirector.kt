package com.example.learnroomdatabase.room.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.learnroomdatabase.room.entities.Director
import com.example.learnroomdatabase.room.entities.School

data class SchoolAndDirector(
    @Embedded
    val school: School,
    @Relation(
        parentColumn = "schoolName",
        entityColumn = "schoolName"
    )
    val director: Director
)
