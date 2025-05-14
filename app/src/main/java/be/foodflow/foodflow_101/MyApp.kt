package be.foodflow.foodflow_101

import android.app.Application
import be.foodflow.foodflow_101.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApp : Application() {

    // Application-wide coroutine scope
    private val appScope = CoroutineScope(SupervisorJob())

    // Room database instance
    val database by lazy { AppDatabase.getDatabase(this, appScope) }
}
