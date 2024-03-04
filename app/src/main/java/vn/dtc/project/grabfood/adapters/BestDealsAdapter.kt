package vn.dtc.project.grabfood.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.databinding.BestDealsRvItemBinding

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding): ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(food: Food){
            binding.apply {
                Glide.with(itemView).load(food.images[0]).into(imgBestDeal)
                food.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * food.price
                    tvNewPrice.text = "$ ${String.format("%.2f",priceAfterOffer)}"
                }
                tvOldPrice.text = "$ ${food.price}"
                tvDealFoodName.text = food.name
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }
    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val food = differ.currentList[position]
        holder.bind(food)

        holder.itemView.setOnClickListener{
            onClick?.invoke(food)
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick:((Food) -> Unit)? = null
}
