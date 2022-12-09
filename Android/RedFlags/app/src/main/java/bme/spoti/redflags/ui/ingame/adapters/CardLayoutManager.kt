package bme.spoti.redflags.ui.ingame.adapters

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.PorterDuff
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.ui.graphics.Color
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.RecyclerView.*
import bme.spoti.redflags.R
import kotlin.math.abs

class CardLayoutManager(resources: Resources, private val screenWidth: Int) : LayoutManager() {
    override fun generateDefaultLayoutParams(): LayoutParams =
        LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    private val viewWidth = resources.getDimensionPixelSize(R.dimen.card_width)
    private val viewHeight = resources.getDimensionPixelSize(R.dimen.card_height)
    private var horizontalScrollOffset: Int = 0
    override fun canScrollHorizontally(): Boolean = true
    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler?, state: State?): Int {
        var actualScrolled =0
        val maxOffset= viewWidth*0.7*3.5
        actualScrolled = when {
            (horizontalScrollOffset+dx)< -50 -> {
                -50-horizontalScrollOffset
            }
            (horizontalScrollOffset+dx)> maxOffset -> {
                (maxOffset-horizontalScrollOffset).toInt()
            }
            else -> {
                dx
            }
        }
        horizontalScrollOffset += actualScrolled
        recycler?.let { fill(it) }
        return actualScrolled
    }

    override fun onLayoutChildren(recycler: Recycler?, state: State?) {
        recycler?.let { fill(it) }
    }

    private fun fill(recycler: Recycler) {
        detachAndScrapAttachedViews(recycler)
        val midpoint = screenWidth / 2f
        for (i in 0 until itemCount) {
            val left = (i * viewWidth*0.6f - horizontalScrollOffset).toInt()
            val right = left + viewWidth


            val view= recycler.getViewForPosition(i)
            val childMidpoint = (left + right) / 2f

            val amount =(-midpoint+childMidpoint)/60
            val top = (abs(amount)*10).toInt()
            val bottom = top + viewHeight
                addView(view)
            view.rotation=amount
            view.elevation = (1f-abs((midpoint-childMidpoint)/(screenWidth/2f)))*10
            view.translationZ =(1f-abs((midpoint-childMidpoint)/(screenWidth/2f)))*10
            measureChild(view, viewWidth, viewHeight)
            layoutDecorated(view,left,top,right, bottom)
        }
        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }
    private fun getTopOffsetForView(viewCentreX: Double): Int {
        val radius = screenWidth*2
        val cosAlpha = (radius - viewCentreX) / radius
        val alpha = Math.acos(MathUtils.clamp(cosAlpha, -1.0, 1.0))
        val y = radius - (radius * Math.sin(alpha))
        return y.toInt()
    }
}