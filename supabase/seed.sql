-- Execute este seed no Supabase SQL Editor
-- Substitua 'SEU_USER_ID' pelo seu UUID de usuário
-- (veja em Authentication → Users → copie o UUID)

DO $$
DECLARE
  uid uuid := 'SEU_USER_ID';  -- << SUBSTITUA AQUI
  wid uuid;
BEGIN

  -- A1 - Peito e Tríceps
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'A1 - Peito e Tríceps', CURRENT_DATE, 'A1', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'CRUCIFIXO INCLINADO POLIA', 4, 12, 1),
    (wid, 'SUPINO RETO (WIDE CHEST)', 4, 12, 2),
    (wid, 'CROSSOVER POLIA ALTA', 4, 12, 3),
    (wid, 'TRÍCEPS GRAVITON / BANCO', 4, 12, 4),
    (wid, 'TRÍCEPS PULLEY', 4, 12, 5),
    (wid, 'TRÍCEPS FRANCÊS UNL', 4, 12, 6);

  -- A2 - Peito e Tríceps
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'A2 - Peito e Tríceps', CURRENT_DATE, 'A2', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'SUPINO INCLINADO SMITH', 4, 12, 1),
    (wid, 'CROSSOVER POLIA MÉDIA SENTADO', 4, 12, 2),
    (wid, 'MERGULHO (DIPS)', 4, 12, 3),
    (wid, 'TRÍCEPS CROSS UNILATERAL', 4, 12, 4),
    (wid, 'TRÍCEPS TESTA', 4, 12, 5),
    (wid, 'TRÍCEPS COICE', 4, 12, 6);

  -- B1 - Perna
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'B1 - Perna', CURRENT_DATE, 'B1', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'CADEIRA EXTENSORA', 4, 12, 1),
    (wid, 'LEG PRESS 45', 4, 12, 2),
    (wid, 'AGACHAMENTO SMITH', 4, 12, 3),
    (wid, 'TRAPÉZIO', 4, 12, 4),
    (wid, 'ELEVAÇÃO LATERAL POLIA MÉDIA', 4, 12, 5),
    (wid, 'CRUCIFIXO INVERTIDO', 4, 12, 6);

  -- B2 - Perna
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'B2 - Perna', CURRENT_DATE, 'B2', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'ELEVAÇÃO PÉLVICA', 4, 12, 1),
    (wid, 'MESA / CADEIRA FLEXORA', 4, 12, 2),
    (wid, 'STIFF SMITH', 4, 12, 3),
    (wid, 'AGACHAMENTO SMITH', 4, 12, 4),
    (wid, 'TRAPÉZIO', 4, 12, 5),
    (wid, 'ELEVAÇÃO LATERAL POLIA BAIXA', 4, 12, 6),
    (wid, 'CRUCIFIXO INVERTIDO', 4, 12, 7);

  -- C1 - Costas e Bíceps
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'C1 - Costas e Bíceps', CURRENT_DATE, 'C1', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'BARRA FIXA PEGADA ABERTA AQC', 4, 12, 1),
    (wid, 'PULLEY BARRA ROMANA', 4, 12, 2),
    (wid, 'REMADA', 4, 12, 3),
    (wid, 'PULL DOWN MÁQUINA', 4, 12, 4),
    (wid, 'SUPERMAN CABLE CURL', 4, 12, 5),
    (wid, 'ROSCA 45°', 4, 12, 6),
    (wid, 'ROSCA SCOTT HALTER', 4, 12, 7);

  -- C2 - Costas e Bíceps
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'C2 - Costas e Bíceps', CURRENT_DATE, 'C2', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'BARRA FIXA PEGADA PARALELA AQC', 4, 12, 1),
    (wid, 'PULLEY MÁQUINA UNL', 4, 12, 2),
    (wid, 'REMADA CURVADA', 4, 12, 3),
    (wid, 'DORSAL POLIA', 4, 12, 4),
    (wid, 'ROSCA MARTELO', 4, 12, 5),
    (wid, 'ROSCA DIRETA POLIA', 4, 12, 6),
    (wid, 'SUPERMAN CABLE CURL', 4, 12, 7);

  -- D - Tríceps e Antebraço
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'D - Tríceps e Antebraço', CURRENT_DATE, 'D', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'TRÍCEPS UNL', 4, 12, 1),
    (wid, 'TRÍCEPS BANCO', 4, 12, 2),
    (wid, 'TRÍCEPS POLIA MÉDIA (DESENVOLV)', 4, 12, 3),
    (wid, 'TRÍCEPS COICE', 4, 12, 4),
    (wid, 'ROSCA INVERSA', 4, 12, 5),
    (wid, 'EXTENSÃO PUNHO', 4, 12, 6),
    (wid, 'ABS', 4, 20, 7);

  -- E - Ombro
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'E - Ombro', CURRENT_DATE, 'E', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'CRUCIFIXO INVERTIDO', 4, 12, 1),
    (wid, 'ELEVAÇÃO LATERAL POLIA', 4, 12, 2),
    (wid, 'ELEVAÇÃO LATERAL POLIA MÉDIA', 4, 12, 3),
    (wid, 'DESENVOLVIMENTO MÁQUINA', 4, 12, 4),
    (wid, 'PANTURRILHA', 4, 20, 5),
    (wid, 'CRUCIFIXO INCLINADO POLIA', 4, 12, 6),
    (wid, 'CROSSOVER POLIA ALTA', 4, 12, 7),
    (wid, 'SUPINO RETO / CROSSOVER POLIA MÉDIA', 4, 12, 8);

  -- F - Costas e Bíceps 2
  INSERT INTO public.workouts (id, user_id, name, date, notes, is_template)
  VALUES (uuid_generate_v4(), uid, 'F - Costas e Bíceps 2', CURRENT_DATE, 'F', true)
  RETURNING id INTO wid;
  INSERT INTO public.exercises (workout_id, name, sets, reps, "order") VALUES
    (wid, 'BÍCEPS SENTADO POLIA MÉDIA', 4, 12, 1),
    (wid, 'ROSCA BARRA', 4, 12, 2),
    (wid, 'ROSCA MARTELO CONCENTRADA', 4, 12, 3),
    (wid, 'PULLEY MAQUINA UNL', 4, 12, 4),
    (wid, 'PULLEY TRIÂNGULO', 4, 12, 5),
    (wid, 'REMADA MAQUINA / DORSAL POLIA', 4, 12, 6);

END $$;
