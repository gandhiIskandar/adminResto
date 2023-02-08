package com.daharansemar.adminresto.repository

import android.net.Uri
import android.util.Log
import com.daharansemar.adminresto.dataClass.*
import com.daharansemar.adminresto.util.FirebaseStarter.firebaseAuth
import com.daharansemar.adminresto.util.FirebaseStarter.firebaseFirestore
import com.daharansemar.adminresto.util.FirebaseStarter.firebaseStorage
import com.daharansemar.adminresto.util.Utilization.simpleDateFormatUntilMonth
import com.daharansemar.adminresto.util.Utilization.untilMonth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.model.mutation.ArrayTransformOperation
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class Repository {

    private val TAG = "Repository"


    companion object {
        @Volatile
        private lateinit var instance: Repository

        fun getInstance(): Repository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = Repository()
                }
                return instance
            }
        }
    }


    private val firestore = firebaseFirestore()
    private val storage = firebaseStorage()

    private val auth = firebaseAuth()

    suspend fun getPesanan(invoker: (MutableList<Pesanan>?) -> Unit) {

        coroutineScope {
            val pesananList = arrayListOf<Pesanan>()

            val collection = firestore.collection("Pesanan")

            collection.get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        it.result.forEach { data ->
                            val item = data.toObject<Pesanan>()
                            pesananList.add(item)
                        }

                        invoker.invoke(pesananList)

                    } else {
                        Log.e(TAG, it.exception.toString())
                    }
                }
        }


    }

    suspend fun setToken(data:String, callback: (Boolean) -> Unit){
        coroutineScope {
            val collection = firestore.collection("AdminToken")

            collection.document("B5zlAROLSZFjfiU8hVPD").set(mapOf("token" to data) ).addOnCompleteListener {

                    callback.invoke(it.isSuccessful)

            }
        }
    }

    suspend fun getImageData(callback: (List<ImageData>) -> Unit) = coroutineScope {
       firestore.collection("ImageData").get().addOnCompleteListener {
            if(it.isSuccessful ){
                val data = it.result.toObjects<ImageData>()
                callback.invoke(data)
            }
        }
    }

    suspend fun setImageData(data:ImageData, callback: (Boolean) -> Unit) = coroutineScope{

        val collection = firestore.collection("ImageData").document(data.id!!).set(data)

        collection.addOnCompleteListener {
            callback.invoke(it.isSuccessful)
        }
    }

    suspend fun removeImageData(id:String, callback: (Boolean) -> Unit) = coroutineScope {
        val ref = storage.reference.child("images/banner/${id}.jpg")

        ref.delete().addOnCompleteListener {
           if(it.isSuccessful){
               val collection = firestore.collection("ImageData").document(id)
               collection.delete().addOnCompleteListener { task ->
                   callback.invoke(task.isSuccessful)
               }
           }
        }


    }

    suspend fun updatePesanan(
        status: String,
        id: String,
        jumlah: Double,
        invoker: (Boolean) -> Unit
    ) {
        coroutineScope {


            firestore.collection("Pesanan").document(id).update("status", status)
                .addOnCompleteListener {
                    if (it.isSuccessful) {


                        invoker.invoke(true)


                    } else {
                        invoker.invoke(false)
                        Log.e(TAG, it.exception.toString())
                    }
                }
        }


    }

    suspend fun setPengeluaranOrPendapatan(cashFlow: CashFlow, invoker: (Boolean) -> Unit) {
        coroutineScope {
            updatePendapatanAtauPengeluaran(cashFlow) {
                invoker.invoke(it)
            }
        }
    }

    fun updatePendapatanAtauPengeluaran(
        cashFlow: CashFlow,
        invoker: (Boolean) -> Unit,

        ) {


        val id = firestore.collection("Cashflow").document().id
        val path =
            firestore.collection("Cashflow").document(id)

        cashFlow.id = id

        path.set(
            cashFlow
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                invoker.invoke(true)


            } else {
                invoker.invoke(false)


            }
        }


    }

    suspend fun updateCashflow(id: String, jumlah: Double, invoker: (Boolean) -> Unit) =
        coroutineScope {


            val path =
                firestore.collection("Cashflow").document(id)

            path.update("jumlah", jumlah.toLong()).addOnCompleteListener {
                if (it.isSuccessful) {
                    invoker.invoke(true)
                } else {
                    invoker.invoke(false)
                }
            }

        }

    suspend fun getKeterangan(callback: (List<Keterangan>) -> Unit) = coroutineScope {
        val path = firestore.collection("Keterangan").get()

        val dataArray = arrayListOf<Keterangan>()

        path.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.forEach { data ->
                    val hhh = data.toObject<Keterangan>()

                    dataArray.add(hhh)
                }
                callback.invoke(dataArray)

            }
        }
    }

    suspend fun setKeterangan(keterangan: Keterangan, callback: (Boolean) -> Unit) =
        coroutineScope {
            val path = firestore.collection("Keterangan")

            val id = keterangan.id ?: path.document().id

            keterangan.id = id

            path.document(id).set(keterangan).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }
        }

    suspend fun hapusKeterangan(id: String, callback: (Boolean) -> Unit) = coroutineScope {

        val path = firestore.collection("Keterangan")

        path.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        }
    }

    suspend fun hapusCashflow(id: String, invoker: (Boolean) -> Unit) = coroutineScope {
        val path = firestore.collection("Cashflow").document(id).delete()

        path.addOnCompleteListener {
            if (it.isSuccessful) {
                invoker.invoke(true)
            } else {
                invoker.invoke(false)
            }

        }
    }

    suspend fun getCashflow(invoker: (List<CashFlow>) -> Unit) =
        coroutineScope {
            val path =
                firestore.collection("Cashflow").orderBy("tanggal", Query.Direction.DESCENDING)

            path.get().addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val array = arrayListOf<CashFlow>()
                    it.result.forEach { data ->
                        val cashflow = data.toObject<CashFlow>()

                        array.add(cashflow)

                    }

                    invoker.invoke(array)

                }

            }

        }

    suspend fun getKategori(invoker: (MutableList<Kategori>?) -> Unit) {
        coroutineScope {
            val arrayKategori = mutableListOf<Kategori>()
            firestore.collection("Kategori").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.forEach { data ->
                            arrayKategori.add(data.toObject<Kategori>())
                            Log.d("kuontol", data.toObject<Kategori>().nama!!)
                        }

                        invoker.invoke(arrayKategori)


                    } else {
                        invoker.invoke(null)
                    }

                }
        }
    }

    suspend fun setKategori(kategori: Kategori, invoker: (Boolean) -> Unit) {
        coroutineScope {
            val collection = firestore.collection("Kategori")

            val id = collection.document().id

            collection.document(id).set(kategori).addOnCompleteListener {
                if (it.isSuccessful) {
                    invoker.invoke(true)
                } else {
                    invoker.invoke(false)
                }
            }

        }
    }

    suspend fun insertMenu(menu: Menu, invoker: (Boolean) -> Unit) {
        coroutineScope {


            val collection = firestore.collection("Menu")

            /*     val id = collection.document().id

                 //build data terakhir
                 menu.key = id*/

            collection.document(menu.key!!).set(menu).addOnCompleteListener {
                if (it.isSuccessful) {
                    invoker.invoke(true)
                } else {
                    invoker.invoke(false)
                }
            }


        }
    }

    suspend fun storeNewToken(token: String, callback: (Boolean) -> Unit) {
        coroutineScope {
            firestore.collection("AdminToken").document("B5zlAROLSZFjfiU8hVPD")
                .set(mapOf("token" to token)).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }
        }
    }

    suspend fun uploadImage(data: ByteArray, nama: String, invoker: (Pair<Uri, String>?) -> Unit) {
        coroutineScope {
            val ref = storage.reference.child("images/menu/atl${nama.substring(0..3)}.jpg")
            val upload = ref.putBytes(data)
            upload.addOnSuccessListener { up ->
                up.metadata?.reference?.downloadUrl?.addOnCompleteListener { download ->
                    if (download.isSuccessful) {
                        invoker.invoke(Pair(download.result, nama))
                    }

                }
            }.addOnFailureListener {
                invoker.invoke(null)
            }

        }
    }

    suspend fun uploadImageData(data:ByteArray, nama:String, invoker:(Uri)-> Unit) = coroutineScope{
            val ref = storage.reference.child("images/banner/${nama}.jpg")

        val upload = ref.putBytes(data)

        upload.addOnSuccessListener{
            up ->
            up.metadata?.reference?.downloadUrl?.addOnCompleteListener { download ->
                if(download.isSuccessful){
                    invoker.invoke(download.result)
                }
            }
        }
    }

    suspend fun insertOrUpdateKategori(kategori: Kategori, callback: (Boolean) -> Unit) {
        coroutineScope {
            val ref = firestore.collection("Kategori")

            val id = kategori.id?:ref.document().id

            kategori.id = id

            ref.document(id).set(kategori).addOnCompleteListener {

                callback.invoke(it.isSuccessful)

            }
        }
    }

    suspend fun removeKategori(id:String, callback: (Boolean) -> Unit) = coroutineScope {
        val ref = firestore.collection("Kategori")

     ref.document(id).delete().addOnCompleteListener {
          callback.invoke(it.isSuccessful)
      }




    }

    suspend fun getMenu(invoker: (List<Menu>?) -> Unit) {
        coroutineScope {
            val array: MutableList<Menu> = mutableListOf()
            val ref = firestore.collection("Menu").get()

            ref.addOnCompleteListener {

                if (it.isSuccessful) {

                    it.result.forEach { data ->
                        val yar = data.toObject<Menu>()



                        array.add(yar)
                    }

                    invoker.invoke(array)
                } else {
                    invoker.invoke(null)
                }

            }
        }
    }

    suspend fun updateMenu(data: Menu, invoker: (Boolean) -> Unit) {
        coroutineScope {
            firestore.collection("Menu").document(data.key!!).set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        invoker.invoke(true)
                    } else {
                        invoker.invoke(false)
                    }
                }
        }
    }

    suspend fun hapusMenu(id: String, invoker:(Boolean)->Unit){
        coroutineScope {
            firestore.collection("Menu").document(id).delete().addOnCompleteListener {
                invoker(it.isSuccessful)
            }
        }
    }

    suspend fun loginAttempt(list: List<String>, invoker: (Boolean) -> Unit) {
        coroutineScope {
            auth.signInWithEmailAndPassword(list[0], list[1]).addOnCompleteListener {
                if (it.isSuccessful) {
                    invoker.invoke(true)
                } else {
                    invoker.invoke(false)
                }
            }
        }

    }

    suspend fun getCurrentUser(invoker: (Boolean) -> Unit) {
        coroutineScope {
            if (auth.currentUser != null) {
                invoker.invoke(true)
            } else {
                invoker.invoke(false)
            }

        }
    }

}