# 🎬 Cinetopia Android App

**Cinetopia** es una aplicación nativa de Android desarrollada en Kotlin que ofrece una experiencia completa para los amantes del cine. Permite a los usuarios consultar la cartelera, comprar boletos, ordenar productos de dulcería y gestionar sus puntos de lealtad, todo integrado con una billetera digital moderna.

## ✨ Características Principales

* **🎟️ Compra de Boletos:** Selección de películas, horarios y elección de asientos en tiempo real.
* **🍿 Dulcería Móvil:** Catálogo completo de alimentos y bebidas para pre-ordenar o recibir en la sala.
* **💎 Sistema de Recompensas:** Los usuarios acumulan puntos por cada compra que pueden canjear por productos o descuentos.
* **📲 Integración con Billetera (Wallet):**
    * **Google Wallet:** Agrega tus boletos y tarjetas de lealtad directamente a tu cuenta de Google.
    * **Apple Wallet:** Generación de pases (.pkpass) para compatibilidad con dispositivos iOS (si aplica en un entorno híbrido o mediante backend).
* **🔐 Autenticación Segura:** Inicio de sesión y registro de usuarios gestionado por Firebase Authentication.

## 🛠️ Tech Stack

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **IDE:** Android Studio
* **Backend & Servicios (Firebase):**
    * **Firebase Authentication:** Gestión de usuarios.
    * **Firestore / Realtime Database:** Almacenamiento de películas, órdenes y perfiles.
    * **Firebase Cloud Messaging (FCM):** Notificaciones push para confirmación de compras.
* **Pagos & Integraciones:**
    * [Google Wallet API](https://developers.google.com/wallet)
    * PassKit (para soporte de pases tipo Apple).
* **Arquitectura:** MVVM (Model-View-ViewModel).

## 📸 Capturas de Pantalla

| Home / Cartelera | Selección de Asientos | Dulcería | Billetera / Wallet |
|:---:|:---:|:---:|:---:|
| ![Home](url_a_tu_imagen_1.png) | ![Asientos](url_a_tu_imagen_2.png) | ![Dulceria](url_a_tu_imagen_3.png) | ![Wallet](url_a_tu_imagen_4.png) |

*(Reemplaza `url_a_tu_imagen_X.png` con las rutas de tus capturas de pantalla)*

## 🚀 Instalación y Configuración

Sigue estos pasos para ejecutar el proyecto en tu entorno local:

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/LTONA16/Cinetopia.git
    ```
2.  **Abrir en Android Studio:**
    Abre el proyecto y espera a que Gradle sincronice las dependencias.

3.  **Configuración de Firebase:**
    * Crea un proyecto en la [Consola de Firebase](https://console.firebase.google.com/).
    * Registra la app con tu `package name`.
    * Descarga el archivo `google-services.json` y colócalo en la carpeta `app/` del proyecto.

4.  **Configuración de Google Wallet:**
    * Asegúrate de tener acceso a la Google Pay & Wallet Console.
    * Configura las credenciales en tu archivo `local.properties` (no incluido en el repo por seguridad):
        ```properties
        WALLET_ISSUER_ID="tu_issuer_id"
        WALLET_CLASS_ID="tu_class_id"
        ```

5.  **Ejecutar:**
    Conecta tu dispositivo o inicia un emulador y corre la aplicación.
