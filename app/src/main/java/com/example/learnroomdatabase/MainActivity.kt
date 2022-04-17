package com.example.learnroomdatabase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.learnroomdatabase.databinding.ActivityMainBinding
import com.example.learnroomdatabase.room.SchoolDataBase
import com.example.learnroomdatabase.room.dao.SchoolDao
import com.example.learnroomdatabase.room.entities.Director
import com.example.learnroomdatabase.room.entities.School
import com.example.learnroomdatabase.room.entities.Student
import com.example.learnroomdatabase.room.entities.Subject
import com.example.learnroomdatabase.room.entities.relations.SchoolWithStudents
import com.example.learnroomdatabase.room.entities.relations.StudentSubjectCrossRef
import com.example.learnroomdatabase.room.entities.relations.StudentWithSubjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = SchoolDataBase.getInstance(this).schoolDao
        getAllSchoolsAndUpdateSpinner(dao)
        getAllStudentsAndUpdateSpinner(dao)
        getAllSubjectsAndUpdateSpinner(dao)
        lifecycleScope.launch {
            binding.apply {
                saveSchoolDirector.setOnClickListener {
                    if (etDirectorName.text.isNotBlank() && etSchoolName.selectedItem.toString()
                        .isNotBlank()
                    ) {
                        showToast(true)
                        lifecycleScope.launch(Dispatchers.IO) {
                            dao.insertDirector(
                                Director(
                                    etDirectorName.text.toString(),
                                    etSchoolName.selectedItem.toString()
                                )
                            )
                        }
                    } else {
                        showToast(false)
                    }
                }
                saveSchool.setOnClickListener {
                    if (etSchool.text.isNotBlank()) {
                        showToast(true)
                        lifecycleScope.launch {
                            async(Dispatchers.IO) { dao.insertSchool(School(schoolName = etSchool.text.toString())) }.await()
                            withContext(Dispatchers.Main) {
                                getAllSchoolsAndUpdateSpinner(dao)
                            }
                        }
                    } else {
                        showToast(false)
                    }
                }

                saveSubject.setOnClickListener {
                    if (etSubject.text.isNotBlank()) {
                        showToast(true)
                        lifecycleScope.launch {
                            async {
                                dao.insertSubject(
                                    Subject(
                                        subjectName = etSubject.text.toString()
                                    )
                                )
                            }.await()
                            withContext(Dispatchers.Main) {
                                getAllSubjectsAndUpdateSpinner(dao)
                            }
                        }
                    } else {
                        showToast(false)
                    }
                }

                saveStudent3.setOnClickListener {
                    if (etStudent.text.isNotBlank() && etSemester.text.isNotBlank()) {
                        showToast(true)
                        lifecycleScope.launch {
                            async(Dispatchers.IO) {
                                dao.insertStudent(
                                    Student(
                                        studentName = etStudent.text.toString(),
                                        semester = etSemester.text.toString().toInt(),
                                        schoolName = spSchool.selectedItem.toString()
                                    )
                                )
                            }.await()
                            withContext(Dispatchers.Main) {
                                getAllStudentsAndUpdateSpinner(dao)
                            }
                        }
                    } else {
                        showToast(false)
                    }
                }

                studentWithSubject.setOnClickListener {
                    if (spStudent.selectedItem.toString()
                        .isNotBlank() && spSubject.selectedItem.toString().isNotBlank()
                    ) {
                        showToast(true)
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                dao.insertSubjectCrossRef(
                                    StudentSubjectCrossRef(
                                        spStudent.selectedItem.toString(),
                                        spSubject.selectedItem.toString()
                                    )
                                )
                            }
                        }
                    } else {
                        showToast(false)
                    }
                }
            }
        }

        /**
         * Check logs by filtering "show RoomDatabase" in logcat
         */
        binding.runQueries.setOnClickListener {
            runYourQueriesHere(dao)
        }
    }

    private fun getAllSubjectsAndUpdateSpinner(dao: SchoolDao) {
        lifecycleScope.launch {
            val result = getSubjects(dao)
            updateSpinner(binding.spSubject, result)
        }
    }

    private fun getAllStudentsAndUpdateSpinner(dao: SchoolDao) {
        lifecycleScope.launch {
            val result = getStudents(dao)
            updateSpinner(binding.spStudent, result)
        }
    }

    private fun runYourQueriesHere(dao: SchoolDao) {
        lifecycleScope.launch {
            val result = dao.getSchoolWithStudents("St. Joseph's School")
            val subjects = dao.getSubjectsOfStudent("Shivanand")
            logAllSchoolWithStudentData(result)
            logAllSubjectsOfStudent(subjects)
        }
    }

    private fun logAllSubjectsOfStudent(result: List<StudentWithSubjects>) {
        result.forEach {
            logSubjectsOfStudent(it)
        }
    }

    private fun logSubjectsOfStudent(school: StudentWithSubjects) {
        Log.d("show RoomDatabase", school.subject.toString())
    }

    private fun logAllSchoolWithStudentData(result: List<SchoolWithStudents>) {
        result.forEach {
            logSchoolWithStudentData(it)
        }
    }

    private fun logSchoolWithStudentData(school: SchoolWithStudents) {
        Log.d("show RoomDatabase", school.students.toString())
    }

    private fun showToast(state: Boolean) {
        Toast.makeText(this, if (state) "Data Saved" else "Fill all section", Toast.LENGTH_LONG)
            .show()
    }

    private fun getAllSchoolsAndUpdateSpinner(dao: SchoolDao) {
        lifecycleScope.launch {
            val result = getSchools(dao)
            updateSpinner(binding.etSchoolName, result)
            updateSpinner(binding.spSchool, result)
        }
    }

    private suspend fun getSchools(dao: SchoolDao): Array<String> = withContext(Dispatchers.IO) {
        return@withContext dao.getSchools().map { school -> school.schoolName }
            .toTypedArray()
    }

    private suspend fun getStudents(dao: SchoolDao): Array<String> = withContext(Dispatchers.IO) {
        return@withContext dao.getStudents().map { it.studentName }
            .toTypedArray()
    }

    private suspend fun getSubjects(dao: SchoolDao): Array<String> = withContext(Dispatchers.IO) {
        return@withContext dao.getSubjects().map { it.subjectName }
            .toTypedArray()
    }

    private fun updateSpinner(spinner: Spinner, result: Array<String>) {
        ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            result
        ).also { adapter ->
            adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
}
