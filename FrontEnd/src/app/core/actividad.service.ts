import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Actividad, DatosActividad } from '../models/actividad';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class ActividadService {
  private url = `${API_URL}/actividades`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, pagina: number, tamano: number): Observable<Pagina<Actividad>> {
    return this.http.get<Pagina<Actividad>>(this.url, { params: { buscar, pagina, tamano } });
  }

  listarActivas(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(`${this.url}/activas`);
  }

  obtener(id: number): Observable<Actividad> {
    return this.http.get<Actividad>(`${this.url}/${id}`);
  }

  crear(datos: DatosActividad): Observable<Actividad> {
    return this.http.post<Actividad>(this.url, datos);
  }

  editar(id: number, datos: DatosActividad): Observable<Actividad> {
    return this.http.put<Actividad>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Actividad> {
    return this.http.put<Actividad>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Actividad> {
    return this.http.put<Actividad>(`${this.url}/${id}/desactivar`, {});
  }
}