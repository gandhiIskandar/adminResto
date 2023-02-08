package com.daharansemar.adminresto

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daharansemar.adminresto.databinding.FragmentDashboardBinding
import com.daharansemar.adminresto.repository.DataStoreRepository
import com.daharansemar.adminresto.util.FirebaseStarter.firebaseLogOut
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var dataStore: DataStoreRepository
    private lateinit var mContext: Context
    private lateinit var myToken: String
    private lateinit var menuHost:FragmentActivity
    private lateinit var menuProvider: MenuProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //   findNavController().clearBackStack()

         menuHost = requireActivity()

        getDatafromRepo()

        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.requestToken) {
                    if (::myToken.isInitialized) {
                        viewModel.setToken(myToken) {
                            if (it) {
                                toastMaker(mContext, "update token berhasil").show()
                            }
                        }
                    }
                }else{
                    firebaseLogOut()
                }

                return true
            }
        }

        menuHost.addMenuProvider(menuProvider)


        binding = FragmentDashboardBinding.inflate(layoutInflater)
        with(binding) {
            goToMenu.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_fragmentListMenu)
            }
            goToPembukuan.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_pembukuanFragment)

            }
            goToPesanan.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_fragmentListPesanan)
            }
            goToPoster.setOnClickListener{
                findNavController().navigate(R.id.action_dashboardFragment_to_fragmentEditBanner)
            }
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()

        menuHost.removeMenuProvider(menuProvider)
    }

    private fun requestToken(tokenny: String?) {


        FirebaseMessaging.getInstance().token.addOnCompleteListener {


            if (it.isSuccessful) {
                val token = it.result

                myToken = token


                if (tokenny != token || tokenny == null) {
                    viewModel.setTokenDataStore(token) { state ->
                        if (state) {
                            toastMaker(mContext, "update token berhasil").show()
                            setDataToken(token)
                        } else {
                           toastMaker(mContext, "update token gagal").show()
                        }
                    }
                }


            } else {
                Log.d("tokenomy", it.exception.toString())
            }
        }


    }


    private fun getDatafromRepo() {
        CoroutineScope(Dispatchers.Main).launch {
            val text = dataStore.getToken()

            text.collect { data ->
                requestToken(data)

                Log.d("tokener", data)


            }

        }
    }

    private fun setDataToken(tokenny: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStore.saveToken(tokenny!!)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        dataStore = DataStoreRepository(mContext)


    }
}