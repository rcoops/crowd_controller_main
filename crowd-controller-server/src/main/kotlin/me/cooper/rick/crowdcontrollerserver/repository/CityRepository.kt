package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.City
import org.springframework.data.jpa.repository.JpaRepository

interface CityRepository: JpaRepository<City, Long>