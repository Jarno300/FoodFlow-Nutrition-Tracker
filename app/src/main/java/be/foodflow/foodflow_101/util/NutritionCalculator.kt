package be.foodflow.foodflow_101.util


import android.util.Log
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.model.UserProfile
import kotlin.math.max
import kotlin.math.round

object NutritionCalculator {
    private const val TAG = "NutritionCalculator"
    private const val DEBUG = false  // Disable for normal operation

    // Calculate BMR and TDEE based on user profile
    fun calculateEnergyNeeds(userProfile: UserProfile): UserProfile {
        // Mifflin-St Jeor Equation for BMR
        val bmr = (10 * userProfile.weight) + (6.25 * userProfile.height) - (5 * userProfile.age) + 5
        
        // Set TDEE equal to BMR for simplicity as requested
        val tdee = bmr

        if (DEBUG) Log.d(TAG, "BMR calculated as: $bmr, TDEE: $tdee")
        
        return userProfile.copy(bmr = round(bmr).toFloat(), tdee = round(tdee).toFloat())
    }

    // Calculate macro targets based on user profile and goals
    fun calculateMacroTargets(userProfile: UserProfile, goals: NutritionGoals): NutritionGoals {
        if (DEBUG) {
            Log.d(TAG, "----------- NUTRITION CALCULATION START -----------")
            Log.d(TAG, "User Profile: weight=${userProfile.weight}, height=${userProfile.height}, age=${userProfile.age}")
            Log.d(TAG, "User Profile: BMR=${userProfile.bmr}, TDEE=${userProfile.tdee}")
            Log.d(TAG, "Goals: deficit=${goals.deficitPercentage}, protein=${goals.targetProteinPerWeight}, fat%=${goals.targetFatPercentage}")
        }
        
        // Calculate daily calorie target with deficit (TDEE * deficit)
        val dailyKcal = userProfile.tdee * goals.deficitPercentage
        if (DEBUG) Log.d(TAG, "Daily calories = ${userProfile.tdee} * ${goals.deficitPercentage} = $dailyKcal")

        // Calculate protein target based on body weight (weight * protein target g/kg)
        val dailyProtein = userProfile.weight * goals.targetProteinPerWeight
        if (DEBUG) Log.d(TAG, "Daily protein = ${userProfile.weight} * ${goals.targetProteinPerWeight} = $dailyProtein")
        
        // Calculate fat target based on percentage of daily calories (calories * fat percentage / 9)
        val fatPercentage = goals.targetFatPercentage
        val dailyFat = (dailyKcal * fatPercentage) / 9f
        if (DEBUG) Log.d(TAG, "Daily fat = (${dailyKcal} * ${fatPercentage}) / 9 = $dailyFat")

        // Calculate carbs target using remaining calories
        // (calories - protein*4 - fat*9) / 4
        val proteinCalories = dailyProtein * 4f
        val fatCalories = dailyFat * 9f
        val carbCalories = max(0f, dailyKcal - proteinCalories - fatCalories)
        val dailyCarbs = carbCalories / 4f
        if (DEBUG) Log.d(TAG, "Daily carbs = (${dailyKcal} - ${proteinCalories} - ${fatCalories}) / 4 = $dailyCarbs")

        // Round values for display
        val roundedKcal = round(dailyKcal)
        val roundedProtein = round(dailyProtein)
        val roundedFat = round(dailyFat)
        val roundedCarbs = round(dailyCarbs)
        
        if (DEBUG) Log.d(TAG, "Final values: kcal=$roundedKcal, protein=$roundedProtein, fat=$roundedFat, carbs=$roundedCarbs")

        // Create a new NutritionGoals object with calculated values and all input parameters
        val result = NutritionGoals(
            userId = goals.userId,
            deficitPercentage = goals.deficitPercentage,
            targetProteinPerWeight = goals.targetProteinPerWeight,
            targetFatPercentage = goals.targetFatPercentage,
            dailyKcal = roundedKcal,
            dailyProtein = roundedProtein,
            dailyFat = roundedFat,
            dailyCarbs = roundedCarbs
        )
        
        if (DEBUG) {
            Log.d(TAG, "Returning: $result")
            Log.d(TAG, "----------- NUTRITION CALCULATION END -----------")
        }
        
        return result
    }

    // Helper function to round to nearest integer
    private fun round(value: Float): Float {
        return Math.round(value).toFloat()
    }
}
