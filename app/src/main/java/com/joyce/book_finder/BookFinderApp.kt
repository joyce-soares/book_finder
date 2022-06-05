package com.joyce.book_finder

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseOptions
import com.joyce.book_finder.di.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

open class BookFinderApp: Application() {

    private lateinit var appContext: Context
    
    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() {
        val appModules = listOf(module)
        org.koin.core.context.startKoin {
           androidLogger(org.koin.core.logger.Level.ERROR)
            androidContext(this@BookFinderApp)
          modules(appModules)
        }
    }
}