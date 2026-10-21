import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Carrera, DatosCarrera } from '../models/carrera';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class CarreraService {
  private url = `${API_URL}/carreras`;

  constructor(private http: HttpClient) {}

  listar(pagina: number, tamano: number): Observable<Pagina<Carrera>> {
    return this.http.get<Pagina<Carrera>>(this.url, { params: { pagina, tamano } });
  }

  listarActivas(): Observable<Carrera[]> {
    return this.http.get<Carrera[]>(`${this.url}/activas`);
  }

  crear(datos: DatosCarrera): Observable<Carrera> {
    return this.http.post<Carrera>(this.url, datos);
  }

  editar(id: number, datos: DatosCarrera): Observable<Carrera> {
    return this.http.put<Carrera>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Carrera> {
    return this.http.put<Carrera>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Carrera> {
    return this.http.put<Carrera>(`${this.url}/${id}/desactivar`, {});
  }
}