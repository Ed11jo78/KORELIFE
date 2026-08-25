# 📘 DOCUMENTACIÓN TÉCNICA Y OPERATIVA OFICIAL: KORELIFE (LIFE OS)

---

## 1. RESUMEN EJECUTIVO Y VISIÓN GENERAL

**KoreLife** es una plataforma integral de productividad personal y gestión financiera (Life OS) diseñada para unificar en un único ecosistema cohesivo las mejores características y metodologías de 15 aplicaciones financieras líderes y 10 herramientas de productividad global.

- **Nombre Oficial:** KoreLife
- **Versión del Sistema:** 1.0.0 (Fase Comercial / Pruebas)
- **Plataformas Soportadas:** Android (APK / Play Store), iOS (IPA / App Store / TestFlight), Web App Responsiva (Desktop y Móvil) y Panel Administrativo Web.
- **Backend & Cloud:** Supabase Cloud (PostgreSQL 16, Auth, Realtime, Storage) y Docker Local (PostgreSQL + Redis + pgAdmin).

---

## 2. MAPEO EXHAUSTIVO DE LAS 25 APLICACIONES INTEGRADAS

### A. Módulo Financiero (15 Herramientas)

| Aplicación Original | Función Principal Integrada | Mejora Incorporada en KoreLife | Implementación en la Plataforma |
| :--- | :--- | :--- | :--- |
| **Fintonic** | Control y alertas | Señales de salud financiera | Algoritmo que calcula el *Health Score* (0-100), alertando de desviaciones presupuestarias y patrones de riesgo. |
| **Revolut** | Gastos y viajes | Analítica y organización | Gestión multimoneda nativa con desglose de consumos en itinerarios y reportes de consumo en movilidad. |
| **N26** | Orden por objetivos | Subcuentas y reglas | Espacios/Subcuentas virtuales con reglas automáticas de redondeo y transferencias por goteo. |
| **Wallet** | Control detallado | Sincronización y reportes | Conciliación de cuentas globales, sincronización bancaria y generación de balances en tiempo real. |
| **YNAB** | Método estricto | Presupuesto por asignación | Metodología *Zero-Based Budgeting*, donde cada peso o dólar ingresado se asigna a un sobre antes de gastarse. |
| **Simplifi** | Vista general | Metas y claridad | Panel consolidado de metas financieras con visualización de avance y fechas objetivo. |
| **Monarch** | Hogar | Planificación compartida | Gestión de presupuestos familiares con asignación de roles y seguimiento de metas conjuntas del hogar. |
| **Rocket Money** | Suscripciones | Recurrentes y recortes | Auditoría automática de pagos recurrentes, detección de aumentos y calculadora de ahorro. |
| **PocketGuard** | Simplicidad | Lo que queda para gastar | Indicador central *"In My Pocket"* que descuenta gastos fijos, deudas y ahorro para mostrar el saldo libre real. |
| **Empower** | Patrimonio | Visión global de activos | Monitor de patrimonio neto (*Net Worth Tracker*), calculando Activos (inmuebles, fondos) menos Pasivos (deudas). |
| **Copilot** | Experiencia móvil | Reglas y automatización | Motor de clasificación asistido por reglas lógicas configurables (`SI comercio = X ENTONCES categoría = Y`). |
| **Spendee** | Presupuesto compartido | Carteras y categorías | Billeteras independientes por proyecto o viaje con control de acceso por miembro. |
| **MoneyWiz** | Usuarios avanzados | Reportes profundos | Consultas multidimensionales, análisis de flujo de caja y proyecciones fiscales avanzadas. |
| **Monefy** | Registro manual | Rapidez y hábitos | Captura 1-tap (*one-touch entry*) para registrar gastos en menos de 2 segundos sin fricción. |
| **Honeydue** | Parejas | Visibilidad y comunicación | Vistas compartidas para parejas con selector de privacidad granular y chat/comentarios sobre cada gasto. |

---

### B. Módulo de Productividad y Tareas (10 Herramientas)

