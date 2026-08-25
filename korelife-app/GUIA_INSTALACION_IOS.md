# 📱 Guía Oficial: Cómo Ejecutar e Instalar KoreLife en iOS sin Expo Go

Esta guía describe los **3 métodos oficiales y directos** para que cualquier persona pueda instalar y usar la aplicación **KoreLife** en un iPhone físico sin necesidad de abrir ni depender de Expo Go.

---

## 🌟 Método 1: Apple TestFlight (Recomendado para distribuir a cualquier persona en el mundo)

**¿Para quién es?** Para que cualquier persona pueda instalar la app con un enlace público o invitación por correo, exactamente igual que una app descargada de la App Store.

### Pasos:
1. **Ejecutar el script:**
   - Haz doble clic en `generar-ios-testflight.bat` o corre en la terminal:
   ```bash
   npx eas-cli build --platform ios --profile production
   ```
2. **Autenticación con Apple Developer:**
   - EAS Build solicitará tus credenciales de Apple Developer (o iniciará sesión mediante tu cuenta de Expo).
   - El proceso compilará el binario nativo en los servidores de Expo en la nube (sin requerir una Mac física).
3. **Enviar a TestFlight:**
   - Una vez finalizada la compilación, sube el binario a TestFlight con:
   ```bash
   npx eas-cli submit -p ios --latest
   ```
4. **Distribución:**
   - En [App Store Connect](https://appstoreconnect.apple.com) ve a la sección **TestFlight**.
   - Crea un **Enlace Público** (Public Link).
   - ¡Cualquier persona que abra ese enlace podrá instalar KoreLife directamente en su iPhone con un solo toque!

---

## ⚡ Método 2: Instalación Directa de Archivo `.IPA` (Sideloading sin pagar cuenta de Apple)

**¿Para quién es?** Para instalar la app directamente en iPhones de prueba, familiares o amigos usando un archivo `.ipa` compilado, con cualquier cuenta gratuita de Apple ID.

### Pasos:
1. **Generar el ejecutable `.IPA`:**
   - Haz doble clic en `generar-ios-ipa.bat` o corre:
   ```bash
   npx eas-cli build --platform ios --profile preview
   ```
2. **Descargar el archivo `.IPA`:**
   - Al terminar la compilación en EAS, se te proporcionará un enlace directo de descarga para el archivo `KoreLife.ipa`.
3. **Instalar en el iPhone (Elige cualquiera de estas herramientas gratuitas):**

   - **Opción A (Sideloadly - Muy fácil en Windows):**
     1. Descarga [Sideloadly](https://sideloadly.io/) en tu PC Windows.
     2. Conecta el iPhone por USB a la PC.
     3. Arrastra el archivo `KoreLife.ipa` dentro de Sideloadly.
     4. Ingresa tu Apple ID y presiona **Start**.
     5. La app se instalará en la pantalla de inicio del iPhone.
     6. En el iPhone, ve a *Ajustes > General > VPN y gestión de dispositivos*, toca tu Apple ID y presiona **"Confiar"**.

   - **Opción B (AltStore):**
     1. Instala AltServer en tu PC Windows desde [altstore.io](https://altstore.io/).
     2. Abre AltStore en tu iPhone y selecciona `+` para elegir el archivo `KoreLife.ipa`.

---

## 🌐 Método 3: Versión Web App (PWA Instantánea - Cero instalación)

**¿Para quién es?** Para que cualquier usuario en iPhone pueda entrar de inmediato sin descargar archivos ni configurar cuentas de desarrollador.

### Pasos:
1. Exportar la web lista para producción:
   ```bash
   npx expo export --platform web
   ```
2. Desplegar la carpeta `dist/` en cualquier servicio gratuito (Vercel, Netlify o Cloudflare Pages).
3. El usuario abre la URL en **Safari** en su iPhone.
4. Toca el botón **Compartir** (icono de cuadrado con flecha hacia arriba) y selecciona **"Añadir a pantalla de inicio"**.
5. ¡La app aparecerá con su icono oficial, pantalla completa y funcionará como una app nativa independiente!

---

## 🛠️ Resumen de Archivos y Scripts Disponibles en el Proyecto

| Script / Archivo | Función |
|---|---|
| `generar-ios-ipa.bat` | Compila el instalador nativo `.IPA` para distribución interna / Sideloading |
| `generar-ios-testflight.bat` | Compila la versión de producción para Apple TestFlight y App Store |
| `generar-ios-simulador.bat` | Compila para el simulador de Xcode en Mac |
| `generar-apk.bat` | Compila el instalador nativo `.APK` para Android |
| `iniciar-app.bat` | Inicia el servidor de desarrollo local para pruebas rápidas |
