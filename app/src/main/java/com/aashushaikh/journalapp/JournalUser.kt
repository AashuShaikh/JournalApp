package com.aashushaikh.journalapp

import android.app.Application

class JournalUser: Application() {

    var userId: String? = null
    var username: String? = null

    companion object {
        var instance: JournalUser? = null
            get() {
                if (field == null) {
                    field = JournalUser()
                }
                return field
            }
            private set
    }

    override fun onCreate() {
        super.onCreate()
    }
}