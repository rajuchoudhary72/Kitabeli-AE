package com.kitabeli.ae.ui.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.group
import com.kitabeli.ae.R
import com.kitabeli.ae.SurveyChipBindingModel_
import com.kitabeli.ae.databinding.FragmentKioskSurveyBinding
import com.kitabeli.ae.headline
import com.kitabeli.ae.surveyCheckboxView
import com.kitabeli.ae.surveyFreeTextInput
import com.kitabeli.ae.surveyPhotoPicker
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.utils.flexboxView
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KioskSurveyFragment : BaseFragment<KioskSurveyViewModel>() {

    private var _binding: FragmentKioskSurveyBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: KioskSurveyViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentKioskSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): KioskSurveyViewModel {
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(
                    R.dimen.dimen_thirteen
                )
            )
        )
        binding.recyclerView.addItemDecoration(
            LinearDividerDecoration.create(
                size = resources.getDimensionPixelSize(
                    com.midtrans.sdk.uikit.R.dimen.five_dp
                ),
                color = ContextCompat.getColor(requireContext(), R.color.divider)
            )
        )

        binding.recyclerView.withModels {
         /*   surveyRadioBtnView {
                id("1")
                question("Title (Checklist type ‘yes’ or ‘no’)")
            }
            surveyRadioBtnView {
                id("2")
                question("Extra long title that need 2 lines (Checklist type ‘yes’ or ‘no’)")
            }*/

            group {
                id(3040)
                layout(R.layout.vertical_group)
                headline {
                    id(4940)
                    text("Extra long title that need 2 lines (Checklist type ‘yes’ or ‘no’)")
                }
                for (i in 900..901) {
                    surveyCheckboxView {
                        id(i)
                    }
                }
            }

            group {
                id(3000)
                layout(R.layout.vertical_group)
                headline {
                    id(490)
                    text("Title (Checklist type ‘yes’ or ‘no’)")
                }
                for (i in 90..91) {
                    surveyCheckboxView {
                        id(i)
                    }
                }
            }

            group {
                id(3)
                layout(R.layout.vertical_group)
                headline {
                    id(4)
                    text("Title (Checklist type)")
                }
                for (i in 5..8) {
                    surveyCheckboxView {
                        id(i)
                    }
                }
            }

            group {
                id(3)
                layout(R.layout.vertical_group)
                headline {
                    id(4)
                    text("Title (Multiple choice type)")
                }
                flexboxView {
                    id(3443)
                    models(
                        (333..338).map {
                            SurveyChipBindingModel_()
                                .text("text $it")
                                .id(it)
                        }
                    )
                }
            }


            group {
                id(9)
                layout(R.layout.vertical_group)
                headline {
                    id(10)
                    text("Title (Checklist type)")
                }
                for (i in 50..56) {
                    surveyCheckboxView {
                        id(i)
                    }
                }
            }

            surveyFreeTextInput {
                id(101)
                title("Title (Free text type)")
            }

            surveyPhotoPicker {
                id(123)
                title("Title (This is for upload image)")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}