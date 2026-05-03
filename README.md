# FitLog

App Android de registro e acompanhamento de treinos físicos com sugestões de treino via IA.

## Pré-requisitos

- Android Studio Ladybug (2024.2+)
- JDK 17
- Conta no [Supabase](https://supabase.com)
- Chave de API da [Anthropic](https://console.anthropic.com)

## Configuração do Supabase

1. Crie um novo projeto em [app.supabase.com](https://app.supabase.com)
2. Vá em **SQL Editor** e execute o conteúdo de `supabase/schema.sql`
3. Copie a **Project URL** e a **anon public key** em **Project Settings → API**

## Configuração do local.properties

Adicione as chaves ao arquivo `local.properties` na raiz do projeto:

```properties
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_ANON_KEY=sua-anon-key
ANTHROPIC_API_KEY=sua-api-key
```

> **Aviso:** Nunca commite `local.properties` no repositório.

## Como compilar e executar

```bash
# Build debug
./gradlew assembleDebug

# Instalar no dispositivo/emulador
./gradlew installDebug
```

Ou abra o projeto no Android Studio e clique em **Run**.

## Arquitetura

Veja [architecture.md](architecture.md) para detalhes completos.

Resumo:
- **UI**: Jetpack Compose + Material 3 (MVVM, stateless composables)
- **ViewModel**: StateFlow + Hilt
- **Data**: Repository pattern com Room (local) + Supabase (remoto)
- **Offline-first**: Room é a fonte de verdade local; sync com Supabase em background

## Funcionalidades

- Autenticação (login/cadastro) via Supabase Auth
- Registro de treinos com exercícios (séries, repetições, peso)
- Edição e exclusão de treinos
- Sincronização offline-first
- Sugestões de treino personalizadas via Claude (Anthropic API)

## Populando os treinos (seed)

1. Abra o app e faça login/cadastro
2. Acesse **Authentication → Users** no Supabase e copie seu UUID
3. Abra o arquivo `supabase/seed.sql` e substitua `'SEU_USER_ID'` pelo UUID copiado
4. Cole o conteúdo no **SQL Editor** do Supabase e execute
5. Reabra o app — os treinos aparecerão automaticamente no dropdown de "Iniciar Treino"

> Antes do seed, execute também o conteúdo de `supabase/schema.sql` para garantir que
> as tabelas `workout_sessions`, `session_exercises` e a coluna `is_template` existam.

## Limitações conhecidas

- Chave da Anthropic exposta no cliente (aceitável para protótipo; em produção use Edge Function)
- Sem sincronização em background (WorkManager não implementado ainda)
- Sem suporte a múltiplos usuários no mesmo dispositivo

## Melhorias futuras

- WorkManager para sync em background
- Gráficos de evolução de carga por exercício
- Templates de treino pré-definidos
- Export para PDF
- Integração com Health Connect
