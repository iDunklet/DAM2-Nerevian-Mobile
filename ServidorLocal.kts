import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.Executors

val puerto = 8090
// La carpeta se creará en la raíz de tu proyecto
val carpetaGuardado = File(System.getProperty("user.dir"), "DniAlmacen_Local")

if (!carpetaGuardado.exists()) {
    carpetaGuardado.mkdirs()
}

println("=========================================")
println("[SERVIDOR LOCAL SCRIPT] Iniciado.")
println("[PUERTO] $puerto")
println("[CARPETA] ${carpetaGuardado.absolutePath}")
println("=========================================")

val threadPool = Executors.newCachedThreadPool()

try {
    val serverSocket = ServerSocket(puerto)

    while (true) {
        println("Esperando conexiones de clientes...")
        val clientSocket = serverSocket.accept()
        println("¡Cliente conectado desde: ${clientSocket.inetAddress.hostAddress}!")

        // Metemos al cliente en su propio hilo
        threadPool.execute {
            try {
                val entrada = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val salida = PrintWriter(clientSocket.getOutputStream(), true)

                // 1. Leemos lo primero que nos envía la app
                val peticion = entrada.readLine()

                // 2. Comprobamos qué quiere hacer la app
                if (peticion == "DOWNLOAD") {
                    // MODO DESCARGA: Buscamos el último archivo guardado
                    val archivos = carpetaGuardado.listFiles()

                    if (archivos != null && archivos.isNotEmpty()) {
                        // Ordenamos para coger el modificado más recientemente
                        val ultimoArchivo = archivos.maxByOrNull { it.lastModified() }
                        val datosBase64 = ultimoArchivo?.readText()

                        // Le enviamos el texto encriptado de vuelta a la app
                        salida.println(datosBase64)
                        println("-> Archivo enviado al cliente: ${ultimoArchivo?.name}")
                    } else {
                        // Si la carpeta está vacía, no mandamos nada
                        salida.println("")
                        println("-> Petición de descarga fallida: La carpeta está vacía.")
                    }

                } else if (!peticion.isNullOrEmpty()) {
                    // MODO SUBIDA: Si no es "DOWNLOAD", asumimos que es el texto Base64 encriptado
                    val archivo = File(carpetaGuardado, "dni_encriptado_${System.currentTimeMillis()}.txt")
                    archivo.writeText(peticion)
                    println("-> Archivo guardado con éxito en: ${archivo.absolutePath}")
                    salida.println("OK")

                } else {
                    println("-> Error: Datos vacíos.")
                    salida.println("ERROR")
                }
            } catch (e: Exception) {
                println("-> Error con cliente: ${e.message}")
            } finally {
                if (!clientSocket.isClosed) clientSocket.close()
                println("Conexión cerrada con este cliente.\n")
            }
        }
    }
} catch (e: Exception) {
    println("Error al iniciar el servidor: ${e.message}")
}