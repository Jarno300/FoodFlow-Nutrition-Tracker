package be.foodflow.foodflow_101.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_goals")
data class NutritionGoals(
    @PrimaryKey(autoGenerate = true) val userId: Long = 1,  // References UserProfile
    val deficitPercentage: Float,  // e.g., 0.8 for 20% deficit
    val targetProteinPerWeight: Float,  // g per kg of body weight
    val targetFatPercentage: Float,  // e.g., 0.3 for 30% of calories from fat

    // Calculated values
    val dailyKcal: Float,
    val dailyProtein: Float,
    val dailyFat: Float,
    val dailyCarbs: Float
) {
    override fun toString(): String {
        return "NutritionGoals(" +
               "deficit=$deficitPercentage, " +
               "proteinPerKg=$targetProteinPerWeight, " +
               "fatPct=$targetFatPercentage, " +
               "kcal=$dailyKcal, " +
               "protein=$dailyProtein, " +
               "fat=$dailyFat, " +
               "carbs=$dailyCarbs)"
    }
}