package com.example.reactiveapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reactiveapp.R
import com.example.reactiveapp.common.TrackingUtility
import com.example.reactiveapp.databinding.ItemRunBinding
import com.example.reactiveapp.db.Run
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {


    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding=ItemRunBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RunViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class RunViewHolder(val binding:ItemRunBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(run: Run?) {

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = run?.timeStamp!!
                }
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calendar.time)

                val avgSpeed = "${run?.avgSpeedInKMH}km/h"
            binding.tvAvgSpeed.text = avgSpeed

                val distanceInKm = "${run?.distanceInMeters!! / 1000f}km"
            binding.tvDistance.text = distanceInKm

            binding.tvTime.text = TrackingUtility.getFormattedStopWatchTime(run?.timeMills!!)

                val caloriesBurned = "${run?.caloriesBurned}kcal"
            binding.tvCalories.text = caloriesBurned

        }

    }


    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)

        holder.itemView.apply {
            Glide.with(this).load(run.img).into(holder.binding.ivRunImage)

        }
    }


}