package vn.dtc.project.grabfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import vn.dtc.project.grabfood.data.CartFood
import vn.dtc.project.grabfood.databinding.BillingFoodRvItemBinding
import vn.dtc.project.grabfood.helper.getFoodPrice

class BillingFoodAdapter: Adapter<BillingFoodAdapter.BillingFoodViewHolder>() {

    inner class BillingFoodViewHolder(val binding: BillingFoodRvItemBinding): ViewHolder(binding.root){
        fun bind(billingFood: CartFood){
            binding.apply {
                Glide.with(itemView).load(billingFood.food.images[0]).into(imageCartFood)
                tvFoodCartName.text = billingFood.food.name
                tvBillingFoodQuantity.text = billingFood.quantity.toString()

                val priceAfterPercentage = billingFood.food.offerPercentage.getFoodPrice(billingFood.food.price)
                tvFoodCartPrice.text = "$ ${String.format("%.2f",priceAfterPercentage)}"
            }
        }
    }

    private val diffUtil = object: DiffUtil.ItemCallback<CartFood>(){
        override fun areContentsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem.food == newItem.food
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingFoodViewHolder {
        return BillingFoodViewHolder(
            BillingFoodRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingFoodViewHolder, position: Int) {
        val billingFood = differ.currentList[position]

        holder.bind(billingFood)
    }


}