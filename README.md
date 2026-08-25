# KoreLife — Plataforma Comercial de Gestión Financiera y Productividad

<div align="center">
  <h3>💚 Tu vida financiera y productiva, unificada.</h3>
  <p>App móvil nativa para Android e iOS + Panel Administrativo Web</p>
</div>

---

## 📦 Estructura del Proyecto

```
korelife/
├── docker-compose.yml        # PostgreSQL + Redis + pgAdmin
├── korelife-db/
│   └── schema.sql            # Esquema completo de base de datos
├── korelife-app/             # App móvil (Expo + React Native)
└── korelife-admin/           # Panel Admin Web (React + Vite)
```

---

## 🚀 Inicio Rápido

### 1. Levantar la Base de Datos (Docker)

```bash
# Desde la carpeta korelife/
docker compose up -d
```

Servicios disponibles:
- **PostgreSQL:** `localhost:5432` (BD: korelife, User: korelife_user)
- **Redis:** `localhost:6379`
- **pgAdmin (UI):** http://localhost:5050 (admin@korelife.app / KoreLife2026!)

### 2. App Móvil (Android + iOS)

```bash
cd korelife-app
npm install
cp .env.example .env
# Edita .env con tus credenciales de Supabase
npm run start
# Escanea el QR con Expo Go en tu teléfono
```

### 3. Panel Administrativo Web

```bash
cd korelife-admin
npm install
cp .env.example .env
# Edita .env con tus credenciales de Supabase
npm run dev
# Abre http://localhost:3000
```

---

## 🗄️ Base de Datos

### Conexión Local (Docker)
```
Host:     localhost
Puerto:   5432
Base:     korelife
Usuario:  korelife_user
Password: KoreLife2026!Secure
```

### pgAdmin Web
- URL: http://localhost:5050
- Email: admin@korelife.app
- Password: KoreLife2026!

Para conectar pgAdmin al PostgreSQL local:
1. Abrir http://localhost:5050
2. Add New Server → Host: `postgres`, Port: `5432`, DB: `korelife`, User: `korelife_user`

---

## 🔐 Configuración Supabase (Cloud)

Para producción, usar Supabase en lugar de PostgreSQL local:

1. Crear cuenta gratuita en https://supabase.com
2. Crear nuevo proyecto
3. Ir a SQL Editor y ejecutar `korelife-db/schema.sql`
4. Copiar URL y ANON KEY del proyecto
5. Pegar en los archivos `.env` de la app y el admin

---

## 📱 App Móvil — Módulos

| Módulo | Descripción |
|--------|-------------|
| 🏠 **Dashboard** | My Daily Cockpit — salud financiera + agenda del día |
| 💰 **Finanzas** | Cuentas, transacciones, presupuesto zero-based, metas, suscripciones, patrimonio |
| ✅ **Tareas** | GTD Inbox, proyectos, Kanban, hábitos, notas, Pomodoro |
| 👥 **Social** | Grupos, mensajes, billeteras compartidas, comunidad |
| 🆘 **Soporte** | FAQ, tickets, reportes, feedback, anuncios |
| ⚙️ **Config** | Perfil, seguridad, plan, miembros del hogar |

---

## 🖥️ Panel Admin — Funciones

| Sección | Descripción |
|---------|-------------|
| 📊 **Dashboard** | Métricas globales, usuarios nuevos, tickets abiertos |
| 👤 **Usuarios** | Gestión completa, asignación de plan Premium |
| 🎫 **Tickets** | Soporte al cliente, responder, cambiar estados |
| ❓ **FAQ** | Crear y gestionar artículos de ayuda |
| 📢 **Anuncios** | Publicar novedades para los usuarios |
| 📈 **Analytics** | Estadísticas de uso de la plataforma |

---

## 🏗️ Build para Producción

### APK Android
```bash
cd korelife-app
npx eas build --platform android --profile preview
```

### IPA iPhone (TestFlight)
```bash
cd korelife-app
npx eas build --platform ios --profile preview
```

### Panel Admin
```bash
cd korelife-admin
npm run build
# Subir carpeta dist/ a tu hosting
```

---

## 🔒 Seguridad

- Contraseñas con bcrypt (Supabase Auth)
- JWT + Refresh Tokens rotativos
- Row Level Security (RLS) en PostgreSQL
- Autenticación biométrica en dispositivo
- HTTPS obligatorio en producción
- Rate limiting en API

---

## 📋 Credenciales de Desarrollo

| Servicio | Usuario | Contraseña |
|---------|---------|-----------|
| PostgreSQL | korelife_user | KoreLife2026!Secure |
| Redis | — | KoreLifeRedis2026! |
| pgAdmin | admin@korelife.app | KoreLife2026! |
| Admin App | (crear en Supabase con is_superadmin=true) | — |

---

## 🛠️ Stack Tecnológico

**App Móvil:** Expo 51 · React Native · TypeScript · NativeWind · Zustand · Supabase

**Panel Admin:** React 18 · Vite · TypeScript · Tailwind CSS · React Query · Supabase

**Base de Datos:** PostgreSQL 16 · Redis 7 · Supabase (producción)

**Build/Deploy:** EAS Build · Docker · GitHub Actions

---

*KoreLife v1.0.0 — Fase de pruebas*
