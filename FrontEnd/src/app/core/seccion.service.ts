import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { DatosSeccion, Seccion } from '../models/seccion';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class SeccionService {
  private url = `${API_URL}/secciones`;

  constructor(private http: HttpClient) {}

  listar(idGrado: number, pagina: number, tamano: number): Observable<Pagina<Seccion>> {
    return this.http.get<Pagina<Seccion>>(this.url, { params: { idGrado, pagina, tamano } });
  }

  crear(datos: DatosSeccion): Observable<Seccion> {
    return this.http.post<Seccion>(this.url, datos);
  }

  editar(id: number, datos: DatosSeccion): Observable<Seccion> {
    return this.http.put<Seccion>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Seccion> {
    return this.http.put<Seccion>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Seccion> {
    return this.http.put<Seccion>(`${this.url}/${id}/desactivar`, {});
  }
}