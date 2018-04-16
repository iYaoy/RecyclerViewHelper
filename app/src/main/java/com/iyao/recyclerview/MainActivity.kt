package com.iyao.recyclerview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.ViewGroup
import android.widget.TextView
import com.iyao.recyclerviewhelper.adapter.*
import com.iyao.recyclerviewhelper.itemdecoration.GridLayoutItemDecoration
import com.iyao.recyclerviewhelper.touchevent.MutableListAdapterImpl
import com.iyao.recyclerviewhelper.touchevent.addOnItemClickListener
import com.iyao.recyclerviewhelper.touchevent.addOnItemLongClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    private val itemTouchHelper = ItemTouchHelper(MutableListAdapterImpl(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0))

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                        //statusView
                            in -100..-1 -> spanCount
                            in android.R.layout.simple_list_item_multiple_choice + 1..android.R.layout.simple_list_item_multiple_choice + 3 ->
                                spanCount
                            else -> 1
                        }
                    }
                }
            }
            itemTouchHelper.attachToRecyclerView(this)
            addItemDecoration(GridLayoutItemDecoration().apply {
                startAndEndDecoration = 50
                topAndBottomDecoration = 30
                horizontalMiddleDecoration = 30
                verticalMiddleDecoration = 30
                decorateFullItem = true
            })
            adapter = CachedStatusWrapper().apply {
                client = CachedHeaderAndFooterWrapper().apply {
                    client = CachedMultipleChoiceWrapper().apply {
                        setHasStableIds(true)
                        client = object : CachedAutoRefreshAdapter<String>() {

                            override fun getItemId(position: Int) = if (position in 0 until itemCount) position.toLong() else -1

                            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
                                return layoutInflater
                                        .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
                                        .let {
                                            it.setBackgroundColor(Color.WHITE)
                                            CacheViewHolder(it)
                                        }
                            }

                            override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {
                                holder.childView<TextView>(android.R.id.text1)?.text = get(position)
                            }

                            override fun onBindViewHolder(holder: CacheViewHolder, position: Int, payloads: MutableList<Any>) {
                                holder.childView<TextView>(android.R.id.text1)?.run {
                                    when {
                                        payloads.isEmpty() -> super.onBindViewHolder(holder, position, payloads)
                                    }
                                }
                            }
                        }
                    }
                }.apply {
                    addHeader(android.R.layout.simple_list_item_multiple_choice + 1,
                            CacheViewHolder(layoutInflater
                                    .inflate(android.R.layout.simple_list_item_multiple_choice,
                                            recycler_view,
                                            false))
                                    .apply {
                                        itemView.setBackgroundColor(Color.WHITE)
                                        childView<TextView>(android.R.id.text1)?.text = "Header: 菲利普亲王入院"
                                    })
                    addHeader(android.R.layout.simple_list_item_multiple_choice + 2,
                            CacheViewHolder(layoutInflater
                                    .inflate(android.R.layout.simple_list_item_multiple_choice,
                                            recycler_view,
                                            false))
                                    .apply {
                                        itemView.setBackgroundColor(Color.WHITE)
                                        childView<TextView>(android.R.id.text1)?.text = "Header: 美国公布征税清单"
                                    })
                    addFooter(android.R.layout.simple_list_item_multiple_choice + 3,
                            CacheViewHolder(layoutInflater
                                    .inflate(android.R.layout.simple_list_item_multiple_choice,
                                            recycler_view,
                                            false))
                                    .apply {
                                        itemView.setBackgroundColor(Color.WHITE)
                                        childView<TextView>(android.R.id.text1)?.text = "Footer: 女孩感冒右腿截肢"
                                    })
                }
            }.apply {
                addStatusView(-2, CacheViewHolder(layoutInflater.inflate(R.layout.layout_data_empty, recycler_view, false)))
            }
            addOnItemClickListener { _, viewHolder ->
                adapter.takeIsInstance<CachedMultipleChoiceWrapper>()?.run {
                    adapter.getWrappedPosition(this, viewHolder.adapterPosition).run {
                        setItemChecked(this, !isItemChecked(this))
                    }
                }
                adapter.takeIsInstance<CachedStatusWrapper>()?.run {
                    when (viewHolder.itemViewType) {
                        -2 -> setCurrentStatusIf(-2, { takeIsInstance<CachedAutoRefreshAdapter<String>>()?.itemCount == 0 })
                        0 -> Unit
                        else -> setCurrentStatus(-2)
                    }
                }
            }
            addOnItemLongClickListener { _, viewHolder ->
                adapter.takeIsInstance<CachedStatusWrapper>()?.run {
                    when (viewHolder.itemViewType) {
                        -2 -> setCurrentStatusIf(-2, { takeIsInstance<CachedAutoRefreshAdapter<String>>()?.itemCount == 0 })
                        in android.R.layout.simple_list_item_multiple_choice + 1
                                ..android.R.layout.simple_list_item_multiple_choice + 3  -> setCurrentStatus(-2)
                        0 -> itemTouchHelper.startDrag(viewHolder)
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        @Suppress("UNCHECKED_CAST")
        recycler_view.adapter.takeIsInstance<CachedAutoRefreshAdapter<String>>()?.run {
            listOf(
                    "臧鸿飞曝婚内出轨"
                    , "詹姆斯超越科比"
                    , "韩庚再聊退团经历"
                    , "韩庚再聊退团经历"
                    , "韩庚再聊退团经历"
                    , "韩庚再聊退团经历"
                    , "韩庚再聊退团经历"
                    , "女子在墓园摔伤"
            ).also {
                refresh(it, object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return size
                    }

                    override fun getNewListSize(): Int {
                        return it.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return get(oldItemPosition) == it[newItemPosition]
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return false
                    }
                })
            }

            listOf(
                    { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    , { add(3, "女子在墓园摔伤") }
                    ,{ add(0, "女孩感冒右腿截肢") }
                    ,{ this@run[3] = "女孩感冒右腿截肢" }
                    ,{ this@run[4] = "女孩感冒右腿截肢" }
                    ,{ addAll(listOf("菲利普亲王入院", "棚改专项债问世")) }
                    ,{ add("YouTube总部枪击") }
                    ,{ addAll(0, listOf("76人大胜篮网", "美国公布征税清单")) }
                    ,{ remove("76人大胜篮网") }
                    ,{ removeAll(listOf("76人大胜篮网", "美国公布征税清单")) }
                    ,{ retainAll(listOf("菲利普亲王入院", "棚改专项债问世")) }
                    ,{ addAll(listOf("76人大胜篮网", "美国公布征税清单", "女孩感冒右腿截肢", "美国公布征税清单")) }
            ).toMutableList().run {
                timer("AutoRefresh", false, 1000, 1000,
                        {
                            recycler_view.post {
                                if (isNotEmpty()) {
                                    removeAt(0).invoke()
                                }
                            }
                        })
            }
        }
    }
}