| Aplicación Original | Característica Clave Integrada | Implementación en KoreLife |
| :--- | :--- | :--- |
| **monday AI Workspace** | Flujos de trabajo flexibles | Tableros dinámicos adaptables a cualquier proyecto personal con automatizaciones de estado. |
| **ClickUp** | Colaboración y jerarquías | Jerarquía modular: Espacio > Proyecto > Lista > Tarea > Subtarea con asignaciones. |
| **Trello** | Organización visual | Vistas Kanban interactivas con estados configurables y tarjetas con portadas. |
| **Microsoft To Do** | Ecosistema Microsoft & Mi Día | Sección enfocada *"Mi Día"*, lista rápida de prioridades y sincronización de agenda. |
| **Evernote** | Notas y archivo digital | Escáner OCR de facturas y notas para digitalizar comprobantes físicos y adjuntarlos a gastos. |
| **TickTick** | Hábitos y objetivos | Monitor de rachas diarias con progreso circular y temporizador Pomodoro (25/5 min). |
| **OmniFocus** | Metodología GTD | Bandeja universal *Inbox GTD*, procesamiento guiado (Aclarar > Organizar > Ejecutar). |
| **Any.do** | Gestión sencilla | Asistente de revisión diaria y recordatorios por tiempo y prioridad. |
| **Motion** | Planificación automatizada | Motor de agenda que inserta automáticamente las tareas pendientes en los huecos libres del día. |
| **Notion** | Espacios personalizables | Base de conocimiento modular para wikis personales, planes estratégicos y documentos. |

---

## 3. ARQUITECTURA DEL SISTEMA Y STACK TECNOLÓGICO

- **App Móvil:** React Native 0.74, Expo SDK 51, TypeScript, Expo Router v3, NativeWind (Tailwind CSS), Zustand, Expo Local Authentication (Face ID / Huella), Expo Notifications.
- **Panel Administrativo Web:** React 18, Vite 5.4, TypeScript, Tailwind CSS, Recharts (visualización de métricas), React Router v6, React Hot Toast, Lucide Icons.
- **Base de Datos & Auth:** PostgreSQL 16 (con extensiones `uuid-ossp`, `pgcrypto`, `pg_trgm`), Supabase Auth con JWT rotativos y RLS (Row Level Security).
- **Contenedores:** Docker Compose (PostgreSQL 16 Alpine, Redis 7 Alpine, pgAdmin 4).

---

## 4. ESQUEMA DE BASE DE DATOS (36 TABLAS RELACIONALES)

El esquema SQL (`korelife-db/schema.sql`) incluye:
1. **Usuarios:** `users`, `user_profiles`, `user_sessions`, `password_reset_tokens`, `email_verifications`, `push_tokens`.
2. **Finanzas:** `accounts`, `categories`, `transactions`, `budgets`, `budget_allocations`, `goals`, `subscriptions`, `assets`, `liabilities`, `automation_rules`, `shared_wallets`, `shared_wallet_members`.
3. **Productividad:** `projects`, `task_lists`, `tasks`, `habits`, `habit_logs`, `pomodoro_sessions`, `note_folders`, `notes`.
4. **Social & Soporte:** `community_groups`, `group_members`, `group_messages`, `direct_messages`, `user_follows`, `support_tickets`, `ticket_messages`, `faq_categories`, `faq_articles`, `announcements`, `notifications`.

---

## 5. GUÍA DE EJECUCIÓN Y OPERACIÓN

### Iniciar Panel Admin Web
```bash
cd C:\Users\LOGISCCS03\.gemini\antigravity\scratch\korelife\korelife-admin
npm run dev
# URL: http://localhost:5173
# Login: admin@korelife.com / admin
```

### Iniciar App Móvil en Expo Go
```bash
cd C:\Users\LOGISCCS03\.gemini\antigravity\scratch\korelife\korelife-app
npx expo start --clear
# Escanear el código QR con Expo Go
```

### Generar APK Instalable para Android
```bash
cd C:\Users\LOGISCCS03\.gemini\antigravity\scratch\korelife\korelife-app
npx eas build --platform android --profile preview
```

---
*KoreLife Platform — Documentación Técnica Oficial v1.0.0*
