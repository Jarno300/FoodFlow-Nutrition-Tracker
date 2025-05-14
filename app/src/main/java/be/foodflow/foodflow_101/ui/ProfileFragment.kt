package be.foodflow.foodflow_101.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import be.foodflow.foodflow_101.MyApp
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.model.UserProfile
import be.foodflow.foodflow_101.data.repository.UserRepository

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    
    // User profile fields
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var ageInput: EditText
    
    // Nutrition goals fields
    private lateinit var deficitInput: EditText
    private lateinit var proteinPerKgInput: EditText
    private lateinit var fatPercentInput: EditText
    
    // Result displays
    private lateinit var bmrText: TextView
    private lateinit var tdeeText: TextView
    private lateinit var targetCaloriesText: TextView
    private lateinit var targetProteinText: TextView
    private lateinit var targetFatText: TextView
    private lateinit var targetCarbsText: TextView
    
    // Buttons
    private lateinit var calculateButton: Button
    private lateinit var saveGoalsButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initViews(view)
        
        // Initialize view model
        val app = requireActivity().application as MyApp
        val userRepository = UserRepository(app.database.userDao())
        
        val factory = ProfileViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        
        // Set up observers
        setupObservers()
        
        // Set up click listeners
        setupClickListeners()
    }
    
    private fun initViews(view: View) {
        // User profile fields
        weightInput = view.findViewById(R.id.input_weight)
        heightInput = view.findViewById(R.id.input_height)
        ageInput = view.findViewById(R.id.input_age)
        
        // Nutrition goals fields
        deficitInput = view.findViewById(R.id.input_deficit)
        proteinPerKgInput = view.findViewById(R.id.input_protein_per_kg)
        fatPercentInput = view.findViewById(R.id.input_fat_percent)
        
        // Result displays
        bmrText = view.findViewById(R.id.text_bmr)
        tdeeText = view.findViewById(R.id.text_tdee)
        targetCaloriesText = view.findViewById(R.id.text_target_calories)
        targetProteinText = view.findViewById(R.id.text_target_protein)
        targetFatText = view.findViewById(R.id.text_target_fat)
        targetCarbsText = view.findViewById(R.id.text_target_carbs)
        
        // Buttons
        calculateButton = view.findViewById(R.id.button_calculate)
        saveGoalsButton = view.findViewById(R.id.button_save_goals)
    }
    
    private fun setupObservers() {
        // Observe user profile
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                updateProfileUI(it)
            }
        }
        
        // Observe nutrition goals
        viewModel.nutritionGoals.observe(viewLifecycleOwner) { goals ->
            goals?.let {
                updateGoalsUI(it)
            }
        }
    }
    
    private fun setupClickListeners() {
        calculateButton.setOnClickListener {
            if (validateProfileInputs()) {
                val userProfile = createUserProfileFromInputs()
                viewModel.saveUserProfile(userProfile)
            }
        }
        
        saveGoalsButton.setOnClickListener {
            if (validateGoalsInputs()) {
                // Get the current user profile from viewModel instead of creating a new one
                val currentProfile = viewModel.userProfile.value
                if (currentProfile != null) {
                    val nutritionGoals = createNutritionGoalsFromInputs()
                    viewModel.saveNutritionGoals(currentProfile, nutritionGoals)
                } else {
                    Toast.makeText(
                        context,
                        "Please calculate BMR/TDEE first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun validateProfileInputs(): Boolean {
        val weight = weightInput.text.toString().toFloatOrNull()
        val height = heightInput.text.toString().toFloatOrNull()
        val age = ageInput.text.toString().toIntOrNull()
        
        if (weight == null || height == null || age == null) {
            Toast.makeText(
                context,
                "Please enter valid values for weight, height, and age",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        if (weight <= 0 || height <= 0 || age <= 0) {
            Toast.makeText(
                context,
                "Weight, height, and age must be positive values",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        return true
    }
    
    private fun validateGoalsInputs(): Boolean {
        val deficit = deficitInput.text.toString().toFloatOrNull()
        val proteinPerKg = proteinPerKgInput.text.toString().toFloatOrNull()
        val fatPercent = fatPercentInput.text.toString().toFloatOrNull()
        
        if (deficit == null || proteinPerKg == null || fatPercent == null) {
            Toast.makeText(
                context,
                "Please enter valid values for deficit, protein, and fat goals",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        // Updated validation logic
        if (deficit <= 0 || deficit > 1 || proteinPerKg <= 0 || fatPercent <= 0 || fatPercent > 100) {
            Toast.makeText(
                context,
                "Deficit should be between 0-1, protein should be positive, fat % should be between 0-100",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        return true
    }
    
    private fun createUserProfileFromInputs(): UserProfile {
        val weight = weightInput.text.toString().toFloat()
        val height = heightInput.text.toString().toFloat()
        val age = ageInput.text.toString().toInt()
        
        return UserProfile(
            weight = weight,
            height = height,
            age = age,
            bmr = 0f, // Will be calculated in repository
            tdee = 0f  // Will be calculated in repository
        )
    }
    
    private fun createNutritionGoalsFromInputs(): NutritionGoals {
        val deficit = deficitInput.text.toString().toFloat()
        val proteinPerKg = proteinPerKgInput.text.toString().toFloat()
        
        // Get the fat percentage input (e.g., 30 for 30%)
        val fatPercentInput = fatPercentInput.text.toString().toFloat()
        
        // Convert to decimal by dividing by 100 (e.g., 30 / 100 = 0.3)
        val fatPercent = fatPercentInput / 100f
        
        Log.d("ProfileFragment", "Fat percentage: input=$fatPercentInput, decimal=$fatPercent")
        
        return NutritionGoals(
            deficitPercentage = deficit,
            targetProteinPerWeight = proteinPerKg,
            targetFatPercentage = fatPercent,
            dailyKcal = 0f,      // Will be calculated in repository
            dailyProtein = 0f,   // Will be calculated in repository
            dailyFat = 0f,       // Will be calculated in repository
            dailyCarbs = 0f      // Will be calculated in repository
        )
    }
    
    private fun updateProfileUI(profile: UserProfile) {
        // Update result displays
        bmrText.text = "BMR: ${profile.bmr.toInt()} kcal"
        tdeeText.text = "TDEE: ${profile.tdee.toInt()} kcal"
        
        // Update input fields if they're empty
        if (weightInput.text.isNullOrEmpty()) {
            weightInput.setText(profile.weight.toString())
        }
        if (heightInput.text.isNullOrEmpty()) {
            heightInput.setText(profile.height.toString())
        }
        if (ageInput.text.isNullOrEmpty()) {
            ageInput.setText(profile.age.toString())
        }
    }
    
    private fun updateGoalsUI(goals: NutritionGoals) {
        // Update result displays
        targetCaloriesText.text = "Target calories: ${goals.dailyKcal.toInt()} kcal"
        targetProteinText.text = "Target protein: ${goals.dailyProtein.toInt()} g"
        targetFatText.text = "Target fat: ${goals.dailyFat.toInt()} g"
        targetCarbsText.text = "Target carbs: ${goals.dailyCarbs.toInt()} g"
        
        // Update input fields if they're empty
        if (deficitInput.text.isNullOrEmpty()) {
            deficitInput.setText(goals.deficitPercentage.toString())
        }
        if (proteinPerKgInput.text.isNullOrEmpty()) {
            proteinPerKgInput.setText(goals.targetProteinPerWeight.toString())
        }
        if (fatPercentInput.text.isNullOrEmpty()) {
            fatPercentInput.setText((goals.targetFatPercentage * 100).toString())
        }
    }
} 