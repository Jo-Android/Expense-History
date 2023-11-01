package com.hgtech.expensehistory.di.config

import android.app.Application
import android.content.ContextWrapper
import com.hgtech.expensehistory.di.module.Module
import com.pixplicity.easyprefs.library.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class App:  Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(Module.databaseModule, Module.viewModule, Module.repositories))
        }

        Prefs.Builder()
            .setContext(applicationContext)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true).build()
    }
}