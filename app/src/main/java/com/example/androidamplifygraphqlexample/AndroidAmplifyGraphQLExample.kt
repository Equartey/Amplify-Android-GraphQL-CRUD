package com.example.androidamplifygraphqlexample

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify

class AndroidAmplifyGraphQLExample : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
            Log.i("AndroidAmplifyGraphQL", "Init Amplify")
        } catch (error: AmplifyException) {
            Log.e("AndroidAmplifyGraphQL", "Could not init Amplify", error)
        }
    }
}