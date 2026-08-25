# 🚀 KoreLife — Dossier Ejecutivo y Documentación Integral del Sistema
**Plataforma Integral de Finanzas Personales Multimoneda, Productividad GTD y Comunidad**

---

## 📌 1. Resumen Ejecutivo (Executive Summary)

**KoreLife** es una solución tecnológica *all-in-one* concebida para transformar radicalmente la manera en que las personas gestionan su patrimonio, organizan su productividad diaria e interactúan con una comunidad orientada al crecimiento financiero.

A diferencia de las aplicaciones tradicionales diseñadas exclusivamente para economías de moneda única, KoreLife nace con un **motor multimoneda nativo en tiempo real**, optimizado tanto para la realidad de economías con múltiples tipos de cambio (como Venezuela con Bolívares VES, Dólares USD y Euros EUR) como para usuarios globales que operan con dinero fiat, plataformas P2P (Binance, Airtm) y cuentas internacionales.

---

## 🎯 2. Propuesta de Valor Diferencial

1. **Gestión Multimoneda en Tiempo Real**: Conversión automática instantánea entre Bolívares (VES), Dólares (USD) y Euros (EUR) utilizando tasas oficiales del Banco Central (BCV) y tasas de mercado P2P (Binance y Airtm).
2. **Cockpit Financiero Diario**: Visualización inmediata del patrimonio neto total, límites de gasto diario ("PocketGuard") y métricas de salud financiera (0 a 100).
3. **Productividad Basada en Metodologías Probadas**: Integración completa de la metodología GTD (*Getting Things Done*), tableros Kanban, Pomodoro Timer integrado y seguimiento de hábitos con rachas.
4. **Comunidad Social Interactiva**: Red de debates, grupos temáticos, mensajería directa en tiempo real y feed social de logros financieros.
5. **Panel Administrativo Corporativo (SaaS Admin)**: Panel web profesional para métricas de retención, asignación de membresías y resolución de tickets de soporte.
6. **Multiplataforma Nativa**: Disponible en iPhone (iOS), Android, Web PWA y Expo Go con sincronización en la nube.

---

## 🧱 3. Arquitectura Tecnológica del Ecosistema

```mermaid
graph TD
    subgraph Frontend [Clientes de Usuario]
        MobileApp[📱 KoreLife Mobile App (iOS / Android / Expo)]
        WebApp[🌐 KoreLife Web App PWA (Vercel Production)]
        AdminPanel[💻 KoreLife Admin Portal (React + Vite + Tailwind)]
    end

    subgraph CoreEngine [Capa de Lógica y Estado]
        ZustandAuth[Auth & OAuth Store]
        ZustandFinance[Finance & Multi-Currency Engine]
        ZustandTasks[GTD & Habits Store]
        ZustandSocial[Social & Realtime Chat Store]
        RatesEngine[Live Rates Monitor (BCV / Binance / Airtm)]
    end

    subgraph Backend [Infraestructura en la Nube]
        SupabaseAuth[Supabase Auth (Google OAuth & Email)]
        SupabaseDB[(PostgreSQL Database + RLS Security)]
        SupabaseRealtime[Supabase Realtime WebSockets]
        SupabaseStorage[Cloud Storage (Facturas / Avatares)]
        EASCloud[Expo Application Services Cloud]
        VercelEdge[Vercel Serverless Edge Network]
    end

    Frontend --> CoreEngine
    CoreEngine --> Backend
```

---

## 📱 4. Módulos y Funcionalidades del Sistema

### 4.1. Módulo Financiero Multimoneda (Finanzas Pro)
* **Monitor de Tasas en Vivo**:
  * Tasa BCV USD (Oficial).
  * Tasa BCV EUR (Oficial).
  * Tasa Binance P2P USDT (Mercado libre).
  * Tasa Airtm USD.
  * Selector dinámico para recalcular todo el patrimonio y transacciones bajo la tasa seleccionada.
* **Gestión de Cuentas y Billeteras**:
  * Cuentas bancarias en Bolívares (Pago Móvil, Banesco, Mercantil, BDV, etc.).
  * Billeteras digitales e internacionales (Binance USDT, Zinli, Zelle, PayPal, Efectivo $ / Bs).
* **Registro Inteligente de Transacciones**:
  * Registro de Ingresos, Gastos y Transferencias entre cuentas.
  * Teclado numérico de alto contraste para ingreso rápido en Bolívares o Dólares.
  * Categorización automática con iconos vectoriales coloreados.
* **Presupuestos Zero-Based (YNAB Style)**:
  * Asignación de cada centavo recibido a categorías específicas (Alimentación, Servicios, Vivienda, Transporte, Entretenimiento, etc.).
  * Barras de progreso de gasto y alertas visuales de exceso presupuestario.
* **Metas de Ahorro y Patrimonio**:
  * Metas con cálculo automático de porcentaje completado y fechas estimadas.
  * Análisis de Activos vs Pasivos para cálculo del Patrimonio Neto Real.
* **Gestor de Suscripciones**:
  * Control de pagos recurrentes (Netflix, Spotify, Internet, Gym, etc.) con alertas de cobro próximo.

---

### 4.2. Módulo de Productividad y GTD
* **Cockpit "Mi Día" & Inbox GTD**:
  * Captura rápida de tareas sin procesar y planificación del día actual.
* **Tablero Kanban Drag & Drop**:
  * Columnas interactivas: *Por hacer*, *En progreso*, *Bloqueado*, *Completado*.
