package com.daharansemar.adminresto.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

object FirebaseStarter {

    private val firebase = Firebase

    fun firebaseAuth():FirebaseAuth{
        return firebase.auth
    }

    fun firebaseFirestore():FirebaseFirestore{
        return firebase.firestore
    }

    fun firebaseStorage():FirebaseStorage{
        return firebase.storage
    }

    fun firebaseLogOut(){
        firebase.auth.signOut()
    }

}