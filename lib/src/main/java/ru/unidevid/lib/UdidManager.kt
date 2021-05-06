package ru.unidevid.lib

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Base64
import ru.unidevid.lib.udid.OpenUuidManager
import java.util.*

class UdidManager private constructor(context: Context) {

    init {
        OpenUuidManager.sync(context = context)
    }

    companion object {
        private lateinit var instance: UdidManager

        fun init(context: Context): UdidManager {
            synchronized(UdidManager::class) {
                if (!Companion::instance.isInitialized) {
                    instance = UdidManager(context = context)
                }
            }
            return instance
        }

        @SuppressLint("ObsoleteSdkInt")
        fun getUUID(): String {

            if (!Companion::instance.isInitialized) {
                throw Exception("UdidManager instance has not been initialised")
            }

            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val oldUUID = UUID.nameUUIDFromBytes(
                    OpenUuidManager.getOpenUDID().toByteArray()
                ).toString()
//                Log.e("oldUUID", oldUUID)
                oldUUID
            } else {
                val newUUID = UUID.nameUUIDFromBytes(
                    (Base64.encodeToString(
                        DeviceIDUtil.getUniqueID().toByteArray(), 0
                    )).toByteArray()
                ).toString()
//                Log.e("newUUID", newUUID)
                newUUID
            }
        }
    }
}