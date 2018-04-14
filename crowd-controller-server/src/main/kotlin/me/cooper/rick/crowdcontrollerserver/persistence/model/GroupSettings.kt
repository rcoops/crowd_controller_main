package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupSettingsDto
import javax.persistence.*

@Entity
internal data class GroupSettings(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        private val id: Long = 0,

        @OneToOne(mappedBy = "settings", fetch = FetchType.LAZY, optional = false)
        val group: Group? = null,

        val isClustering: Boolean = false,
        val minClusterRadius: Double = 100.0,
        val minNodePercentage: Double = 0.5,
        val maxLifeInHours: Int = 12) {

    fun toDto(): GroupSettingsDto {
        return GroupSettingsDto(isClustering, minClusterRadius, minNodePercentage)
    }

    fun fromDto(dto: GroupSettingsDto?): GroupSettings {
        return if (dto == null) this else copy(
                isClustering = dto.clustering,
                minClusterRadius = dto.minClusterRadius,
                minNodePercentage = dto.minNodePercentage
        )
    }

}
