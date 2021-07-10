package com.naptech.today.func

import androidx.appcompat.app.AppCompatActivity

open class NaptechActivity: AppCompatActivity() {
    protected inner class FragmentManager(private val layout: Int) {
        fun replace(fragment: NaptectFragment) {
            supportFragmentManager.beginTransaction().replace(layout, fragment).commit()
        }
    }
}