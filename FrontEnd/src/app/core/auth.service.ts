import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface UsuarioSesion {
  id: number;
  username: string;
  rol: string;
}

interface LoginRespuesta {
  token: string;
  expiraEnMinutos: number;
  usuario: UsuarioSesion;
}

const CLAVE_TOKEN = 'colegio_token';
const CLAVE_USUARIO = 'colegio_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private usuarioSubject = new BehaviorSubject<UsuarioSesion | null>(this.leerUsuarioGuardado());
  usuario$: Observable<UsuarioSesion | null> = this.usuarioSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<LoginRespuesta> {
    return this.http.post<LoginRespuesta>('/api/auth/login', { username, password }).pipe(
      tap((respuesta) => {
        localStorage.setItem(CLAVE_TOKEN, respuesta.token);
        localStorage.setItem(CLAVE_USUARIO, JSON.stringify(respuesta.usuario));
        this.usuarioSubject.next(respuesta.usuario);
      })
    );
  }

  logout(irALogin: boolean = true): void {
    localStorage.removeItem(CLAVE_TOKEN);
    localStorage.removeItem(CLAVE_USUARIO);
    this.usuarioSubject.next(null);
    if (irALogin) {
      this.router.navigate(['/login']);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(CLAVE_TOKEN);
  }

  estaLogueado(): boolean {
    const token = this.getToken();
    return token !== null && !this.tokenExpirado(token);
  }

  getUsuario(): UsuarioSesion | null {
    return this.usuarioSubject.value;
  }

  tieneRol(roles: string[]): boolean {
    const usuario = this.getUsuario();
    return usuario !== null && roles.includes(usuario.rol);
  }

  private tokenExpirado(token: string): boolean {
    try {
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(payload)
          .split('')
          .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
          .join('')
      );
      const exp = JSON.parse(json).exp as number;
      return exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private leerUsuarioGuardado(): UsuarioSesion | null {
    try {
      const texto = localStorage.getItem(CLAVE_USUARIO);
      return texto ? (JSON.parse(texto) as UsuarioSesion) : null;
    } catch {
      return null;
    }
  }
}