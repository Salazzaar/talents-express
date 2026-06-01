/**
 * Talents Express — API Client & Utilities
 * Centraliza toda comunicação com o backend Spring Boot
 */

const API_BASE = 'http://localhost:8080/api';

// ─── AUTH TOKEN ─────────────────────────────────────────────────────────────
const Auth = {
  save(user)  { localStorage.setItem('te_user', JSON.stringify(user)); },
  get()       { try { return JSON.parse(localStorage.getItem('te_user')); } catch { return null; } },
  clear()     { localStorage.removeItem('te_user'); },
  token()     { return Auth.get()?.token || ''; },
  isLogged()  { return !!Auth.get(); },
  isPrestador(){ return Auth.get()?.perfil === 'PRESTADOR'; },
  isCliente() { return Auth.get()?.perfil === 'CLIENTE'; },
  requireLogin(redirect = 'login.html') {
    if (!Auth.isLogged()) { window.location.href = redirect; return false; }
    return true;
  }
};

// ─── HTTP HELPERS ────────────────────────────────────────────────────────────
async function apiFetch(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(Auth.token() ? { 'Authorization': `Bearer ${Auth.token()}` } : {}),
    ...(options.headers || {})
  };
  try {
    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
    const data = res.status !== 204 ? await res.json().catch(() => ({})) : {};
    if (!res.ok) throw new ApiError(data.message || `Erro ${res.status}`, res.status, data);
    return data;
  } catch (e) {
    if (e instanceof ApiError) throw e;
    throw new ApiError('Falha de conexão com o servidor. Verifique se o backend está ativo.', 0);
  }
}

