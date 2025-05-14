package be.foodflow.foodflow_101.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.foodflow.foodflow_101.MyApp
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.model.Food
import be.foodflow.foodflow_101.data.repository.FoodRepository
import kotlinx.coroutines.launch

class FoodLogFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_food_log, container, false)
    }

    private lateinit var foodAdapter: FoodListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = FoodRepository((requireActivity().application as MyApp).database.foodDao())

        foodAdapter = FoodListAdapter(emptyList()) { food ->
            lifecycleScope.launch {
                repo.delete(food as Food)
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.food_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = foodAdapter

        repo.allFoods.observe(viewLifecycleOwner) { foods ->
            foodAdapter.updateList(foods)
        }

        val name = view.findViewById<EditText>(R.id.input_food_name)
        val amount = view.findViewById<EditText>(R.id.input_amount)
        val measure = view.findViewById<EditText>(R.id.input_measure)
        val kcal = view.findViewById<EditText>(R.id.input_kcal)
        val protein = view.findViewById<EditText>(R.id.input_protein)
        val fat = view.findViewById<EditText>(R.id.input_fat)
        val carbs = view.findViewById<EditText>(R.id.input_carbs)
        val button = view.findViewById<Button>(R.id.button_add_food)

        button.setOnClickListener {
            val nameStr = name.text.toString().trim()
            val amountStr = amount.text.toString().trim()
            val measureStr = measure.text.toString().trim()
            val kcalStr = kcal.text.toString().trim()
            val proteinStr = protein.text.toString().trim()
            val fatStr = fat.text.toString().trim()
            val carbsStr = carbs.text.toString().trim()

            if (nameStr.isEmpty() || amountStr.isEmpty() || measureStr.isEmpty()
                || kcalStr.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty() || carbsStr.isEmpty()
            ) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountVal = amountStr.toFloatOrNull()
            val kcalVal = kcalStr.toFloatOrNull()
            val proteinVal = proteinStr.toFloatOrNull()
            val fatVal = fatStr.toFloatOrNull()
            val carbsVal = carbsStr.toFloatOrNull()

            if (amountVal == null || kcalVal == null || proteinVal == null || fatVal == null || carbsVal == null) {
                Toast.makeText(
                    context,
                    "All numeric values must be valid numbers",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val food = Food(
                name = nameStr,
                amount = amountVal,
                measure = measureStr,
                kcal = kcalVal,
                protein = proteinVal,
                fat = fatVal,
                carbs = carbsVal
            )

            lifecycleScope.launch {
                repo.insert(food)
            }

            name.text.clear(); amount.text.clear(); measure.text.clear()
            kcal.text.clear(); protein.text.clear(); fat.text.clear(); carbs.text.clear()
        }
    }
}
