package com.example.arthurvanremoortel_werkstuk.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Recipe::class, Ingredient::class], version = 1, exportSchema = false)
// Source: https://developer.android.com/codelabs/android-room-with-a-view-kotlin
public abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("DATABASE", "create.")
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.recipeDao())
                    }
                }
            }

            suspend fun populateDatabase(recipeDao: RecipeDao) {
                // Delete all content here.
                recipeDao.deleteAll()
                Log.d("DATABASE", "Populate.")

                // Add sample recipes.
                val recipe = Recipe(null,"Pizza Margherita", 8, false, 20)
                recipeDao.insert(recipe)
                val recipe2 = Recipe(null,"Pizza Hawaii", 3, false, 50)
                recipeDao.insert(recipe2)
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            Log.d("DATABASE", "get.")
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

    }

}
