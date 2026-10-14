<div align="center">

# 🌟 KoreLife (Life OS) — Plataforma Integral de Productividad y Finanzas

[![Version](https://img.shields.io/badge/version-1.0.0-emerald?style=for-the-badge)](https://github.com)
[![Platform](https://img.shields.io/badge/platform-Android%2014%2B%20%7C%20Web%20Admin-blue?style=for-the-badge)](https://github.com)
[![Tech Stack](https://img.shields.io/badge/stack-Kotlin%20%7C%20Jetpack%20Compose%20%7C%20React%20%7C%20Firebase-purple?style=for-the-badge)](https://github.com)
[![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)](https://github.com)

*La evolución definitiva del bienestar personal: unificando las mejores características de 15 aplicaciones financieras y 10 herramientas de productividad global en un único ecosistema inteligente, sincronizado en la nube y con Panel Web de Administración Maestro.*

[Ver Demo en Vivo](#-guía-de-instalación-y-ejecución) • [Reportar Bug](https://github.com) • [Solicitar Funcionalidad](https://github.com)

</div>

---

## 📖 Tabla de Contenidos
1. [🌟 Resumen Ejecutivo](#-resumen-executive)
2. [🏛️ Arquitectura y Componentes del Sistema](#-arquitectura-y-componentes-del-sistema)
3. [💳 Módulo Financiero (15 Apps Integradas)](#-módulo-financiero-15-apps-integradas)
4. [⚡ Módulo de Productividad (10 Apps Integradas)](#-módulo-de-productividad-10-apps-integradas)
5. [🔔 Funcionalidades Destacadas (Notificaciones, Hábitos y Divisas)](#-funcionalidades-destacadas-notificaciones-hábitos-y-divisas)
6. [👑 Panel de Administración Web Maestro](#-panel-de-administración-web-maestro)
7. [⚙️ Guía de Instalación y Configuración](#-guía-de-instalación-y-configuración)
8. [📊 Esquema de Base de Datos Cloud Firestore](#-esquema-de-base-de-datos-cloud-firestore)
9. [🛡️ Seguridad y Autenticación](#-seguridad-y-autenticación)
10. [👥 Contribución y Licencia](#-contribución-y-licencia)

---

## 🌟 Resumen Ejecutivo

**KoreLife** es un Sistema Operativo Personal (*Life OS*) diseñado para erradicar la fragmentación digital. En lugar de utilizar múltiples aplicaciones desconectadas para gestionar presupuestos, inversiones, tareas, hábitos y notas, KoreLife unifica todo en una experiencia nativa móvil en **Android (Kotlin + Jetpack Compose)** ultrarrápida, complementada con un **Panel Web Administrativo** de control total (`admin_panel.html`) y persistencia en tiempo real con **Firebase Cloud Firestore**.

---

## 🏛️ Arquitectura y Componentes del Sistema

```
┌────────────────────────────────────────────────────────┐
│                   KORELIFE ECOSYSTEM                   │
├──────────────────────────┬─────────────────────────────┤
│   Android Mobile App     │     Admin Web Dashboard     │
│  (Kotlin / Compose / M3) │   (React / Tailwind / Chart)│
└─────────────┬────────────+──────────────┬──────────────┘
              │                           │
              └─────────────┬─────────────┘
                            ▼
              ┌──────────────────────────┐
              │ Firebase Cloud Firestore │
              │  & Authentication (Auth) │
              └─────────────┬─────────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
      [ BCV API Service ]        [ Binance P2P API ]
```

* **App Móvil (`/app`):** Desarrollada con arquitectura MVVM, Coroutines, Flow, Jetpack Navigation y Material Design 3.
* **Panel Web (`/admin_panel.html`):** Interfaz web independiente para el Administrador Maestro con métricas globales, control de usuarios, tickets de soporte y tasas en tiempo real.
* **Backend y Sincronización:** Firebase Authentication (Email & Google Sign-In) y Cloud Firestore con reglas de seguridad estrictas.

---

## 💳 Módulo Financiero (15 Apps Integradas)

| Aplicación Original | Función Principal Integrada en KoreLife |
| :--- | :--- |
| **Fintonic** | Señales de salud financiera (*Health Score* 0-100) y alertas de desviación. |
| **Revolut** | Gestión multimoneda nativa y desglose de gastos en movilidad. |
| **N26** | Subcuentas virtuales (*Spaces*) con reglas automáticas de redondeo. |
| **Wallet** | Sincronización y conciliación bancaria global en tiempo real. |
| **YNAB** | Metodología estricta *Zero-Based Budgeting* (asignación por sobre). |
| **Simplifi** | Panel consolidado de metas financieras con barras de progreso. |
| **Monarch** | Presupuestos familiares y planificación compartida del hogar. |
| **Rocket Money** | Auditoría automática de pagos recurrentes y suscripciones. |
| **PocketGuard** | Indicador *"In My Pocket"* (saldo libre real tras gastos fijos). |
| **Empower** | Monitor de patrimonio neto (*Net Worth Tracker*). |
| **Copilot** | Motor de reglas lógicas inteligentes (`SI comercio = X ENTONCES categoría = Y`). |
| **Spendee** | Billeteras independientes por proyecto o viaje con control de acceso. |
| **MoneyWiz** | Análisis avanzados de flujo de caja y proyecciones. |
| **Monefy** | Captura 1-tap (*one-touch entry*) para registrar gastos en < 2 segundos. |
| **Honeydue** | Vistas compartidas para parejas con selector de privacidad granular. |

---

## ⚡ Módulo de Productividad (10 Apps Integradas)

| Aplicación Original | Característica Clave Integrada en KoreLife |
| :--- | :--- |
| **monday AI Workspace** | Tableros dinámicos adaptables con automatizaciones de estado. |
| **ClickUp** | Jerarquía modular: Espacio > Proyecto > Lista > Tarea > Subtarea. |
| **Trello** | Vistas Kanban interactivas con estados configurables. |
| **Microsoft To Do** | Sección *"Mi Día"* y lista rápida de prioridades. |
| **Evernote** | Escáner OCR de facturas y notas para digitalizar comprobantes. |
| **TickTick** | Monitor de rachas diarias y temporizador Pomodoro (25/5 min). |
| **OmniFocus** | Bandeja universal *Inbox GTD* con procesamiento guiado. |
| **Any.do** | Asistente de revisión diaria y recordatorios inteligentes. |
| **Motion** | Planificación automatizada que inserta tareas en huecos libres. |
| **Notion** | Base de conocimiento modular para wikis y planes estratégicos. |

---

## 🔔 Funcionalidades Destacadas

1. **Sistema de Alertas Inteligentes de Presupuesto:**
   * Notificaciones automáticas cuando el usuario alcanza el **80% o 90%** del límite mensual definido en cualquier categoría de gasto.
2. **Mapa de Calor de Hábitos (Heatmap Calendar):**
   * Vista de calendario interactiva que visualiza gráficamente el historial de consistencia mensual y rachas de cumplimiento de hábitos.
3. **Servicio Multimoneda en Tiempo Real (BCV & Binance):**
   * Actualización automática de tasas de cambio utilizando las APIs oficiales del Banco Central de Venezuela (BCV) y Binance P2P.
4. **Gráficos Interactivos de Ingresos vs. Gastos:**
   * Visualización avanzada comparando flujos de caja por categoría mediante gráficos de barras y áreas.

---

## 👑 Panel de Administración Web Maestro

El archivo `admin_panel.html` provee un panel exclusivo para el **Administrador Maestro**:
* **Dashboard Global:** Métricas de usuarios activos, transacciones y estado de servidores.
* **Gestión de Usuarios:** Listado, búsqueda, cambio de roles y bloqueo de cuentas.
* **Monitoreo de Tasas:** Tasas BCV y Binance sincronizadas.
* **Mesa de Ayuda (Tickets):** Resolución y estatus de incidencias reportadas desde la app móvil.

---

## ⚙️ Guía de Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/korelife.git
cd korelife
```

### 2. Configurar la App Android
* Abre el proyecto en **Android Studio (Giraffe o superior)**.
* Asegúrate de configurar tu archivo `google-services.json` de Firebase en `app/google-services.json`.
* Sincroniza Gradle (`build.gradle.kts`) y compila el proyecto.

### 3. Ejecutar el Panel Web
* Abre directamente el archivo `admin_panel.html` en tu navegador web, o sije el archivo en cualquier servidor estático (Vercel, Netlify, GitHub Pages).

---

## 📊 Esquema de Base de Datos Cloud Firestore

* `users/{userId}`: Perfil, rol (`is_superadmin`), preferencias y suscripción.
* `transactions/{txId}`: Registro de ingresos/egresos, categoría y divisa.
* `budgets/{budgetId}`: Presupuestos mensuales y umbrales de alerta (80%/90%).
* `habits/{habitId}`: Definición de hábitos y registros diarios de consistencia (*heatmap*).
* `support_tickets/{ticketId}`: Mensajes y estado de soporte técnico.

---

<div align="center">
  <p>Desarrollado con ❤️ para la excelencia en productividad y finanzas personales.</p>
</div>

