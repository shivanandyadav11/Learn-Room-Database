package com.example.learnroomdatabase.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.learnroomdatabase.room.entities.Director
import com.example.learnroomdatabase.room.entities.School
import com.example.learnroomdatabase.room.entities.Student
import com.example.learnroomdatabase.room.entities.Subject
import com.example.learnroomdatabase.room.entities.relations.SchoolAndDirector
import com.example.learnroomdatabase.room.entities.relations.SchoolWithStudents
import com.example.learnroomdatabase.room.entities.relations.StudentSubjectCrossRef
import com.example.learnroomdatabase.room.entities.relations.StudentWithSubjects
import com.example.learnroomdatabase.room.entities.relations.SubjectWithStudents

@Dao
interface SchoolDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertSchool(school: School)

    @Insert(onConflict = REPLACE)
    suspend fun insertDirector(director: Director)

    @Insert(onConflict = REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Insert(onConflict = REPLACE)
    suspend fun insertSubjectCrossRef(crossRef: StudentSubjectCrossRef)

    @Query("SELECT * FROM school")
    suspend fun getSchools(): List<School>

    @Query("SELECT * FROM student")
    suspend fun getStudents(): List<Student>

    @Query("SELECT * FROM subject")
    suspend fun getSubjects(): List<Subject>

    @Transaction
    @Query("SELECT * FROM school WHERE schoolName = :schoolName")
    suspend fun getSchoolAndDirWithSchoolName(schoolName: String): List<SchoolAndDirector>

    @Transaction
    @Query("SELECT * FROM school WHERE schoolName = :schoolName")
    suspend fun getSchoolWithStudents(schoolName: String): List<SchoolWithStudents>

    @Transaction
    @Query("SELECT * FROM subject WHERE subjectName = :subjectName")
    suspend fun getStudentsOfSubject(subjectName: String): List<SubjectWithStudents>

    @Transaction
    @Query("SELECT * FROM student WHERE studentName = :studentName")
    suspend fun getSubjectsOfStudent(studentName: String): List<StudentWithSubjects>
}
