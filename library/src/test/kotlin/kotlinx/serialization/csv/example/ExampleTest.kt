package kotlinx.serialization.csv.example

import kotlinx.serialization.csv.Csv
import kotlinx.serialization.csv.CsvConfiguration
import kotlinx.serialization.csv.example.Feature.*
import kotlinx.serialization.csv.example.Tire.Axis.FRONT
import kotlinx.serialization.csv.example.Tire.Axis.REAR
import kotlinx.serialization.csv.example.Tire.Side.LEFT
import kotlinx.serialization.csv.example.Tire.Side.RIGHT
import kotlinx.serialization.list
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.test.assertStringFormAndRestored
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

/**
 * Test complex [LocationRecord].
 */
class ExampleTest {

    // Persons
    private val jonSmith = Person(12, "Jon", "Smith", null)
    private val janeDoe = Person(42, "Jane", "Doe", 1581602631744)

    // Vehicles
    private val tesla = Vehicle(UUID.fromString("f9682dcb-30f7-4e88-915e-60e3b2758da7"), VehicleType.CAR, "Tesla")
    private val porsche = Vehicle(UUID.fromString("5e1afd88-97a2-4373-a83c-44a49c552abd"), VehicleType.CAR, "Porsche")
    private val harley = Vehicle(UUID.fromString("c038c27b-a3fd-4e35-b6ac-ab06d747e16c"), VehicleType.BIKE, "Harley")

    @Test
    fun testLocationRecords() = assertStringFormAndRestored(
        """|id,date,position.latitude,position.longitude,driver.id,driver.foreName,driver.lastName,driver.birthday,vehicle.uuid,vehicle.type,vehicle.brand,vehicleData.speed,vehicleData.consumption,vehicleData.consumption.Unknown,vehicleData.consumption.Combustion.consumptionLiterPer100Km,vehicleData.consumption.Electric.consumptionKWhPer100Km
           |0,2020-02-01T13:33:00,0.0,0.0,12,Jon,Smith,,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,,Unknown,Unknown,,
           |1,2020-02-01T13:37:00,0.1,0.1,12,Jon,Smith,,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,27.7778,Electric,,,18.1
           |9000,2020-02-05T07:59:00,48.137154,11.576124,42,Jane,Doe,1581602631744,c038c27b-a3fd-4e35-b6ac-ab06d747e16c,MOTORBIKE,Harley,20.0,Combustion,,7.9,
        """.trimMargin().replace("\n", "\r\n"),
        listOf(
            LocationRecord(
                0, LocalDateTime.of(2020, 2, 1, 13, 33),
                Position(0.0, 0.0),
                jonSmith, tesla,
                VehicleData(null, Consumption.Unknown)
            ),
            LocationRecord(
                1, LocalDateTime.of(2020, 2, 1, 13, 37),
                Position(0.1, 0.1),
                jonSmith, tesla,
                VehicleData(27.7778, Consumption.Electric(18.1))
            ),
            LocationRecord(
                9_000, LocalDateTime.of(2020, 2, 5, 7, 59),
                Position(48.137154, 11.576124),
                janeDoe, harley,
                VehicleData(20.0, Consumption.Combustion(7.9))
            )
        ),
        LocationRecord.serializer().list,
        Csv(
            CsvConfiguration.rfc4180.copy(
                hasHeaderRecord = true
            )
        )
    )

    @Test
    fun testVehiclePartRecords() = assertStringFormAndRestored(
        """|101,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,Tire,FRONT,LEFT,245,35,21,0.25
           |102,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,Tire,FRONT,RIGHT,245,35,21,0.21
           |103,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,Tire,REAR,LEFT,265,35,21,0.35
           |104,f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,Tire,REAR,RIGHT,265,35,21,0.32
           |201,5e1afd88-97a2-4373-a83c-44a49c552abd,CAR,Porsche,Oil,20,50,0.2
           |202,5e1afd88-97a2-4373-a83c-44a49c552abd,CAR,Porsche,Tire,FRONT,LEFT,265,35,20,0.2
           |203,5e1afd88-97a2-4373-a83c-44a49c552abd,CAR,Porsche,Tire,FRONT,RIGHT,265,35,20,0.2
           |204,5e1afd88-97a2-4373-a83c-44a49c552abd,CAR,Porsche,Tire,REAR,LEFT,265,35,20,0.2
           |205,5e1afd88-97a2-4373-a83c-44a49c552abd,CAR,Porsche,Tire,REAR,RIGHT,265,35,20,0.2
        """.trimMargin().replace("\n", "\r\n"),
        listOf(
            VehiclePartRecord(101, tesla, Tire(FRONT, LEFT, 245, 35, 21), 0.25),
            VehiclePartRecord(102, tesla, Tire(FRONT, RIGHT, 245, 35, 21), 0.21),
            VehiclePartRecord(103, tesla, Tire(REAR, LEFT, 265, 35, 21), 0.35),
            VehiclePartRecord(104, tesla, Tire(REAR, RIGHT, 265, 35, 21), 0.32),
            VehiclePartRecord(201, porsche, Oil(20, 50), 0.2),
            VehiclePartRecord(202, porsche, Tire(FRONT, LEFT, 265, 35, 20), 0.2),
            VehiclePartRecord(203, porsche, Tire(FRONT, RIGHT, 265, 35, 20), 0.2),
            VehiclePartRecord(204, porsche, Tire(REAR, LEFT, 265, 35, 20), 0.2),
            VehiclePartRecord(205, porsche, Tire(REAR, RIGHT, 265, 35, 20), 0.2)
        ),
        VehiclePartRecord.serializer().list,
        Csv(
            CsvConfiguration.rfc4180,
            SerializersModule {
                polymorphic(Part::class) {
                    Tire::class with Tire.serializer()
                    Oil::class with Oil.serializer()
                }
            }
        )
    )

    @Test
    fun testVehicleFeaturesRecords() = assertStringFormAndRestored(
        """|c038c27b-a3fd-4e35-b6ac-ab06d747e16c,MOTORBIKE,Harley,,
           |c038c27b-a3fd-4e35-b6ac-ab06d747e16c,MOTORBIKE,Harley,0,0
           |f9682dcb-30f7-4e88-915e-60e3b2758da7,CAR,Tesla,5,ELECTRIC,AUTOMATIC,HEATED_SEATS,NAVIGATION_SYSTEM,XENON,2,ELECTRIC,0,XENON,1
        """.trimMargin().replace("\n", "\r\n"),
        listOf(
            VehicleFeaturesRecord(harley, null, null),
            VehicleFeaturesRecord(harley, emptyList(), emptyMap()),
            VehicleFeaturesRecord(
                tesla,
                listOf(ELECTRIC, AUTOMATIC, HEATED_SEATS, NAVIGATION_SYSTEM, XENON),
                mapOf(ELECTRIC to 0, XENON to 1)
            )
        ),
        VehicleFeaturesRecord.serializer().list,
        Csv(CsvConfiguration.rfc4180)
    )
}
