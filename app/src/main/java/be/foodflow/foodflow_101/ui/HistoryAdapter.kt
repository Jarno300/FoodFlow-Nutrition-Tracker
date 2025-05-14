package be.foodflow.foodflow_101.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.model.LogHistory
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryAdapter(
    private var historyLogs: List<LogHistory>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_log, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyLogs[position])
    }

    override fun getItemCount() = historyLogs.size

    fun updateList(newLogs: List<LogHistory>) {
        historyLogs = newLogs
        notifyDataSetChanged()
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.text_history_date)
        private val kcalText: TextView = itemView.findViewById(R.id.text_history_kcal)
        private val proteinText: TextView = itemView.findViewById(R.id.text_history_protein)
        private val fatText: TextView = itemView.findViewById(R.id.text_history_fat)
        private val carbsText: TextView = itemView.findViewById(R.id.text_history_carbs)

        fun bind(historyLog: LogHistory) {
            // Format date
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("nl", "BE"))
            dateText.text = historyLog.date.format(formatter)
            
            // Format macros
            kcalText.text = "${historyLog.totalKcal.toInt()} kcal"
            proteinText.text = "${historyLog.totalProtein.toInt()} g"
            fatText.text = "${historyLog.totalFat.toInt()} g"
            carbsText.text = "${historyLog.totalCarbs.toInt()} g"
        }
    }
} 