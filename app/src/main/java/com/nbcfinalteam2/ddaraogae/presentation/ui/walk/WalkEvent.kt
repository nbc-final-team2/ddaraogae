package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import com.nbcfinalteam2.ddaraogae.R

sealed interface WalkEvent {
    data class Error(val strResId: Int): WalkEvent
    data object StartWalking: WalkEvent
    data object StopWalking: WalkEvent
}

enum class WalkError {
    NO_SELECTED_DOG {
        override fun getStrResId(): Int {
            return R.string.msg_no_selected_dog
        }
    },
    LOAD_STORE_FAIL {
        override fun getStrResId(): Int {
            return R.string.msg_load_store_fail
        }
    },
    LOAD_DOG_FAIL {
        override fun getStrResId(): Int {
            return R.string.msg_load_dog_fail
        }
    },
    NO_NETWORK_CONNECTION {
        override fun getStrResId(): Int {
            return R.string.msg_no_network_connection
        }
    },
    NO_LOCATION_CONNECTION {
        override fun getStrResId(): Int {
            return R.string.msg_no_gps_connection
        }
    }
    ;

    abstract fun getStrResId(): Int
}