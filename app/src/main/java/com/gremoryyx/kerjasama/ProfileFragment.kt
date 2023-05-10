package com.gremoryyx.kerjasama

import TabPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private lateinit var tabPagerAdapter: TabPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val fragment = listOf(
            ProfilePersonalFragment(),
            ProfileAccountFragment(),
        )

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.tab_container)
        val tabTitles = listOf("Register", "Account")

        // Initialize the TabPagerAdapter (Step 2)
        tabPagerAdapter = TabPagerAdapter(this, fragment)
        viewPager.adapter = tabPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        return view

    }
}