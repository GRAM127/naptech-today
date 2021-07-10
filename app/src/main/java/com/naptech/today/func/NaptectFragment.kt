package com.naptech.today.func

import androidx.fragment.app.Fragment

open class NaptectFragment: Fragment() {
    protected inner class FragmentManager(private val layout: Int) {
        fun replace(fragment: NaptectFragment) {
            parentFragmentManager.beginTransaction().replace(layout, fragment).commit()
        }
    }
}