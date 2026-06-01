# 🚀 Talents Express — Backend API (MVP)
**Disciplina:** Projeto e Desenvolvimento de Sistemas II  
**Integrantes:** Pedro Peres · Nickolas Brussolo · Matheus Carvalho · Bruno Enrique

---

## ⚙️ Pré-requisitos
- **Java 17+** (recomendado: Java 21)
- **Maven 3.8+**
- IDE: IntelliJ IDEA, Eclipse ou VS Code com Extension Pack for Java

---

## ▶️ Como Rodar

```bash
# 1. Entre na pasta do backend
cd backend

# 2. Compile e execute
mvn spring-boot:run

# A API sobe em http://localhost:8080
```

**Console do Banco H2 (ver dados em tempo real):**  
Acesse: `http://localhost:8080/h2-console`  
- JDBC URL: `jdbc:h2:mem:talentsexpress`
- Usuário: `sa` | Senha: *(em branco)*

---

## 👤 Contas de Teste (carregadas automaticamente)

| Perfil    | E-mail               | Senha     |
|-----------|----------------------|-----------|
| Admin     | admin@talents.com    | admin123  |
| Cliente   | maria@email.com      | senha123  |
| Prestador | joao@email.com       | senha123  |
| Prestador | ana@email.com        | senha123  |
| Prestador | carlos@email.com     | senha123  |

---

## 📡 Endpoints da API

### 🔐 Auth (RF01 · RF02)
| Método | Rota                   | Descrição              |
|--------|------------------------|------------------------|
| POST   | /api/auth/cadastrar    | Cadastrar novo usuário |
| POST   | /api/auth/login        | Login                  |
| POST   | /api/auth/logout       | Logoff                 |
| GET    | /api/auth/me           | Dados do usuário logado|

**Body — Cadastrar:**
```json
{
  "nomeCompleto": "João da Silva",
  "email": "joao@email.com",
  "senha": "123456",
  "cpfCnpj": "123.456.789-00",
  "dataNascimento": "1990-05-10",
  "tipoUsuario": "PRESTADOR",
  "telefone": "(11) 91234-5678",
  "cidade": "Barueri",
  "latitude": -23.508,
  "longitude": -46.876
}
```

**Body — Login:**
```json
{ "email": "joao@email.com", "senha": "123456" }
```

---

### 👥 Usuários (RF11)
| Método | Rota                          | Descrição          |
|--------|-------------------------------|--------------------|
| GET    | /api/usuarios/{id}            | Buscar usuário     |
| PUT    | /api/usuarios/{id}/perfil     | Atualizar perfil   |
| GET    | /api/usuarios/prestadores     | Listar prestadores |

---

### 🛠️ Serviços (RF03 · RF09)
| Método | Rota                                         | Descrição          |
|--------|----------------------------------------------|--------------------|
| GET    | /api/servicos                                | Listar todos       |
| GET    | /api/servicos?categoria=BRAÇAL               | Filtrar categoria  |
| GET    | /api/servicos/{id}                           | Buscar serviço     |
| POST   | /api/servicos/prestador/{prestadorId}        | Publicar serviço   |
| PUT    | /api/servicos/{id}/prestador/{prestadorId}   | Editar serviço     |
| DELETE | /api/servicos/{id}/prestador/{prestadorId}   | Remover serviço    |
| GET    | /api/servicos/prestador/{prestadorId}        | Serviços do prestador |

**Categorias:** `BRAÇAL` | `INTELECTUAL` | `ARTÍSTICO`

**Body — Publicar Serviço:**
```json
{
  "titulo": "Instalação Elétrica",
  "descricao": "Instalação e manutenção de circuitos elétricos",
  "categoria": "BRAÇAL",
  "precoBase": 150.00,
  "disponibilidade": "Seg-Sex 08:00-18:00"
}
```

---

### 🎯 Matching (RF05)
| Método | Rota                                         | Descrição                   |
|--------|----------------------------------------------|-----------------------------|
| GET    | /api/matching                                | Todos os prestadores        |
| GET    | /api/matching?categoria=BRAÇAL&lat=-23.5&lon=-46.8 | Por categoria + localização |

