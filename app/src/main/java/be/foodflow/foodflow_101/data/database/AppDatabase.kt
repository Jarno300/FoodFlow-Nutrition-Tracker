package be.foodflow.foodflow_101.data.database

import android.content.Context
import be.foodflow.foodflow_101.data.database.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import be.foodflow.foodflow_101.data.model.*
import be.foodflow.foodflow_101.data.preset.preloadedFoods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(
    entities = [Food::class, FoodLog::class, UserProfile::class, NutritionGoals::class, LogHistory::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun logDao(): LogDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "macro_tracker_database"
                )
                    .addCallback(DatabaseCallback(context, scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Prepopulate with food data from assets
                    populateFoodDatabase(database.foodDao())
                }
            }
        }
        
        // Add callback for database opened to ensure preloaded foods after migration
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Check if food table is empty and populate if needed
                    val foodCount = database.foodDao().getFoodCount()
                    if (foodCount == 0) {
                        populateFoodDatabase(database.foodDao())
                    }
                }
            }
        }

        private suspend fun populateFoodDatabase(foodDao: FoodDao) {
            val existing = foodDao.getAllFoodsOnce()
            if (existing.isEmpty()) {
                preloadedFoods.forEach { foodDao.insert(it) }
            }
        }
    }
}