class ApiError extends Error {
  constructor(message, status, data = {}) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

const api = {
  get:    (path)         => apiFetch(path, { method: 'GET' }),
  post:   (path, body)   => apiFetch(path, { method: 'POST',   body: JSON.stringify(body) }),
  put:    (path, body)   => apiFetch(path, { method: 'PUT',    body: JSON.stringify(body) }),
  delete: (path)         => apiFetch(path, { method: 'DELETE' }),
  patch:  (path, body)   => apiFetch(path, { method: 'PATCH',  body: JSON.stringify(body) }),
};

// ─── ENDPOINTS ───────────────────────────────────────────────────────────────
const AuthAPI = {
  register: (data)  => api.post('/auth/register', data),
  login:    (data)  => api.post('/auth/login', data),
};

const UserAPI = {
  me:           ()      => api.get('/users/me'),
  update:       (data)  => api.put('/users/me', data),
};

const ServicoAPI = {
  listar:       ()         => api.get('/servicos'),
  meus:         ()         => api.get('/servicos/meus'),
  buscar:       (params)   => api.get(`/servicos/buscar?${new URLSearchParams(params)}`),
  criar:        (data)     => api.post('/servicos', data),
  atualizar:    (id, data) => api.put(`/servicos/${id}`, data),
  excluir:      (id)       => api.delete(`/servicos/${id}`),
  detalhes:     (id)       => api.get(`/servicos/${id}`),
};

const SolicitacaoAPI = {
  criar:         (data) => api.post('/solicitacoes', data),
  listarCliente: ()     => api.get('/solicitacoes/cliente'),
  listarPrestador: ()   => api.get('/solicitacoes/prestador'),
  aceitar:       (id)   => api.patch(`/solicitacoes/${id}/aceitar`, {}),
  recusar:       (id)   => api.patch(`/solicitacoes/${id}/recusar`, {}),
  cancelar:      (id)   => api.patch(`/solicitacoes/${id}/cancelar`, {}),
  concluir:      (id)   => api.patch(`/solicitacoes/${id}/concluir`, {}),
  detalhes:      (id)   => api.get(`/solicitacoes/${id}`),
};

const AvaliacaoAPI = {
  criar:   (data) => api.post('/avaliacoes', data),
  buscar:  (prestadorId) => api.get(`/avaliacoes/prestador/${prestadorId}`),
};

const PagamentoAPI = {
  iniciar: (data) => api.post('/pagamentos/iniciar', data),
  status:  (id)   => api.get(`/pagamentos/${id}/status`),
};

// ─── MOCK DATA (fallback quando backend offline) ──────────────────────────────
const MOCK = {
  prestadores: [
    { id: 1, nome: 'Carlos Oliveira', categoria: 'Braçal', servico: 'Eletricista Residencial', preco: 120, avaliacao: 4.8, avaliacoes: 47, bairro: 'Alphaville', disponivel: true, descricao: 'Mais de 10 anos de experiência em instalações elétricas residenciais e comerciais.' },
    { id: 2, nome: 'Maria Santos',    categoria: 'Intelectual', servico: 'Aulas de Matemática', preco: 80, avaliacao: 4.9, avaliacoes: 132, bairro: 'Barueri Centro', disponivel: true, descricao: 'Professora formada pela USP, especialista em preparação para vestibulares.' },
    { id: 3, nome: 'Lucas Ferreira',  categoria: 'Artístico', servico: 'Designer Gráfico', preco: 150, avaliacao: 4.7, avaliacoes: 28, bairro: 'Tamboré', disponivel: true, descricao: 'Design de identidade visual, materiais gráficos e mídias digitais.' },
    { id: 4, nome: 'Ana Costa',       categoria: 'Braçal', servico: 'Diarista',  preco: 160, avaliacao: 4.6, avaliacoes: 89, bairro: 'Aldeia da Serra', disponivel: true, descricao: 'Limpeza e organização residencial com produtos próprios e equipamentos.' },
    { id: 5, nome: 'Pedro Lima',      categoria: 'Intelectual', servico: 'Contador Autônomo', preco: 200, avaliacao: 4.5, avaliacoes: 34, bairro: 'Alphaville', disponivel: false, descricao: 'Declaração IR, contabilidade para MEI e pequenas empresas.' },
    { id: 6, nome: 'Juliana Rocha',   categoria: 'Artístico', servico: 'Fotógrafa', preco: 350, avaliacao: 5.0, avaliacoes: 61, bairro: 'Barueri Centro', disponivel: true, descricao: 'Fotografia de eventos, ensaios pessoais e corporativos.' },
  ],
  solicitacoes: [
    { id: 1, cliente: 'Roberto Alves', servico: 'Instalação de tomadas', categoria: 'Braçal', dataHora: '2026-05-20T14:00:00', local: 'Rua das Flores, 123 - Alphaville', status: 'PENDENTE', valor: 120 },
    { id: 2, cliente: 'Fernanda Silva', servico: 'Manutenção elétrica', categoria: 'Braçal', dataHora: '2026-05-22T09:00:00', local: 'Av. Itapevi, 500 - Barueri', status: 'PENDENTE', valor: 90 },
  ],
};

// ─── UI UTILITIES ────────────────────────────────────────────────────────────
const UI = {
  toast(msg, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }
    const icons = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type]}</span><span>${msg}</span>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4100);
  },

  loading(show, msg = 'Carregando...') {
    let overlay = document.getElementById('loading-overlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'loading-overlay';
      overlay.className = 'loading-overlay';
      overlay.innerHTML = `<div class="spinner"></div><p>${msg}</p>`;
      document.body.appendChild(overlay);
    }
    overlay.querySelector('p').textContent = msg;
    overlay.classList.toggle('open', show);
  },

  modal: {
    open(id)  { document.getElementById(id)?.classList.add('open'); },
    close(id) { document.getElementById(id)?.classList.remove('open'); },
  },

  stars(rating, container, interactive = false) {
    container.innerHTML = '';
    for (let i = 1; i <= 5; i++) {
      const s = document.createElement('span');
      s.className = `star${i <= rating ? ' active' : ''}`;
      s.textContent = '★';
      if (interactive) {
        s.addEventListener('click', () => {
          container.dataset.value = i;
          container.querySelectorAll('.star').forEach((st, idx) => {
            st.classList.toggle('active', idx < i);
          });
        });
      }
      container.appendChild(s);
    }
  },

  formatCurrency(v) { return `R$ ${Number(v).toFixed(2).replace('.', ',')}`; },
  formatDate(d)  { return new Date(d).toLocaleDateString('pt-BR'); },
  formatDateTime(d) { return new Date(d).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }); },
  initials(name) { return name?.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2) || '??'; },

  getStatusBadge(status) {
    const map = {
      PENDENTE: ['warning', '⏳ Pendente'],
      ACEITO:   ['success', '✅ Aceito'],
      RECUSADO: ['danger',  '❌ Recusado'],
      CANCELADO:['danger',  '🚫 Cancelado'],
      CONCLUIDO:['primary', '🏆 Concluído'],
      AGUARDANDO_PAGAMENTO: ['warning', '💳 Aguard. Pagamento'],
    };
    const [cls, label] = map[status] || ['primary', status];
    return `<span class="badge badge-${cls}">${label}</span>`;
  },

  renderNavbar(activePage) {
    const user = Auth.get();
    const isPrestador = user?.perfil === 'PRESTADOR';
    const navLinks = user
      ? isPrestador
        ? [
            { href: 'dashboard-prestador.html', label: '🏠 Painel' },
            { href: 'portfolio.html', label: '🛠️ Meus Serviços' },
            { href: 'atendimentos.html', label: '📋 Chamados' },
          ]
        : [
            { href: 'dashboard-cliente.html', label: '🏠 Início' },
            { href: 'busca.html', label: '🔍 Buscar' },
            { href: 'meus-pedidos.html', label: '📦 Meus Pedidos' },
          ]
      : [];

    const nav = document.querySelector('.navbar');
    if (!nav) return;
    const linksHtml = navLinks.map(l =>
      `<a href="${l.href}" class="${activePage === l.href ? 'active' : ''}">${l.label}</a>`
    ).join('');
    const userHtml = user
      ? `<div style="display:flex;align-items:center;gap:0.75rem">
           <span style="font-size:0.85rem;color:var(--text-secondary)">${user.nome?.split(' ')[0] || 'Usuário'}</span>
           <button onclick="Auth.clear();window.location.href='login.html'" class="btn btn-outline btn-sm">Sair</button>
         </div>`
      : `<a href="login.html" class="btn btn-primary btn-sm">Entrar</a>`;

    nav.innerHTML = `
      <a href="${user ? (isPrestador ? 'dashboard-prestador.html' : 'dashboard-cliente.html') : 'index.html'}" class="navbar-brand">
        <div class="logo-icon">⚡</div> Talents Express
      </a>
      <div class="navbar-links">${linksHtml}${userHtml}</div>`;
  }
};

// Expõe globalmente
window.Auth = Auth;
window.api = api;
window.AuthAPI = AuthAPI;
window.UserAPI = UserAPI;
window.ServicoAPI = ServicoAPI;
window.SolicitacaoAPI = SolicitacaoAPI;
window.AvaliacaoAPI = AvaliacaoAPI;
window.PagamentoAPI = PagamentoAPI;
window.MOCK = MOCK;
window.UI = UI;
