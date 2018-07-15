package com.ae.apps.messagecounter.data.models

/**
 * Model for the data to be used in the fragment
 */
data class SentCountDetails(val cycleLimit:Int,
                            val sentToday:Int,
                            val sentCycle:Int,
                            val sentInWeek:Int,
                            val startYearCount:Int,
                            val sentLastCycle:Int
                            )