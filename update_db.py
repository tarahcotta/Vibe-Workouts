with open("app/src/main/java/com/example/data/VitalDatabase.kt", "r") as f:
    content = f.read()

content = content.replace("LoggedSetEntity::class\n    ]", "LoggedSetEntity::class,\n        BookmarkedExerciseEntity::class\n    ]")
content = content.replace("version = 2", "version = 3")

with open("app/src/main/java/com/example/data/VitalDatabase.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/data/VitalDao.kt", "r") as f:
    content = f.read()

new_dao_methods = """
    // Bookmarked Exercises
    @Query("SELECT * FROM bookmarked_exercises")
    fun getBookmarkedExercises(): Flow<List<BookmarkedExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmarkedExercise(exercise: BookmarkedExerciseEntity)

    @Query("DELETE FROM bookmarked_exercises WHERE exerciseId = :exerciseId")
    suspend fun deleteBookmarkedExercise(exerciseId: String)
}"""

content = content.replace("}", new_dao_methods)

with open("app/src/main/java/com/example/data/VitalDao.kt", "w") as f:
    f.write(content)
