import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Pagina } from '../models/pagina';
import { DatosUsuarioAdmin, UsuarioAdmin } from '../models/usuario-admin';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class UsuarioAdminService {

private url = `${API_URL}/admin/usuarios`;

  constructor(private http: HttpClient) {}

  listar(pagina: number, tamano: number, buscar: string): Observable<Pagina<UsuarioAdmin>> {
    const params = new HttpParams()
      .set('pagina', pagina)
      .set('tamano', tamano)
      .set('buscar', buscar);
    return this.http.get<Pagina<UsuarioAdmin>>(this.url, { params });
  }

  crear(datos: DatosUsuarioAdmin): Observable<UsuarioAdmin> {
    return this.http.post<UsuarioAdmin>(this.url, datos);
  }

  editar(id: number, datos: DatosUsuarioAdmin): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(this.url + '/' + id, datos);
  }

  desactivar(id: number): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(this.url + '/' + id + '/desactivar', {});
  }

  activar(id: number): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(this.url + '/' + id + '/activar', {});
  }
}