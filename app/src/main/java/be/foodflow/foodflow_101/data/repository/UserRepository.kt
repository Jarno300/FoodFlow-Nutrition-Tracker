package be.foodflow.foodflow_101.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import be.foodflow.foodflow_101.data.database.UserDao
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.model.UserProfile
import be.foodflow.foodflow_101.util.NutritionCalculator


class UserRepository(private val userDao: UserDao) {
    private val TAG = "UserRepository"

    val userProfile: LiveData<UserProfile?> = userDao.getUserProfile()
    val nutritionGoals: LiveData<NutritionGoals?> = userDao.getNutritionGoals()

    suspend fun saveUserProfile(userProfile: UserProfile) {
        Log.d(TAG, "Saving user profile - before calculation: ${userProfile}")
        val calculatedProfile = NutritionCalculator.calculateEnergyNeeds(userProfile)
        Log.d(TAG, "Saving user profile - after calculation: ${calculatedProfile}")
        userDao.insertUserProfile(calculatedProfile)
    }

    suspend fun saveNutritionGoals(userProfile: UserProfile, nutritionGoals: NutritionGoals) {
        Log.d(TAG, "Saving nutrition goals - userProfile: ${userProfile}")
        Log.d(TAG, "Saving nutrition goals - before calculation: ${nutritionGoals}")
        val calculatedGoals = NutritionCalculator.calculateMacroTargets(userProfile, nutritionGoals)
        Log.d(TAG, "Saving nutrition goals - after calculation: ${calculatedGoals}")
        userDao.insertNutritionGoals(calculatedGoals)
    }
}