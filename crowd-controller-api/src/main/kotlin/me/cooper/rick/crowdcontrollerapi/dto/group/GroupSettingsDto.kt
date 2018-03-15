package me.cooper.rick.crowdcontrollerapi.dto.group

data class GroupSettingsDto(val clustering: Boolean = false,
                            val minClusterRadius: Double = 100.0,
                            val minNodePercentage: Double = 0.5)
