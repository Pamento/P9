package com.openclassrooms.realestatemanager.ui.activity

import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment.Companion.newInstance
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment.Companion.newInstance
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.util.enums.EFragments
import android.os.Bundle
import android.view.Menu
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.fragments.*
import com.openclassrooms.realestatemanager.util.Constants.Constants
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var view: View
    private lateinit var binding: ActivityMainBinding
    private var mActivityHasTwoFragment = false
    private lateinit var mFragmentManager: FragmentManager
    private var mEFragments = EFragments.LIST
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        view = binding.root
        setFragmentManager()
        setContentView(view)
        checkIfActivityHasDoubleFragment()
        initListFragment()
    }

    private fun setFragmentManager() {
        mFragmentManager = supportFragmentManager
    }

    private fun checkIfActivityHasDoubleFragment() {
        val fc = binding.mainActivityFragmentContainerSecondary
        mActivityHasTwoFragment = fc != null
    }

    private fun setToolbarTitle(toolbarTitle: String, backUpButton: Boolean) {
        supportActionBar?.title = toolbarTitle
        if (backUpButton) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowCustomEnabled(true)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setDisplayShowCustomEnabled(false)
        }
        // invalidateOptionsMenu() for recreate menu according to functions of current fragment
        invalidateOptionsMenu()
    }

    private fun initListFragment() {
        // The function: initListFragment() is called only on the opening the application. She doesn't part of navigation.
        val fTransaction = mFragmentManager.beginTransaction()
        val listProperty = ListProperty.newInstance()
        fTransaction.replace(
            R.id.main_activity_fragment_container,
            listProperty,
            Constants.LIST_FRAGMENT
        ).commit()
        if (mActivityHasTwoFragment) initDetailFragment(true)
    }

    private fun initDetailFragment(firstPositionOfList: Boolean) {
        // The function: initDetailFragment() is called only on the opening the application. She doesn't part of navigation.
        val detail = newInstance(mActivityHasTwoFragment, firstPositionOfList)
        val ft = mFragmentManager.beginTransaction()
        ft.replace(
            R.id.main_activity_fragment_container_secondary,
            detail,
            Constants.DETAIL_FRAGMENT
        ).commit()
    }

    fun displayFragm(fragment: EFragments, toolbarTitle: String) {
        mEFragments = fragment
        val transaction = mFragmentManager.beginTransaction()
        when (fragment) {
            EFragments.LIST -> {
                setToolbarTitle(resources.getString(R.string.app_name), false)
                val lp = ListProperty.newInstance()
                transaction.replace(getFragmentContainer(fragment), lp, Constants.LIST_FRAGMENT)
                if (mActivityHasTwoFragment) {
                    initDetailFragment(false)
                }
            }
            EFragments.MAP -> if (isMapsServiceOk) {
                setToolbarTitle("New York", false)
                val mf = newInstance(mActivityHasTwoFragment)
                transaction.replace(getFragmentContainer(null), mf, Constants.MAP_FRAGMENT)
            } else {
                val msg = resources.getString(R.string.error_msg_map_service_not_available)
                NotifyBySnackBar.showSnackBar(1, view, msg)
            }
            EFragments.DETAIL -> {
                setToolbarTitle(toolbarTitle, true)
                val df = newInstance(mActivityHasTwoFragment, true)
                // transaction.add add the fragment as a layer without removing the list
                transaction.add(getFragmentContainer(null), df, Constants.DETAIL_FRAGMENT)
            }
            EFragments.ADD -> {
                setToolbarTitle(toolbarTitle, false)
                val ap = AddProperty.newInstance()
                transaction.add(getFragmentContainer(null), ap, Constants.ADD_FRAGMENT)
            }
            EFragments.SEARCH -> {
                setToolbarTitle(toolbarTitle, false)
                val se = SearchEngine.newInstance()
                transaction.add(getFragmentContainer(null), se, Constants.SEARCH_FRAGMENT)
            }
            EFragments.SIMULATOR -> {
                setToolbarTitle(toolbarTitle, true)
                val ls = LoanSimulator.newInstance()
                transaction.add(getFragmentContainer(null), ls, Constants.SIMULATOR_FRAGMENT)
            }
            EFragments.EDIT -> {
                setToolbarTitle(toolbarTitle, false)
                val ep = EditProperty.newInstance()
                transaction.add(getFragmentContainer(null), ep, Constants.EDIT_FRAGMENT)
            }
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.addToBackStack(toolbarTitle).commit()
    }

    private fun getFragmentContainer(fragment: EFragments?): Int {
        // if LIST && doubleFragment fragm
        return if (fragment == EFragments.LIST && mActivityHasTwoFragment) {
            R.id.main_activity_fragment_container
        } else if (fragment == null && mActivityHasTwoFragment) {
            R.id.main_activity_fragment_container_secondary
        } else {
            R.id.main_activity_fragment_container
        }
    }

    // Check if service Google Maps is available
    private val isMapsServiceOk: Boolean
        get() {
            val available =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)
            when {
                available == ConnectionResult.SUCCESS -> {
                    return true
                }
                GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                    val dialog = GoogleApiAvailability.getInstance()
                        .getErrorDialog(this@MainActivity, available, Constants.ERROR_DIALOG_REQUEST)
                    dialog?.show()
                }
                else -> {
                    return false
                }
            }
            return false
        }

    // for manage display of name of fragment like title in the Toolbar
    override fun onBackPressed() {
        super.onBackPressed()
        mEFragments = findFragmentItIs()
        when (mEFragments) {
            EFragments.LIST -> setToolbarTitle(resources.getString(R.string.app_name), false)
            EFragments.MAP -> setToolbarTitle(resources.getString(R.string.frg_map_title), false)
            EFragments.DETAIL -> {
                var propertyType = "Real Estate:"
                val d =
                    mFragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT) as DetailFragment?
                if (d != null) propertyType = d.propertyType
                setToolbarTitle(propertyType, true)
            }
        }
    }

    private fun findFragmentItIs(): EFragments {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.main_activity_fragment_container)
        return if (fragment is ListProperty) EFragments.LIST else if (fragment is MapFragment) EFragments.MAP else if (fragment is DetailFragment) EFragments.DETAIL else if (fragment is EditProperty) EFragments.EDIT else if (fragment is LoanSimulator) EFragments.SIMULATOR else if (fragment is SearchEngine) EFragments.SEARCH else EFragments.ADD
    }

    // enable/display the flesh of backUp button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Manage menu Toolbar for different fragments
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        val inflater = menuInflater
        if (mEFragments == EFragments.LIST || mEFragments == EFragments.MAP) {
            inflater.inflate(R.menu.main_menu, menu)
        } else if (mEFragments == EFragments.ADD || mEFragments == EFragments.EDIT || mEFragments == EFragments.SEARCH) {
            inflater.inflate(R.menu.cancel_save_menu, menu)
        } else if (mEFragments == EFragments.DETAIL) {
            inflater.inflate(R.menu.detail_menu, menu)
        } else {
            inflater.inflate(R.menu.simulator_menu, menu)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // switch method not allowed for R.id.*
        when (item.itemId) {
            R.id.mi_add -> {
                displayFragm(EFragments.ADD, resources.getString(R.string.frg_add_title))
            }
            R.id.mi_edit -> {
                displayFragm(EFragments.EDIT, resources.getString(R.string.frg_edit_title))
            }
            R.id.mi_search -> {
                displayFragm(EFragments.SEARCH, resources.getString(R.string.frg_search_title))
            }
            R.id.mi_cancel -> {
                onBackPressed()
            }
            R.id.mi_save -> {
                runCommand(0)
            }
            R.id.mi_loan_calculator -> {
                displayFragm(EFragments.SIMULATOR, resources.getString(R.string.frg_simulator_title))
            }
            R.id.mi_dollar_to_euro -> {
                runCommand(2)
            }
            R.id.mi_euro_to_dollar -> {
                runCommand(1)
            }
            R.id.mi_reset_row_query -> {
                runCommand(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun runCommand(commandCode: Int) {
        when (mEFragments) {
            EFragments.ADD -> {
                val aF =
                    mFragmentManager.findFragmentByTag(Constants.ADD_FRAGMENT) as AddProperty?
                aF?.checkFormValidityBeforeSave()
            }
            EFragments.EDIT -> {
                val eF =
                    mFragmentManager.findFragmentByTag(Constants.EDIT_FRAGMENT) as EditProperty?
                eF?.updateProperty()
            }
            EFragments.SEARCH -> {
                val sF =
                    mFragmentManager.findFragmentByTag(Constants.SEARCH_FRAGMENT) as SearchEngine?
                sF?.searchProperties()
            }
            EFragments.SIMULATOR -> {
                val lF =
                    mFragmentManager.findFragmentByTag(Constants.SIMULATOR_FRAGMENT) as LoanSimulator?
                lF?.handleCurrency(commandCode)
            }
            EFragments.DETAIL -> {
                val dF =
                    mFragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT) as DetailFragment?
                dF?.handleCurrency(commandCode)
                val list =
                    mFragmentManager.findFragmentByTag(Constants.LIST_FRAGMENT) as ListProperty?
                list?.resetRowQuery()
            }
            EFragments.LIST -> {
                val list =
                    mFragmentManager.findFragmentByTag(Constants.LIST_FRAGMENT) as ListProperty?
                list?.resetRowQuery()
            }
            EFragments.MAP -> {
                val map =
                    mFragmentManager.findFragmentByTag(Constants.MAP_FRAGMENT) as MapFragment?
                map?.resetRowQuery()
            }
        }
    }
}