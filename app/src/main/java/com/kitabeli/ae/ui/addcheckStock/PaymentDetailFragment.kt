package com.kitabeli.ae.ui.addcheckStock

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.databinding.FragmentPaymentDetailBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.onboarding.StockOpOnBoardingViewModel
import com.kitabeli.ae.utils.ext.copyToClipboard
import com.kitabeli.ae.utils.ext.openWhatsAppSupport
import com.kitabeli.ae.utils.ext.toStandardRupiah
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentDetailFragment : BaseFragment<StockOpOnBoardingViewModel>() {
    private var _binding: FragmentPaymentDetailBinding? = null

    private val mViewModel: StockOpOnBoardingViewModel by viewModels()
    private val binding get() = _binding!!

    var stockOpId = ""
    var stockOpDate = ""
    var amount = 0L
    var incentive = 0L
    var bankName = ""
    var vaNumber = ""
    var expiredAt = 0L
    var billCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockOpId = arguments?.getString("STOCKOP_ID").orEmpty()
        stockOpDate = arguments?.getString("STOCKOP_DATE").orEmpty()
        amount = (arguments?.getString("AMOUNT") ?: "0").replace(".", "").toLong()
        incentive = (arguments?.getString("INCENTIVE") ?: "0").replace(".", "").toLong()
        bankName = arguments?.getString("BANK_NAME").orEmpty()
        vaNumber = arguments?.getString("VA_NUMBER").orEmpty()
        expiredAt = arguments?.getLong("EXPIRED_AT") ?: 0L
        billCode = arguments?.getString("BILL_CODE").orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPaymentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): StockOpOnBoardingViewModel {
        return mViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvInvoiceId.text = "ID : $stockOpId"
            tvInvoiceDateRange.text = stockOpDate
            tvSaleAmount.text = (amount + incentive).toStandardRupiah()
            tvCommissionAmount.text = incentive.toStandardRupiah()
            tvInvoiceAmountValue.text = amount.toStandardRupiah()
            tvVaNumber.text = vaNumber
            tvBankName.text = "VA Bank ${bankName.uppercase()}"

            tvBillerCode.text = billCode

            if (billCode.isEmpty()) {
                clBillerCode.showGone(false)
                viewPaymentSeparator2.showGone(false)
            }

            btnHelp.setOnClickListener {
                activity?.openWhatsAppSupport()
            }
            tvCopy.setOnClickListener {
                requireContext().copyToClipboard(vaNumber)
            }
            tvCopyBill.setOnClickListener {
                requireContext().copyToClipboard(billCode)
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            startCountdown(expiredAt)
        }
    }

    private fun startCountdown(paymentExpireTime: Long) = with(binding) {
        var kickStartTimer: CountDownTimer?
        val futureMillis = (paymentExpireTime - System.currentTimeMillis() / 1000) * 1000
        kickStartTimer = object : CountDownTimer(futureMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24
                val hours: String
                val mins: String
                val sec: String

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                hours = if (elapsedHours < 10) {
                    "0${elapsedHours}"
                } else {
                    "$elapsedHours"
                }

                mins = if (elapsedMinutes < 10) {
                    "0${elapsedMinutes}"
                } else {
                    "$elapsedMinutes"
                }

                sec = if (elapsedSeconds < 10) {
                    "0${elapsedSeconds}"
                } else {
                    "$elapsedSeconds"
                }

                tvTimer.text = "$mins:$sec"
            }

            override fun onFinish() {
            }

        }
        kickStartTimer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
