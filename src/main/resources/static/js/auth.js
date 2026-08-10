/* =============================================
   CONCORDE AIRLINES — Sistema de Autenticación
   Roles: Administrador, Agente y Cliente
   Ahora valida contra la base de datos real (tabla usuario)
   a través de la API en http://localhost:8082
   ============================================= */

'use strict';

const AUTH_API = 'http://localhost:8082/api';

function inicialesDe(nombreCompleto) {
  return (nombreCompleto || '')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map(p => p[0].toUpperCase())
    .join('');
}

// Página de destino según el rol, usada al iniciar sesión y en las redirecciones
function paginaSegunRol(rol) {
  if (rol === 'administrador') return 'admin-panel.html';
  if (rol === 'agente') return 'agente-panel.html';
  return 'index.html';
}

// ── SESIÓN ────────────────────────────────────
const Auth = {

  /** Intenta iniciar sesión contra la API real. Retorna una Promise
   *  que resuelve con el usuario si las credenciales son correctas,
   *  o null si no lo son (o si no se pudo conectar con la API). */
  async login(email, password) {
    try {
      const res = await fetch(`${AUTH_API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correoElectronico: email, contrasena: password })
      });
      if (!res.ok) return null;

      const datos = await res.json();
      const user = {
        id: datos.idUsuario,
        nombre: datos.nombreCompleto,
        email: datos.correoElectronico,
        // ADMIN -> administrador ; CLIENTE/AGENTE -> cliente
        rol: datos.rol === 'ADMIN' ? 'administrador' : (datos.rol === 'AGENTE' ? 'agente' : 'cliente'),
        avatar: inicialesDe(datos.nombreCompleto),
        ultimoAcceso: new Date().toLocaleString('es-CO')
      };
      sessionStorage.setItem('concorde_user', JSON.stringify(user));
      return user;
    } catch (e) {
      console.error('No se pudo conectar con la API de login:', e);
      return null;
    }
  },

  /** Cierra la sesión */
  logout() {
    sessionStorage.removeItem('concorde_user');
    window.location.href = 'p03-login.html';
  },

  /** Retorna el usuario logueado o null */
  getUsuario() {
    const raw = sessionStorage.getItem('concorde_user');
    return raw ? JSON.parse(raw) : null;
  },

  /** Verifica si hay sesión activa */
  estaLogueado() {
    return !!this.getUsuario();
  },

  /** Protege páginas que requieren rol específico */
  requiereRol(rol) {
    const user = this.getUsuario();
    if (!user) { window.location.href = 'p03-login.html'; return false; }
    if (user.rol !== rol) {
      window.location.href = paginaSegunRol(user.rol);
      return false;
    }
    return true;
  },

  /** Redirige si ya está logueado (para la página de login) */
  redirigirSiLogueado() {
    const user = this.getUsuario();
    if (user) {
      window.location.href = paginaSegunRol(user.rol);
    }
  }
};
