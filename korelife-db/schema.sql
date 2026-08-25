-- ============================================================
-- KORELIFE — Esquema completo Idempotente (PostgreSQL / Supabase)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ============================================================
-- TIPOS ENUM (Con proteccion si ya existen)
-- ============================================================
DO $$ BEGIN CREATE TYPE user_plan AS ENUM ('free', 'premium', 'family', 'team'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE user_role AS ENUM ('admin', 'member', 'viewer'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE transaction_type AS ENUM ('income', 'expense', 'transfer'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE account_type AS ENUM ('checking', 'savings', 'credit', 'investment', 'cash', 'crypto', 'loan', 'other'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE budget_period AS ENUM ('weekly', 'monthly', 'quarterly', 'yearly'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE goal_status AS ENUM ('active', 'completed', 'paused', 'cancelled'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE task_priority AS ENUM ('none', 'low', 'medium', 'high', 'urgent'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE task_status AS ENUM ('inbox', 'todo', 'in_progress', 'blocked', 'done', 'cancelled'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE habit_frequency AS ENUM ('daily', 'weekly', 'monthly', 'custom'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE ticket_status AS ENUM ('open', 'in_progress', 'waiting_user', 'resolved', 'closed'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE ticket_priority AS ENUM ('low', 'medium', 'high', 'critical'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE ticket_category AS ENUM ('billing', 'technical', 'account', 'feature_request', 'bug', 'other'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE notification_type AS ENUM ('alert', 'reminder', 'social', 'system', 'support'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE message_type AS ENUM ('text', 'image', 'file', 'transaction', 'task', 'system'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE asset_type AS ENUM ('real_estate', 'vehicle', 'investment', 'crypto', 'savings', 'business', 'other'); EXCEPTION WHEN duplicate_object THEN null; END $$;
DO $$ BEGIN CREATE TYPE liability_type AS ENUM ('mortgage', 'auto_loan', 'personal_loan', 'credit_card', 'student_loan', 'other'); EXCEPTION WHEN duplicate_object THEN null; END $$;

-- ============================================================
-- TABLAS PRINCIPALES (IF NOT EXISTS)
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    username        VARCHAR(100) UNIQUE,
    avatar_url      TEXT,
    phone           VARCHAR(50),
    plan            user_plan NOT NULL DEFAULT 'free',
    is_verified     BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    is_staff        BOOLEAN DEFAULT FALSE,
    is_superadmin   BOOLEAN DEFAULT FALSE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    currency        VARCHAR(10) DEFAULT 'USD',
    language        VARCHAR(10) DEFAULT 'es',
    timezone        VARCHAR(100) DEFAULT 'America/Caracas',
    theme           VARCHAR(20) DEFAULT 'system',
    date_format     VARCHAR(30) DEFAULT 'DD/MM/YYYY',
    first_day_week  SMALLINT DEFAULT 1,
    bio             TEXT,
    location        VARCHAR(255),
    website         VARCHAR(255),
    preferences     JSONB DEFAULT '{}',
    onboarding_done BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    refresh_token   TEXT UNIQUE NOT NULL,
    device_name     VARCHAR(255),
    device_type     VARCHAR(100),
    os_info         VARCHAR(100),
    ip_address      INET,
    is_active       BOOLEAN DEFAULT TRUE,
    expires_at      TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT UNIQUE NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS email_verifications (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT UNIQUE NOT NULL,
    verified_at TIMESTAMPTZ,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS push_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT NOT NULL,
    platform    VARCHAR(20) NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, token)
);

CREATE TABLE IF NOT EXISTS accounts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    type            account_type NOT NULL DEFAULT 'checking',
    currency        VARCHAR(10) NOT NULL DEFAULT 'USD',
    balance         DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    credit_limit    DECIMAL(18, 2),
    color           VARCHAR(20) DEFAULT '#6366f1',
    icon            VARCHAR(100) DEFAULT 'bank',
    bank_name       VARCHAR(255),
    last_four       VARCHAR(4),
    is_active       BOOLEAN DEFAULT TRUE,
    include_in_net  BOOLEAN DEFAULT TRUE,
    notes           TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categories (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    icon        VARCHAR(100) DEFAULT 'tag',
    color       VARCHAR(20) DEFAULT '#6366f1',
    type        transaction_type NOT NULL DEFAULT 'expense',
    parent_id   UUID REFERENCES categories(id) ON DELETE SET NULL,
    is_system   BOOLEAN DEFAULT FALSE,
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transactions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id      UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     UUID REFERENCES categories(id) ON DELETE SET NULL,
    amount          DECIMAL(18, 2) NOT NULL,
    type            transaction_type NOT NULL,
    description     VARCHAR(500),
    notes           TEXT,
    date            DATE NOT NULL DEFAULT CURRENT_DATE,
    is_recurring    BOOLEAN DEFAULT FALSE,
    recurring_id    UUID,
    transfer_pair   UUID,
    tags            TEXT[] DEFAULT '{}',
    attachments     TEXT[] DEFAULT '{}',
    location        VARCHAR(255),
    payee           VARCHAR(255),
    is_reviewed     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS budgets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    period          budget_period NOT NULL DEFAULT 'monthly',
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS budget_allocations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    budget_id       UUID NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    amount          DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(budget_id, category_id, period_start)
);

CREATE TABLE IF NOT EXISTS goals (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_id      UUID REFERENCES accounts(id) ON DELETE SET NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    target_amount   DECIMAL(18, 2) NOT NULL,
    current_amount  DECIMAL(18, 2) DEFAULT 0.00,
    deadline        DATE,
    status          goal_status DEFAULT 'active',
    icon            VARCHAR(100) DEFAULT 'target',
    color           VARCHAR(20) DEFAULT '#10b981',
    is_shared       BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     UUID REFERENCES categories(id) ON DELETE SET NULL,
    name            VARCHAR(255) NOT NULL,
    amount          DECIMAL(18, 2) NOT NULL,
    currency        VARCHAR(10) DEFAULT 'USD',
    frequency       VARCHAR(50) NOT NULL DEFAULT 'monthly',
    billing_day     SMALLINT,
    next_billing    DATE,
    url             TEXT,
    icon_url        TEXT,
    color           VARCHAR(20) DEFAULT '#6366f1',
    is_active       BOOLEAN DEFAULT TRUE,
    notes           TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS assets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            asset_type NOT NULL,
    name            VARCHAR(255) NOT NULL,
    value           DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    currency        VARCHAR(10) DEFAULT 'USD',
    description     TEXT,
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS liabilities (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            liability_type NOT NULL,
    name            VARCHAR(255) NOT NULL,
    original_amount DECIMAL(18, 2) NOT NULL,
    current_balance DECIMAL(18, 2) NOT NULL,
    interest_rate   DECIMAL(5, 2) DEFAULT 0.00,
    monthly_payment DECIMAL(18, 2),
    due_date        DATE,
    currency        VARCHAR(10) DEFAULT 'USD',
    description     TEXT,
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS automation_rules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    condition_type  VARCHAR(100) NOT NULL,
    condition_value JSONB NOT NULL DEFAULT '{}',
    action_type     VARCHAR(100) NOT NULL,
    action_value    JSONB NOT NULL DEFAULT '{}',
    is_active       BOOLEAN DEFAULT TRUE,
    run_count       INTEGER DEFAULT 0,
    last_run_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS shared_wallets (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    icon        VARCHAR(100) DEFAULT 'users',
    color       VARCHAR(20) DEFAULT '#6366f1',
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS shared_wallet_members (
    wallet_id   UUID NOT NULL REFERENCES shared_wallets(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        user_role DEFAULT 'member',
    joined_at   TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (wallet_id, user_id)
);

CREATE TABLE IF NOT EXISTS projects (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    color           VARCHAR(20) DEFAULT '#6366f1',
    icon            VARCHAR(100) DEFAULT 'folder',
    status          VARCHAR(50) DEFAULT 'active',
    deadline        DATE,
    is_archived     BOOLEAN DEFAULT FALSE,
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS task_lists (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id  UUID REFERENCES projects(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    color       VARCHAR(20) DEFAULT '#6366f1',
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tasks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    list_id         UUID REFERENCES task_lists(id) ON DELETE SET NULL,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assignee_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    parent_task_id  UUID REFERENCES tasks(id) ON DELETE CASCADE,
    title           VARCHAR(500) NOT NULL,
    description     TEXT,
    priority        task_priority DEFAULT 'none',
    status          task_status DEFAULT 'inbox',
    due_date        TIMESTAMPTZ,
    start_date      TIMESTAMPTZ,
    estimated_mins  INTEGER,
    actual_mins     INTEGER,
    tags            TEXT[] DEFAULT '{}',
    attachments     TEXT[] DEFAULT '{}',
    sort_order      INTEGER DEFAULT 0,
    completed_at    TIMESTAMPTZ,
    is_archived     BOOLEAN DEFAULT FALSE,
    recurrence      JSONB,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS habits (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    icon            VARCHAR(100) DEFAULT 'zap',
    color           VARCHAR(20) DEFAULT '#f59e0b',
    frequency       habit_frequency DEFAULT 'daily',
    target_count    INTEGER DEFAULT 1,
    unit            VARCHAR(50) DEFAULT 'veces',
    reminder_time   TIME,
    is_active       BOOLEAN DEFAULT TRUE,
    streak_current  INTEGER DEFAULT 0,
    streak_best     INTEGER DEFAULT 0,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS habit_logs (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    habit_id    UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date        DATE NOT NULL DEFAULT CURRENT_DATE,
    count       INTEGER DEFAULT 1,
    note        TEXT,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(habit_id, date)
);

CREATE TABLE IF NOT EXISTS pomodoro_sessions (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id     UUID REFERENCES tasks(id) ON DELETE SET NULL,
    duration    INTEGER NOT NULL DEFAULT 25,
    type        VARCHAR(20) DEFAULT 'work',
    started_at  TIMESTAMPTZ NOT NULL,
    ended_at    TIMESTAMPTZ,
    completed   BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS note_folders (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    icon        VARCHAR(100) DEFAULT 'folder',
    color       VARCHAR(20) DEFAULT '#6366f1',
    parent_id   UUID REFERENCES note_folders(id) ON DELETE CASCADE,
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notes (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    folder_id   UUID REFERENCES note_folders(id) ON DELETE SET NULL,
    title       VARCHAR(500) DEFAULT 'Sin título',
    content     TEXT,
    tags        TEXT[] DEFAULT '{}',
    is_pinned   BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    cover_url   TEXT,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS community_groups (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_by  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    avatar_url  TEXT,
    cover_url   TEXT,
    type        VARCHAR(50) DEFAULT 'general',
    is_private  BOOLEAN DEFAULT FALSE,
    member_count INTEGER DEFAULT 1,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS group_members (
    group_id    UUID NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        user_role DEFAULT 'member',
    joined_at   TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE IF NOT EXISTS group_messages (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id    UUID NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content     TEXT,
    type        message_type DEFAULT 'text',
    media_url   TEXT,
    reply_to    UUID REFERENCES group_messages(id) ON DELETE SET NULL,
    is_edited   BOOLEAN DEFAULT FALSE,
    is_deleted  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    edited_at   TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS direct_messages (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    from_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    to_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content     TEXT,
    type        message_type DEFAULT 'text',
    media_url   TEXT,
    is_read     BOOLEAN DEFAULT FALSE,
    is_deleted  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_follows (
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followed_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (follower_id, followed_id)
);

CREATE TABLE IF NOT EXISTS support_tickets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_to     UUID REFERENCES users(id) ON DELETE SET NULL,
    subject         VARCHAR(500) NOT NULL,
    description     TEXT NOT NULL,
    category        ticket_category NOT NULL DEFAULT 'technical',
    priority        ticket_priority NOT NULL DEFAULT 'medium',
    status          ticket_status NOT NULL DEFAULT 'open',
    resolution      TEXT,
    resolved_at     TIMESTAMPTZ,
    closed_at       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ticket_messages (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_id   UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    author_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content     TEXT NOT NULL,
    is_staff    BOOLEAN DEFAULT FALSE,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS faq_categories (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    icon        VARCHAR(100) DEFAULT 'help-circle',
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS faq_articles (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id     UUID NOT NULL REFERENCES faq_categories(id) ON DELETE CASCADE,
    title           VARCHAR(500) NOT NULL,
    content         TEXT NOT NULL,
    tags            TEXT[] DEFAULT '{}',
    helpful_count   INTEGER DEFAULT 0,
    view_count      INTEGER DEFAULT 0,
    is_published    BOOLEAN DEFAULT TRUE,
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS announcements (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    author_id   UUID REFERENCES users(id) ON DELETE SET NULL,
    title       VARCHAR(500) NOT NULL,
    content     TEXT NOT NULL,
    type        VARCHAR(50) DEFAULT 'info',
    is_active   BOOLEAN DEFAULT TRUE,
    target_plan user_plan,
    expires_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notifications (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type        notification_type NOT NULL DEFAULT 'system',
    title       VARCHAR(255) NOT NULL,
    message     TEXT,
    data        JSONB DEFAULT '{}',
    is_read     BOOLEAN DEFAULT FALSE,
    read_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================
-- INDICES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);

-- ============================================================
-- SEED DATA (Categorías iniciales)
-- ============================================================
INSERT INTO categories (name, icon, color, type, is_system, sort_order)
VALUES 
  ('Alimentación', 'utensils', '#f59e0b', 'expense', true, 1),
  ('Transporte', 'car', '#3b82f6', 'expense', true, 2),
  ('Hogar', 'home', '#8b5cf6', 'expense', true, 3),
  ('Salud', 'heart', '#ef4444', 'expense', true, 4),
  ('Entretenimiento', 'music', '#ec4899', 'expense', true, 5),
  ('Salario', 'briefcase', '#10b981', 'income', true, 1),
  ('Inversiones', 'trending-up', '#6366f1', 'income', true, 2)
ON CONFLICT DO NOTHING;

INSERT INTO faq_categories (name, icon, sort_order)
VALUES 
  ('Primeros pasos', 'rocket', 1),
  ('Finanzas', 'dollar-sign', 2),
  ('Tareas', 'check-square', 3),
  ('Soporte', 'tool', 4)
ON CONFLICT DO NOTHING;
