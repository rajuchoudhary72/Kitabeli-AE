package com.kitabeli.ae.ui.login.midtrans

import android.content.Context
import android.util.Log
import com.kitabeli.ae.BuildConfig
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class Midtrans {

    companion object {
        fun checkout(
            context: Context,
            snapToken: String?,
            onResult: ((result: TransactionResult) -> Unit)
        ) {
            val uiKitCustomSetting: UIKitCustomSetting =
                MidtransSDK.getInstance().uiKitCustomSetting
            uiKitCustomSetting.isSkipCustomerDetailsPages = true


            SdkUIFlowBuilder.init()
                .setContext(context)
                .setClientKey(BuildConfig.MIDTRANS_CLIENT_KEY)
                .setMerchantBaseUrl(BuildConfig.MIDTRANS_BASE_URL)
                .setUIkitCustomSetting(uiKitCustomSetting)
                .setTransactionFinishedCallback { transactionResult: TransactionResult ->
                    onResult(
                        transactionResult
                    )
                }
                .enableLog(true)
                .buildSDK()

            MidtransSDK.getInstance().startPaymentUiFlow(context, snapToken)

        }
    }


}