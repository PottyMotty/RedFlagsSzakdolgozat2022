package bme.spoti.redflags.ui.ingame.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.CardData
import bme.spoti.redflags.data.model.PackMetaInfo
import bme.spoti.redflags.databinding.CardItemBinding
import bme.spoti.redflags.databinding.PacksItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber

class CardAdapter(
	val context: Context,
	val isSabotagePhase: Boolean
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
	class CardViewHolder(val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)

	var cards = listOf<CardData>()
		private set
	var selected = mutableListOf<String>()
	var selectedCount = MutableStateFlow(0)
	var playerDoneCreating = MutableStateFlow(false)

	private fun getBackgroundColor(): Int {
		return if (isSabotagePhase) {
			ContextCompat.getColor(context, R.color.primary_red)
		} else {
			ContextCompat.getColor(context, R.color.light_sand)
		}
	}

	private fun getStrokeColorFor(card: CardData): Int {
		return if (selected.contains(card.content)) ContextCompat.getColor(
			context,
			R.color.ok_green
		) else ContextCompat.getColor(context, R.color.black_20)
	}

	private fun onClick(card: CardData) {
		if (isSabotagePhase) {
			if(selected.size==1 && !selected.contains(card.content)){
				notifyItemChanged(cards.indexOfFirst { it.content == selected[0] })
				selected.removeAt(0)
			}
		} else {
			if (selected.size >= 2 && !selected.contains(card.content)) {
				notifyItemChanged(cards.indexOfFirst { it.content == selected[0] })
				selected[0] = selected[1]
				selected.removeAt(1)
			}

		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
		return CardViewHolder(
			CardItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	suspend fun updateDataset(newDataset: List<CardData>) = withContext(Dispatchers.Default) {
		val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
			override fun getOldListSize(): Int {
				return cards.size
			}

			override fun getNewListSize(): Int {
				return newDataset.size
			}

			override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				return cards[oldItemPosition] == newDataset[newItemPosition]
			}

			override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				return cards[oldItemPosition] == newDataset[newItemPosition]
			}
		})
		withContext(Dispatchers.Main) {
			cards = newDataset
			diff.dispatchUpdatesTo(this@CardAdapter)
		}
	}
	fun setSelectedCards(selected: List<String>){
		Timber.d("set selected cards: $selected")
		this.selected = selected.toMutableList()
		selected.forEach { selectedContent->
			notifyItemChanged(cards.indexOfFirst { it.content ==  selectedContent})
		}
		selectedCount.update { selected.size }
		playerDoneCreating.update{true}
	}


	override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
		val card = cards[position]
		holder.binding.apply {
			backgroundCard.setCardBackgroundColor(getBackgroundColor())
			strokeCard.setCardBackgroundColor(getBackgroundColor())
			tvDescription.text = card.content
			strokeCard.strokeColor = getStrokeColorFor(card)
			strokeCard.setOnClickListener {
				if (!playerDoneCreating.value) {
					onClick(card)
					if (selected.contains(card.content)) selected.remove(card.content) else selected.add(
						card.content
					)
					strokeCard.strokeColor = getStrokeColorFor(card)
					selectedCount.update { selected.size }
				}
			}
		}
	}

	override fun getItemCount(): Int = cards.size
}