* **Seguimiento de Hábitos (Habit Tracker)**:
  * Sistema de rachas diarias con animaciones de celebración.
  * Gráficas de consistencia semanal.
* **Temporizador Pomodoro Integrado**:
  * Bloques configurables de 25 minutos de enfoque y 5 minutos de descanso.
* **Base de Conocimiento y Notas**:
  * Notas enriquecidas organizadas por carpetas y etiquetas, con soporte para notas fijadas.

---

### 4.3. Módulo Social y Soporte Comunitario
* **Feed de Logros y Finanzas Compartidas**:
  * Publicación de metas alcanzadas, consejos financieros y celebraciones.
  * Likes ❤️ y comentarios en tiempo real.
* **Grupos y Comunidades Temáticas**:
  * Canales de debate (ej: Inversión en Cripto, Emprendimiento, Ahorro Inteligente).
* **Mensajería Directa & Asistente IA**:
  * Conversaciones persona a persona y canal automatizado de asistencia rápida.
* **Centro de Ayuda y Sistema de Tickets**:
  * Buscador de preguntas frecuentes (FAQ) categorizadas.
  * Creación y seguimiento de tickets de soporte técnico con estados en vivo.

---

### 4.4. Panel Administrativo Web (KoreLife Admin)
* **Gestión de Usuarios**:
  * Alta, baja, bloqueo y asignación directa de planes (Free, Premium, Family, Team).
* **Mesa de Soporte**:
  * Bandeja de entrada para responder y resolver tickets de usuarios.
* **Analytics y Métricas de Negocio**:
  * Crecimiento diario de usuarios, distribución de membresías y reportes de incidencias.

---

## 🔐 5. Seguridad y Autenticación

* **Google OAuth 2.0 Integrado**: Inicio de sesión seguro con un clic mediante ventana nativa del navegador y deep linking seguro (`korelife://auth/callback`).
* **Autenticación Biométrica**: Compatibilidad con Face ID / Touch ID en iOS y Biometría dactilar en Android.
* **Row Level Security (RLS)**: Cada usuario en la base de datos PostgreSQL de Supabase únicamente tiene permisos de lectura y escritura sobre sus propios registros financieros.
* **Modo Demostración / Rápido**: Opción de acceso para auditorías, jurados o presentaciones sin requerir registro previo de tarjeta o correo.

---

## 🌐 6. Métodos de Despliegue y Accesos Actuales

| Canal | Enlace / Método | Descripción |
|---|---|---|
| **Web de Producción (Vercel)** | `https://korelife-app.vercel.app` | Accesible desde cualquier navegador móvil y desktop. Optimizada como PWA con "Añadir a pantalla de inicio" en iPhone/Android. |
| **Expo EAS Cloud Update** | `npx eas-cli update --branch preview` | Publicación instantánea en la nube de Expo. Abre en la app **Expo Go** incluso con la PC apagada. |
| **Túnel Remoto en Vivo** | `iniciar-tunel.bat` | Conexión directa a través de Ngrok para pruebas en vivo con depuración activa desde cualquier red. |
| **Instalador Nativo iOS (.IPA)** | `generar-ios-ipa.bat` | Compilación de ejecutable nativo para firma e instalación vía Sideloadly / AltStore / TestFlight. |

---

## 📊 7. Estructura de Datos (Esquema Principal)

* **`users`**: ID, correo, nombre completo, moneda principal (VES/USD/EUR), avatar, plan de suscripción.
* **`accounts`**: ID, nombre, tipo (banco, efectivo, crédito, inversión), saldo, moneda.
* **`transactions`**: ID, cuenta origen, tipo (ingreso/gasto/transferencia), monto primario, monto secundario, tasa aplicada, categoría, fecha.
* **`budgets`**: ID, categoría, límite asignado, período, monto gastado.
* **`goals`**: ID, nombre, monto meta, monto acumulado, fecha límite.
* **`tasks`**: ID, título, prioridad (alta, media, baja), estado (todo, in_progress, done), fecha vencimiento, proyecto.
* **`habits`**: ID, nombre, frecuencia, racha actual, mejor racha, historial diario.
* **`social_posts`**: ID, autor, contenido, categoría, likes_count, comments_count.
* **`support_tickets`**: ID, usuario, asunto, categoría, prioridad, estado, historial de mensajes.

---

## 🎤 8. Guion Recomendado para Presentaciones (Pitch Script)

1. **Introducción (El Problema - 30 seg)**:
   > *"Gestionar finanzas en un entorno multimoneda con inflación y múltiples tasas de cambio genera caos y desorganización. La mayoría de las aplicaciones solo aceptan una moneda y están desconectadas de las tareas y hábitos diarios del usuario."*

2. **La Solución KoreLife (1 min)**:
   > *"KoreLife unifica en un solo lugar la salud financiera multimoneda con conversión en tiempo real de tasas BCV, Binance y Airtm, con un gestor GTD de productividad, hábitos con rachas y una comunidad interactiva."*

3. **Demostración en Vivo (2 min)**:
   > * Mostrar el Dashboard Principal con el saldo total en Bolívares y Dólares.
   > * Registrar un gasto en Bolívares y ver cómo se refleja automáticamente en Dólares bajo la tasa en vivo.
   > * Mostrar el tablero Kanban de tareas y el temporizador Pomodoro.
   > * Mostrar el feed de la comunidad y el sistema de soporte.

4. **Cierre y Escalabilidad (30 seg)**:
   > *"KoreLife está lista para operar en iOS, Android y Web PWA sobre una infraestructura en la nube de alta disponibilidad, con panel administrativo para escalar usuarios y monetización SaaS."*
