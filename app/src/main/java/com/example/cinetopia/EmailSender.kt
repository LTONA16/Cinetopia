package com.example.cinetopia

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender {

    companion object {
        // Configuración del servidor SMTP (Gmail en este ejemplo)
        private const val SMTP_HOST = "smtp.gmail.com"
        private const val SMTP_PORT = "587"

        // TODO: Reemplaza estos valores con tu email y contraseña de aplicación
        private const val EMAIL_FROM = "csibraintive@gmail.com"
        private const val EMAIL_PASSWORD = "ndov cmjb ldmk wzqd"

        suspend fun enviarEmailBienvenida(
            emailDestino: String,
            nombreUsuario: String,
            password: String
        ): Boolean = withContext(Dispatchers.IO) {
            try {
                // Configurar propiedades del servidor SMTP
                val props = Properties().apply {
                    put("mail.smtp.host", SMTP_HOST)
                    put("mail.smtp.port", SMTP_PORT)
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                }

                // Crear sesión con autenticación
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD)
                    }
                })

                // Crear el mensaje
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL_FROM))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino))
                    subject = "¡Bienvenido a Cinetopia! 🎬"

                    // Contenido HTML del email
                    setContent(getHtmlContent(nombreUsuario, password), "text/html; charset=utf-8")
                }

                // Enviar el mensaje
                Transport.send(message)

                Log.d("EmailSender", "Email enviado exitosamente a: $emailDestino")
                true

            } catch (e: MessagingException) {
                Log.e("EmailSender", "Error al enviar email: ${e.message}")
                e.printStackTrace()
                false
            } catch (e: Exception) {
                Log.e("EmailSender", "Error inesperado: ${e.message}")
                e.printStackTrace()
                false
            }
        }

        /**
         * Genera el contenido HTML del email de bienvenida
         */
        private fun getHtmlContent(nombreUsuario: String, password: String): String {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f4f4f4;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                            border-radius: 10px 10px 0 0;
                        }
                        .content {
                            background-color: white;
                            padding: 30px;
                            border-radius: 0 0 10px 10px;
                        }
                        .credentials {
                            background-color: #f8f9fa;
                            border-left: 4px solid #667eea;
                            padding: 15px;
                            margin: 20px 0;
                        }
                        .credentials p {
                            margin: 10px 0;
                        }
                        .credentials strong {
                            color: #667eea;
                        }
                        .warning {
                            background-color: #fff3cd;
                            border-left: 4px solid #ffc107;
                            padding: 15px;
                            margin: 20px 0;
                            font-size: 14px;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            color: #666;
                            font-size: 12px;
                        }
                        .btn {
                            display: inline-block;
                            padding: 12px 30px;
                            background-color: #667eea;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎬 ¡Bienvenido a Cinetopia!</h1>
                        </div>
                        <div class="content">
                            <h2>¡Hola!</h2>
                            <p>Nos complace darte la bienvenida a Cinetopia, tu destino favorito para disfrutar del mejor cine.</p>
                            
                            <p>Tu cuenta ha sido creada exitosamente. A continuación, encontrarás tus credenciales de acceso:</p>
                            
                            <div class="credentials">
                                <p><strong>👤 Usuario:</strong> $nombreUsuario</p>
                                <p><strong>🔑 Contraseña:</strong> $password</p>
                            </div>
                            
                            <div class="warning">
                                <strong>⚠️ Importante:</strong> Por tu seguridad, te recomendamos guardar esta información en un lugar seguro y no compartirla con nadie.
                            </div>
                            
                            <p>Ya puedes comenzar a disfrutar de:</p>
                            <ul>
                                <li>🎟️ Compra de boletos en línea</li>
                                <li>🍿 Pedidos de dulcería</li>
                                <li>🎁 Programa de recompensas</li>
                                <li>📱 Y mucho más...</li>
                            </ul>
                            
                            <center>
                                <a href="#" class="btn">Comenzar ahora</a>
                            </center>
                            
                            <p style="margin-top: 30px;">Si tienes alguna pregunta, no dudes en contactarnos.</p>
                            
                            <p>¡Disfruta del espectáculo! 🎉</p>
                        </div>
                        <div class="footer">
                            <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                            <p>© 2024 Cinetopia. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()
        }
    }
}