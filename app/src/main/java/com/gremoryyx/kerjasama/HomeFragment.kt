package com.gremoryyx.kerjasama

import TabPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val fragment = listOf(
            JobListFragment(),
            RegisteredJobFragment()
        )

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabTitles = listOf("Job List", "Registered") // add the tab titles here

        val fragmentAdapter = TabPagerAdapter(this, fragment)
        viewPager.adapter = fragmentAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        return view
    }
}