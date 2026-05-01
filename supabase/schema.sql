-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- Workouts table
create table public.workouts (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid references auth.users(id) on delete cascade not null,
    name text not null,
    date date not null,
    notes text default '',
    created_at timestamptz default now()
);

-- Exercises table
create table public.exercises (
    id uuid primary key default uuid_generate_v4(),
    workout_id uuid references public.workouts(id) on delete cascade not null,
    name text not null,
    sets integer not null check (sets > 0),
    reps integer not null check (reps > 0),
    weight_kg numeric(5,2),
    notes text default '',
    "order" integer default 0
);

-- Row Level Security
alter table public.workouts enable row level security;
alter table public.exercises enable row level security;

-- Workouts: users can only see/edit their own
create policy "Users manage own workouts"
    on public.workouts for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

-- Exercises: accessible via workout ownership
create policy "Users manage own exercises"
    on public.exercises for all
    using (
        exists (
            select 1 from public.workouts
            where workouts.id = exercises.workout_id
            and workouts.user_id = auth.uid()
        )
    );

-- Indexes
create index on public.workouts(user_id, date desc);
create index on public.exercises(workout_id, "order");
