package be.foodflow.foodflow_101.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)  val id: Long = 1,  // Single user system
    val weight: Float,  // in kg
    val height: Float,  // in cm
    val age: Int,

    // Calculated values
    val bmr: Float,
    val tdee: Float
)