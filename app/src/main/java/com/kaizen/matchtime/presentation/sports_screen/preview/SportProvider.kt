package com.kaizen.matchtime.presentation.sports_screen.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.util.UiText

class SportProvider : PreviewParameterProvider<List<SportUI>> {

    override val values: Sequence<List<SportUI>>
        get() = sequenceOf(
            listOf(
                SportUI(
                    id = "FOOT",
                    name = "SOCCER",
                    isExpanded = true,
                    showOnlyFavorites = false,
                    icon = Icons.Default.SportsSoccer,
                    events = listOf(
                        EventUI(
                            "22911144",
                            "FOOT",
                            "AEK",
                            "Olympiakos",
                            false,
                            UiText.DynamicString("01:22:00")
                        ),
                        EventUI(
                            "22925144",
                            "FOOT",
                            "SalfordCity",
                            "Bolton",
                            false,
                            UiText.DynamicString("03:25:00")
                        ),
                        EventUI(
                            "11925142",
                            "FOOT",
                            "Kozani FCU",
                            "Makedonikos",
                            false,
                            UiText.DynamicString("11:00:00")
                        ),
                        EventUI(
                            "56925140",
                            "FOOT",
                            "Kilmarnock FC",
                            "Livingston",
                            false,
                            UiText.DynamicString("00:12:22")
                        ),
                        EventUI(
                            "46925166",
                            "FOOT",
                            "Manchester City",
                            "Tottenham Hotspur",
                            false,
                            UiText.DynamicString("21:30:00")
                        )
                    )
                ),
                SportUI(
                    id = "BASK",
                    name = "BASKETBALL",
                    isExpanded = true,
                    showOnlyFavorites = false,
                    icon = Icons.Default.SportsBasketball,
                    events = listOf(
                        EventUI(
                            "66925166",
                            "BASK",
                            "Iraq",
                            "Palestine",
                            false,
                            UiText.DynamicString("21:30:00")
                        )
                    )
                )
            )
        )

}