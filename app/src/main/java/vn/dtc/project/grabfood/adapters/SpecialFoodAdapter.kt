package vn.dtc.project.grabfood.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.databinding.SpecialRvItemBinding

class SpecialFoodAdapter: RecyclerView.Adapter<SpecialFoodAdapter.SpecialFoodViewHolder>() {

    inner class SpecialFoodViewHolder(private val binding: SpecialRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(food: Food){
            binding.apply {
                Glide.with(itemView).load(food.images[0]).into(imageSpecialRvItem)
                tvAdName.text = food.name
                val price_text = food.price.toString()
                tvAdPrice.text =  "$ ${price_text}"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Food>(){
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialFoodViewHolder {
        return SpecialFoodViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialFoodViewHolder, position: Int) {
        val food = differ.currentList[position]
        holder.bind(food)

        holder.itemView.setOnClickListener{
            onClick?.invoke(food)
        }
    }

    var onClick:((Food) -> Unit)? = null
}