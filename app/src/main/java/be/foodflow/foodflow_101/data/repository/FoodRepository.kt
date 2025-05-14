package be.foodflow.foodflow_101.data.repository

import androidx.lifecycle.LiveData
import be.foodflow.foodflow_101.data.database.FoodDao
import be.foodflow.foodflow_101.data.model.Food


class FoodRepository(private val foodDao: FoodDao) {
    val allFoods: LiveData<List<Food>> = foodDao.getAllFoods()

    suspend fun insert(food: Food): Long {
        return foodDao.insert(food)
    }

    suspend fun update(food: Food) {
        foodDao.update(food)
    }

    suspend fun delete(food: Food) {
        foodDao.delete(food)
    }

    suspend fun getFoodById(id: Long): Food? {
        return foodDao.getFoodById(id)
    }

    fun searchFoods(query: String): LiveData<List<Food>> {
        return foodDao.searchFoods(query)
    }
}