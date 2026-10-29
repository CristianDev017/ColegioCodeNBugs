import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Asignacion, CursoSeccion, DatosAsignacion, OpcionAsignacion } from '../models/asignacion';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class AsignacionService {
  private url = `${API_URL}/asignaciones`;

  constructor(private http: HttpClient) {}

  listar(idEmpleado: number, idSeccion: number, pagina: number, tamano: number): Observable<Pagina<Asignacion>> {
    return this.http.get<Pagina<Asignacion>>(this.url, { params: { idEmpleado, idSeccion, pagina, tamano } });
  }

  obtener(id: number): Observable<Asignacion> {
    return this.http.get<Asignacion>(`${this.url}/${id}`);
  }

  maestros(): Observable<OpcionAsignacion[]> {
    return this.http.get<OpcionAsignacion[]>(`${this.url}/maestros`);
  }

  secciones(): Observable<OpcionAsignacion[]> {
    return this.http.get<OpcionAsignacion[]>(`${this.url}/secciones`);
  }

  cursos(idSeccion: number): Observable<CursoSeccion[]> {
    return this.http.get<CursoSeccion[]>(`${this.url}/cursos`, { params: { idSeccion } });
  }

  crear(datos: DatosAsignacion): Observable<Asignacion> {
    return this.http.post<Asignacion>(this.url, datos);
  }

  cambiarMaestro(id: number, datos: DatosAsignacion): Observable<Asignacion> {
    return this.http.put<Asignacion>(`${this.url}/${id}`, datos);
  }

  quitar(id: number): Observable<{ mensaje: string }> {
    return this.http.delete<{ mensaje: string }>(`${this.url}/${id}`);
  }
}