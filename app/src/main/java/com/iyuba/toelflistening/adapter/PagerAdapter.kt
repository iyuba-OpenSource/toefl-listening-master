package com.iyuba.toelflistening.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
苏州爱语吧科技有限公司
 */
class PagerAdapter(activity: FragmentActivity, private val list:List<Fragment>) :FragmentStateAdapter(activity) {
    override fun getItemCount(): Int =list.size

    override fun createFragment(position: Int): Fragment =list[position]

}