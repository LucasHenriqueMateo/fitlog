create extension if not exists "uuid-ossp";

-- Workouts
create table if not exists public.workouts (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid references auth.users(id) on delete cascade not null,
    name text not null,
    date date not null,
    notes text default '',
    created_at timestamptz default now()
);

alter table public.workouts add column if not exists is_template boolean default false;

-- Exercises
create table if not exists public.exercises (
    id uuid primary key default uuid_generate_v4(),
    workout_id uuid references public.workouts(id) on delete cascade not null,
    name text not null,
    sets integer not null check (sets > 0),
    reps integer not null check (reps > 0),
    weight_kg numeric(5,2),
    notes text default '',
    "order" integer default 0
);

-- workout_sessions
create table if not exists public.workout_sessions (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid references auth.users(id) on delete cascade not null,
    template_id uuid references public.workouts(id),
    template_name text not null,
    template_code text not null,
    started_at timestamptz default now(),
    finished_at timestamptz
);

-- session_exercises
create table if not exists public.session_exercises (
    id uuid primary key default uuid_generate_v4(),
    session_id uuid references public.workout_sessions(id) on delete cascade not null,
    exercise_name text not null,
    sets integer,
    reps integer,
    weight_kg numeric(5,2),
    recorded_at timestamptz default now()
);

-- RLS
alter table public.workouts enable row level security;
alter table public.exercises enable row level security;
alter table public.workout_sessions enable row level security;
alter table public.session_exercises enable row level security;

-- Policies (drop antes de criar para evitar erro de duplicata)
drop policy if exists "Users manage own workouts" on public.workouts;
create policy "Users manage own workouts"
    on public.workouts for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

drop policy if exists "Users manage own exercises" on public.exercises;
create policy "Users manage own exercises"
    on public.exercises for all
    using (
        exists (
            select 1 from public.workouts
            where workouts.id = exercises.workout_id
            and workouts.user_id = auth.uid()
        )
    );

drop policy if exists "Users manage own sessions" on public.workout_sessions;
create policy "Users manage own sessions"
    on public.workout_sessions for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

drop policy if exists "Users manage own session exercises" on public.session_exercises;
create policy "Users manage own session exercises"
    on public.session_exercises for all
    using (
        exists (
            select 1 from public.workout_sessions s
            where s.id = session_exercises.session_id
            and s.user_id = auth.uid()
        )
    );

-- Indexes (IF NOT EXISTS disponível no Postgres 9.5+)
create index if not exists idx_workouts_user on public.workouts(user_id, date desc);
create index if not exists idx_exercises_workout on public.exercises(workout_id, "order");
create index if not exists idx_sessions_user on public.workout_sessions(user_id, started_at desc);
create index if not exists idx_session_ex_session on public.session_exercises(session_id);
create index if not exists idx_session_ex_name on public.session_exercises(exercise_name);