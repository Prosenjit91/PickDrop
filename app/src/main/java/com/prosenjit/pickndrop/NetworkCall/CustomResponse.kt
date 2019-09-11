package com.prosenjit.pickndrop .NetworkCall

class CustomResponse<T> {

    /** Callback interface for delivering parsed responses.  */
    interface Listener<T> {
        /** Called when a response is received.  */
        fun onSuccessResponse(response: T)
        fun onError(response: T)
    }
}