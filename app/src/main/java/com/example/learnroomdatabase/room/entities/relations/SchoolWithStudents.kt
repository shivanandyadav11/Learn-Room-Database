package com.example.learnroomdatabase.room.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.learnroomdatabase.room.entities.School
import com.example.learnroomdatabase.room.entities.Student

data class SchoolWithStudents(
    @Embedded val school: School,
    @Relation(
        parentColumn = "schoolName",
        entityColumn = "schoolName"
    )
    val students: List<Student>
)
