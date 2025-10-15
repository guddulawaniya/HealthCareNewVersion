package com.careavatar.core_network.data

import com.careavatar.core_ui.R
import com.careavatar.core_model.OnboardingPage

// Accept drawable IDs from the app module
object OnboardingData {
    fun getPages(): List<OnboardingPage> = listOf(

        OnboardingPage(R.string.complete_wellness, R.string.track_yoga_diet_fitness_and_health_all_in_one_place, R.drawable.onboardingimage1),
        OnboardingPage(R.string.special_care_for_special_needs, R.string.support_for_alzheimer_s_and_autism_with_personalized_caregiver_tools, R.drawable.onboardingimage2),
        OnboardingPage(R.string.emergency_sos, R.string.alert_your_loved_ones_instantly_in_case_of_emergencies, R.drawable.onboardingimage3),
        OnboardingPage(R.string.connect_with_the_community, R.string.meet_people_nearby_or_talk_one_on_one_you_re_not_alone, R.drawable.onboardingimage4)
    )
}
