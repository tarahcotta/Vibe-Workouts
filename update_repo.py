with open("app/src/main/java/com/example/data/VitalRepository.kt", "r") as f:
    content = f.read()

new_methods = """
    val bookmarkedExercises: Flow<List<BookmarkedExerciseEntity>> = dao.getBookmarkedExercises()

    suspend fun addBookmark(exerciseId: String) {
        dao.insertBookmarkedExercise(BookmarkedExerciseEntity(exerciseId))
    }

    suspend fun removeBookmark(exerciseId: String) {
        dao.deleteBookmarkedExercise(exerciseId)
    }

    fun getSetsForSession(sessionId: Long): Flow<List<LoggedSetEntity>> {"""

content = content.replace("    fun getSetsForSession(sessionId: Long): Flow<List<LoggedSetEntity>> {", new_methods)

with open("app/src/main/java/com/example/data/VitalRepository.kt", "w") as f:
    f.write(content)
