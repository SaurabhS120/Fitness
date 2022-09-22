package com.example.fitness.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.fitness.domain.LocalRepo

class LocalSpRepo(applicationContext: Context) : LocalRepo {
    val spName = "config"
    val first_launch_key = "first_launch"
    var sp: SharedPreferences

    init {
        sp = applicationContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    override fun isFirstLaunch(): Boolean {
        sp.apply {
            return getBoolean(first_launch_key, true)
                .also {
                    edit {
                        putBoolean(first_launch_key, false)
                    }
                }
        }

    }
}