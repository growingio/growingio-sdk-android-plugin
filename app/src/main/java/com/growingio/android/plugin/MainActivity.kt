package com.growingio.android.plugin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.growingio.android.sdk.autotrack.GrowingAutotracker

class MainActivity : AppCompatActivity() {
    private val mAllFragments = arrayListOf<Fragment>()
    private var current: Fragment? = null
    private lateinit var tttt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hide_fragmentx)
        findViewById<View>(R.id.f).setOnClickListener { setFragment(0) }

        findViewById<View>(R.id.f1).setOnClickListener { setFragment(1) }

        findViewById<View>(R.id.f2).setOnClickListener { setFragment(2) }

        findViewById<View>(R.id.f3).setOnClickListener { setFragment(3) }

        tttt = findViewById<TextView>(R.id.tttt)
        tttt.setOnClickListener {
            tttt.visibility = if (tttt.isVisible == true) View.GONE else View.VISIBLE
        }

        GrowingAutotracker.get().autotrackPage(this, javaClass.simpleName)
    }

    private fun setFragment(index: Int) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        var fragment = mAllFragments.getOrNull(index)
        if (fragment != null && fragment.isAdded) {
            beginTransaction.show(fragment)
        } else {
            fragment = createdFragment(index)
            beginTransaction.add(R.id.frame_content, fragment)
        }
        beginTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        if (current?.isAdded() == true) {
            beginTransaction.hide(current!!)
            beginTransaction.setMaxLifecycle(current!!, Lifecycle.State.STARTED)
        }
        current = fragment
        beginTransaction.commitAllowingStateLoss()
    }

    private fun createdFragment(index: Int): Fragment {
        if (index == 0) {
            val fragment: Fragment = ColorFragment.newInstance(R.color.demos_blue)
            mAllFragments.add(index, fragment)
            return fragment
        }
        if (index == 1) {
            val fragment: Fragment = ColorFragment.newInstance(R.color.demos_orange)
            mAllFragments.add(index, fragment)
            return fragment
        }
        if (index == 2) {
            val fragment: Fragment = ColorFragment.newInstance(R.color.demos_pink)
            mAllFragments.add(index, fragment)
            return fragment
        }
        val fragment: Fragment = ColorFragment.newInstance(R.color.demos_yellow)
        mAllFragments.add(index, fragment)
        return fragment
    }

}