---

### 📋 Solicitações (RF04 · RF06 · RF07 · RF10 · RF12 · RF13 · RN03)
| Método | Rota                                              | Descrição                |
|--------|---------------------------------------------------|--------------------------|
| POST   | /api/solicitacoes/cliente/{clienteId}             | Criar solicitação        |
| PATCH  | /api/solicitacoes/{id}/aceitar/prestador/{id}     | Aceitar (com concorrência)|
| PATCH  | /api/solicitacoes/{id}/recusar/prestador/{id}     | Recusar                  |
| PATCH  | /api/solicitacoes/{id}/concluir/prestador/{id}    | Concluir (libera avaliação)|
| PATCH  | /api/solicitacoes/{id}/cancelar/usuario/{id}      | Cancelar (RN03: até 2h antes)|
| PATCH  | /api/solicitacoes/{id}/negociar                   | Negociar preço (RF10)    |
| GET    | /api/solicitacoes/cliente/{clienteId}             | Histórico cliente        |
| GET    | /api/solicitacoes/prestador/{prestadorId}         | Histórico prestador      |
| GET    | /api/solicitacoes/prestador/{prestadorId}/pendentes| Pendências do prestador  |

**Body — Criar Solicitação:**
```json
{
  "prestadorId": 3,
  "servicoId": 1,
  "horarioAgendado": "2026-05-25T14:00:00",
  "descricaoProblema": "Preciso instalar 3 tomadas novas",
  "enderecoServico": "Rua das Flores, 123 - Barueri"
}
```

---

### ⭐ Avaliações (RF08)
| Método | Rota                                                    | Descrição              |
|--------|---------------------------------------------------------|------------------------|
| POST   | /api/avaliacoes/solicitacao/{solId}/avaliador/{userId}  | Avaliar (pós-conclusão)|
| GET    | /api/avaliacoes/prestador/{prestadorId}                 | Ver avaliações         |

**Body — Avaliar:**
```json
{ "nota": 5, "comentario": "Excelente profissional, pontual e caprichoso!" }
```

---

## 🏗️ Arquitetura (MVC em Camadas)

```
Frontend (HTML5/CSS3)
        │ REST/JSON (HTTPS)
        ▼
┌─────────────────────────────────────┐
│  Camada de API (Controllers)        │
│  Auth · Usuario · Servico · Matching│
│  Solicitacao · Avaliacao            │
├─────────────────────────────────────┤
│  Camada de Negócio (Services)       │
│  + Matching Engine (Haversine)      │
│  + Controle de Concorrência         │
│  + Regras de Negócio (RN01~RN03)    │
├─────────────────────────────────────┤
│  Camada de Dados (Repositories)     │
│  Spring Data JPA + H2 Database      │
└─────────────────────────────────────┘
```

## ✅ Requisitos Implementados

| RF  | Descrição                  | Status |
|-----|----------------------------|--------|
| RF01| Cadastro de Usuário        | ✅     |
| RF02| Autenticação (sem JWT)     | ✅     |
| RF03| Publicação de Serviços     | ✅     |
| RF04| Solicitação de Serviço     | ✅     |
| RF05| Matching Automático        | ✅     |
| RF06| Aceitação do Serviço       | ✅     |
| RF07| Controle de Concorrência   | ✅ Optimistic Locking |
| RF08| Avaliação (1–5 ⭐)        | ✅     |
| RF09| Filtro por Categoria       | ✅     |
| RF10| Negociação de Preço        | ✅     |
| RF11| Gestão de Perfil           | ✅     |
| RF12| Histórico de Pedidos       | ✅     |
| RF13| Pagamento Simulado         | ✅     |
| RN01| Prestador 18+             | ✅     |
| RN02| Prestador com perfil/serviço| ✅    |
| RN03| Cancelamento até 2h antes  | ✅     |

---

## 🚀 Novidades do Frontend
- **Interface e Dashboard:** Melhoria na responsividade do layout e formatação dos valores de recebimentos ("Ganhos").
- **Avaliações Dinâmicas:** A visualização das avaliações do prestador agora reflete os dados vindos diretamente do banco de dados e calcula a média dinamicamente.
