import { AsyncPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-inicio',
  imports: [AsyncPipe],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss'
})
export class Inicio {
  mensaje = '';
  esError = false;

  constructor(public auth: AuthService, private http: HttpClient) {}

  probarToken(): void {
    this.http.get<{ username: string; rol: string }>('/api/auth/me').subscribe({
      next: (r) => this.mostrar('Token válido: ' + r.username + ' (' + r.rol + ')', false),
      error: (e) => this.mostrar(e.error?.mensaje ?? 'No se pudo conectar', true)
    });
  }

  probarAdmin(): void {
    this.http.get<{ mensaje: string }>('/api/admin/ping').subscribe({
      next: (r) => this.mostrar(r.mensaje, false),
      error: (e) => this.mostrar(e.error?.mensaje ?? 'No se pudo conectar', true)
    });
  }

  cerrarSesion(): void {
    this.auth.logout();
  }

  private mostrar(texto: string, esError: boolean): void {
    this.mensaje = texto;
    this.esError = esError;
  }
}