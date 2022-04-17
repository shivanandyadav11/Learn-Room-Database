package com.example.learnroomdatabase.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learnroomdatabase.room.dao.SchoolDao
import com.example.learnroomdatabase.room.entities.Director
import com.example.learnroomdatabase.room.entities.School
import com.example.learnroomdatabase.room.entities.Student
import com.example.learnroomdatabase.room.entities.Subject
import com.example.learnroomdatabase.room.entities.relations.StudentSubjectCrossRef

@Database(
    entities = [
        Director::class,
        School::class,
        Student::class,
        Subject::class,
        StudentSubjectCrossRef::class
    ],
    version = 2
)
abstract class SchoolDataBase : RoomDatabase() {
    abstract val schoolDao: SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDataBase? = null

        fun getInstance(context: Context): SchoolDataBase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDataBase::class.java,
                    "school_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
