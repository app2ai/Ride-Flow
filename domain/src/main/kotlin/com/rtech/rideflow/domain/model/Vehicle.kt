package com.rtech.rideflow.domain.model

/**
 * A vehicle registered to a [Driver].
 *
 * @property id unique vehicle id.
 * @property make manufacturer, e.g. `Maruti Suzuki`.
 * @property model model name, e.g. `Dzire`.
 * @property year model year, e.g. `2023`.
 * @property plateNo registration plate number, e.g. `KA01AB1234`.
 * @property type ride category this vehicle serves.
 */
data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val plateNo: String,
    val type: VehicleType
)
