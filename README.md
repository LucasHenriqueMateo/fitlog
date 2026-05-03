# FitLog

App Android de registro e acompanhamento de treinos físicos com sugestões personalizadas via IA.

---

## Funcionalidades

- **Autenticação** — login e cadastro via Supabase Auth (email/senha + Google Sign-In opcional)
- **Templates de treino** — crie e gerencie fichas de treino reutilizáveis (A1, B1, C1…)
- **Sessões ativas** — execute um treino com registro de peso por série; bordas coloridas indicam PR (verde), igual (amarelo) ou abaixo (vermelho)
- **Histórico e analytics** — gráfico de evolução de carga por exercício, streak de dias consecutivos, PRs do mês
- **Sugestões de IA** — descreva seu objetivo e receba um treino gerado pelo Claude (Anthropic)
- **Offline-first** — Room persiste dados localmente; sincronização com Supabase em background
- **Sessão persistente** — login salvo entre reinicializações via DataStore

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Android Studio | Ladybug (2024.2+) |
| JDK | 17 |
| Android SDK | API 26+ (minSdk) |
| Conta Supabase | gratuita |
| Chave Anthropic | gratuita com créditos |

---

## 1. Configuração do Supabase

1. Acesse [app.supabase.com](https://app.supabase.com) e crie um novo projeto
2. Vá em **SQL Editor** e execute o conteúdo de `supabase/schema.sql` — cria as tabelas e políticas de RLS
3. Em **Project Settings → API**, copie:
   - **Project URL** → `SUPABASE_URL`
   - **anon public** key → `SUPABASE_ANON_KEY`

### Populando templates iniciais (opcional)

1. Faça login no app e acesse **Authentication → Users** no Supabase para copiar seu UUID
2. Abra `supabase/seed.sql` e substitua `'SEU_USER_ID'` pelo UUID copiado
3. Execute o arquivo no **SQL Editor**

---

## 2. Chave da Anthropic

1. Acesse [console.anthropic.com](https://console.anthropic.com)
2. Crie uma API key em **API Keys**
3. Copie o valor → `ANTHROPIC_API_KEY`

---

## 3. Google Sign-In (opcional)

Se quiser ativar o botão "Continuar com Google":

1. Crie um projeto no [Google Cloud Console](https://console.cloud.google.com)
2. Configure **OAuth 2.0** → Web Client ID
3. Adicione o SHA-1 do seu keystore nas credenciais Android
4. Copie o **Web Client ID** → `GOOGLE_WEB_CLIENT_ID`

> Sem essa chave, o botão do Google fica desabilitado automaticamente e o app funciona normalmente com email/senha.

---

## 4. Configurar `local.properties`

Na raiz do projeto, abra (ou crie) o arquivo `local.properties` e adicione:

```properties
# Supabase
SUPABASE_URL=https://xxxxxxxxxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# Anthropic
ANTHROPIC_API_KEY=sk-ant-...

# Google Sign-In (opcional — deixe em branco para desativar)
GOOGLE_WEB_CLIENT_ID=
```

> **Segurança:** `local.properties` está no `.gitignore` e **nunca** deve ser commitado.
> As chaves são injetadas em tempo de compilação via `BuildConfig` e ficam no APK — aceitável para protótipo; em produção, use uma Edge Function ou backend próprio como proxy.

---

## 5. Build e execução

```bash
# Build debug
./gradlew assembleDebug

# Instalar diretamente no dispositivo/emulador conectado
./gradlew installDebug
```

Ou abra o projeto no **Android Studio** e clique em **Run ▶**.

---

## Arquitetura

```
app/
├── data/
│   ├── local/          # Room (entities, DAOs, database)
│   ├── remote/         # Supabase (DTOs, datasources)
│   └── repository/     # Offline-first: Room como fonte de verdade
├── di/                 # Módulos Hilt (AppModule, DatabaseModule, NetworkModule, SessionModule)
├── domain/model/       # Modelos de domínio (Workout, Exercise, WorkoutSession…)
├── navigation/         # NavGraph + Screen (rotas)
└── ui/
    ├── auth/           # Login/cadastro
    ├── home/           # Tela principal
    ├── history/        # Histórico e analytics
    ├── session/        # Iniciar e executar treino ativo
    ├── splash/         # Tela de loading para restauração de sessão
    ├── workout/        # Templates, criação e detalhes
    ├── ai/             # Sugestões via Claude
    ├── components/     # Componentes reutilizáveis
    └── theme/          # Material 3, cores, tipografia
```

**Stack:** Kotlin · Jetpack Compose · Material 3 · MVVM · Hilt · Room · Supabase · Vico · Anthropic API

---

## Segurança — checklist

- [x] `local.properties` no `.gitignore` — chaves nunca expostas no repositório
- [x] Sem strings hardcoded no código — tudo via `BuildConfig`
- [x] `seed.sql` usa placeholder `'SEU_USER_ID'` — sem UUIDs reais commitados
- [x] Row Level Security (RLS) ativo no Supabase — cada usuário acessa só os próprios dados
- [ ] Chaves no APK compilado (mitigação futura: proxy via Edge Function)

---

## Limitações conhecidas

- Chave Anthropic embutida no APK (protótipo — use proxy em produção)
- Sem sincronização em background (WorkManager não implementado)
- Sem suporte a múltiplos usuários no mesmo dispositivo

## Melhorias futuras

- WorkManager para sync em background
- Export de histórico para PDF / CSV
- Integração com Health Connect
- Proxy backend para chave Anthropic
