package ru.unidevid.lib.udid

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ResolveInfo
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

class OpenUuidManager private constructor(context: Context) : ServiceConnection {
    private val mContext //Application context
            : Context
    private var mMatchingIntents //List of available OpenUDID Intents
            : MutableList<ResolveInfo>? = null
    private val mReceivedOpenUDIDs //Map of OpenUDIDs found so far
            : MutableMap<String?, Int?>
    private val mPreferences //Preferences to store the OpenUDID
            : SharedPreferences
    private val mRandom: Random
    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        //Get the OpenUDID from the remote service
        try {
            //Send a random number to the service
            val data = Parcel.obtain()
            data.writeInt(mRandom.nextInt())
            val reply = Parcel.obtain()
            service.transact(1, Parcel.obtain(), reply, 0)
            if (data.readInt() == reply.readInt()) //Check if the service returns us this number
            {
                val openUDID = reply.readString()
                if (openUDID != null) { //if valid OpenUDID, save it
                    Log.e(TAG, "Received $openUDID")
                    if (mReceivedOpenUDIDs.containsKey(openUDID)) mReceivedOpenUDIDs[openUDID] = mReceivedOpenUDIDs[openUDID]!! + 1 else mReceivedOpenUDIDs[openUDID] = 1
                }
            }
            data.recycle()
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException: " + e.message)
        }
        mContext.unbindService(this)
        startService() //Try the next one
    }

    override fun onServiceDisconnected(className: ComponentName) {}
    private fun storeOpenUDID() {
        val e = mPreferences.edit()
        e.putString(PREF_KEY, OpenUDID)
        e.apply()
    }

    /*
     * Generate a new OpenUDID
     */
    private fun generateOpenUDID() {
        Log.e(TAG, "Generating openUDID")
//        //Try to get the ANDROID_ID
//        OpenUDID = Secure.getString(mContext.contentResolver, Secure.ANDROID_ID)
//        if (OpenUDID == null || OpenUDID == "9774d56d682e549c" || OpenUDID!!.length < 15) {
//            //if ANDROID_ID is null, or it's equals to the GalaxyTab generic ANDROID_ID or bad, generates a new one
        val random = SecureRandom()
        OpenUDID = BigInteger(64, random).toString(16)
//        }
    }

    /*
     * Start the oldest service
     */
    private fun startService() {
        if (mMatchingIntents?.isNotEmpty() == true) { //There are some Intents untested
            Log.e(TAG, "Trying service " + mMatchingIntents!![0].loadLabel(mContext.packageManager))
            val servInfo = mMatchingIntents?.get(0)?.serviceInfo
            val i = Intent()
            i.component = servInfo?.applicationInfo?.packageName?.let { ComponentName(it, servInfo.name) }
            mMatchingIntents?.removeAt(0)
            try {    // try added by Lionscribe
                mContext.bindService(i, this, Context.BIND_AUTO_CREATE)
            } catch (e: SecurityException) {
                startService() // ignore this one, and start next one
            }
        } else { //No more service to test
            calcOpenUDID() //Choose the most frequent
            if (OpenUDID == null) //No OpenUDID was chosen, generate one
                generateOpenUDID()
            Log.e(TAG, "OpenUDID: $OpenUDID")
            storeOpenUDID() //Store it locally
            isInitialized = true
        }
    }

    private fun calcOpenUDID(){
        if (mReceivedOpenUDIDs.isNotEmpty()) {
            val sortedOpenUDIDS: TreeMap<String?, Int?> = TreeMap(ValueComparator())
            sortedOpenUDIDS.putAll(mReceivedOpenUDIDs)
            OpenUDID = sortedOpenUDIDS.firstKey()
        }
    }



    /*
     * Used to sort the OpenUDIDs collected by occurrence
     */
    private inner class ValueComparator : Comparator<Any?> {
        override fun compare(a: Any?, b: Any?): Int {
            return when {
                mReceivedOpenUDIDs[a]!! < mReceivedOpenUDIDs[b]!! -> {
                    1
                }
                mReceivedOpenUDIDs[a] === mReceivedOpenUDIDs[b] -> {
                    0
                }
                else -> {
                    -1
                }
            }
        }
    }

    companion object {
        const val PREF_KEY = "openudid"
        const val PREFS_NAME = "openudid_prefs"
        const val TAG = "OpenUDID"
        private var OpenUDID: String? = null

        /**
         * The Method to call to get OpenUDID
         * @return the OpenUDID
         */
        var isInitialized = false
            private set

        /**
         * The Method to call to get OpenUDID
         * @return the OpenUDID
         */
        fun getOpenUDID(): String? {
            if (!isInitialized) Log.e("OpenUDID", "Initialisation isn't done")
            return OpenUDID
        }

        /**
         * The Method the call at the init of your app
         * @param context    you current context
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun sync(context: Context) {
            //Initialise the Manager
            val manager = OpenUuidManager(context)

            //Try to get the openudid from local preferences
            OpenUDID = manager.mPreferences.getString(PREF_KEY, null)
            if (OpenUDID == null) //Not found
            {
                //Get the list of all OpenUDID services available (including itself)
                manager.mMatchingIntents = context.packageManager.queryIntentServices(Intent("org.OpenUDID.GETUDID"), 0)
                Log.e(TAG, manager.mMatchingIntents?.size.toString() + " services matches OpenUDID")
                if (manager.mMatchingIntents != null) //Start services one by one
                    manager.startService()
            } else { //Got it, you can now call getOpenUDID()
                Log.e(TAG, "OpenUDID: $OpenUDID")
                isInitialized = true
            }
        }
    }

    init {
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        mContext = context
        mRandom = Random()
        mReceivedOpenUDIDs = HashMap()
    }
}