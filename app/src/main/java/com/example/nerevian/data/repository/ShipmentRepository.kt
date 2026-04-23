/*package com.example.nerevian.data.repository

import com.example.nerevian.core.model.business.request.Request
import com.example.nerevian.core.model.business.request.StatusRequest
import com.example.nerevian.core.model.incoterms.IncotermType
import com.example.nerevian.core.model.logistics.CargoType
import com.example.nerevian.core.model.logistics.Carrier
import com.example.nerevian.core.model.logistics.City
import com.example.nerevian.core.model.logistics.ContainerType
import com.example.nerevian.core.model.logistics.Country
import com.example.nerevian.core.model.logistics.FlowType
import com.example.nerevian.core.model.logistics.Port
import com.example.nerevian.core.model.logistics.TransportType
import com.example.nerevian.core.model.user.Client
import com.example.nerevian.core.model.user.DNI
import com.example.nerevian.core.model.user.Role
import com.example.nerevian.core.model.user.User
import com.example.nerevian.core.model.utils.ValidationType
import java.time.LocalDate

class ShipmentRepository {
  // Simulación de la API (Aquí irá Retrofit en el futuro)
   // private val apiService = RetrofitInstance.api

   /**
    * Obtiene las solicitudes (Requests) del servidor.
    * Por ahora devuelve datos "mokeados" (ficticios) para que puedas
    * diseñar tus pantallas de Agent y Client.
    */



       suspend fun getActiveRequest(): List<Request> {
           kotlinx.coroutines.delay(1000)

           // 1. Capa geográfica
           val mockCountry = Country(1, "España")
           val mockCity = City(1, "Barcelona", mockCountry)

           // 2. Capa de Usuario y Cliente
           val mockRoleClient = Role(1, "Client")
           val mockUser =
               User(1, "Adrián", "adrian@example.com", "600000000", "1234", mockRoleClient)
           val mockDni = DNI(1, "12345678X")

           val mockClient = Client(
               id = 1,
               user = mockUser,
               dniId = mockDni,
               registerDate = LocalDate.now()
           )

           // 3. Capa Logística
           val mockPort = Port(1, "Port de Barcelona", mockCity)
           val mockCarrier = Carrier(1, "Maersk Spain", mockCity)
           val mockOperator = User(
               2,
               "Comercial Juan",
               "juan@logistics.com",
               "611222333",
               "pwd",
               Role(2, "Agent")
           )

           // 4. La Request (Usando tus nombres exactos: coments y Volume)
           val mockRequest = Request(
               id = 101,
               transportType = TransportType(1, "Maritime"),
               flowType = FlowType(1, "Export"),
               cargoType = CargoType(1, "General"),
               incotermType = IncotermType(1, "FOB"),
               client = mockClient,
               comments = "Carga de prueba para el desarrollo", // Nombre exacto de tu clase
               carrier = mockCarrier,
               rawWeight = 1500.0f,
               volume = 45.5f, // Nombre exacto de tu clase
               validationType = ValidationType(1, "Automatic"),
               originPort = mockPort,
               destinationPort = Port(
                   2,
                   "Port of Shanghai",
                   City(2, "Shanghai", Country(2, "China"))
               ),
               status = StatusRequest(1, "Pending"),
               operator = mockOperator,
               creationDate = LocalDate.now(),
               containerType = ContainerType(1, 1) // id: 1, type: 1 (según tu data class)
           )

           return listOf(mockRequest)


   }
}*/