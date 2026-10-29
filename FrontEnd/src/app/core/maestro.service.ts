import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AlumnoNomina,
  CursoMaestro,
  DatosPonderacion,
  PaginaPonderaciones,
  Ponderacion,
  RegistroAsistencia,
} from '../models/maestro';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class MaestroService {
  private url = `${API_URL}/maestro/cursos`;

  constructor(private http: HttpClient) {}

  cursos(pagina: number, tamano: number): Observable<Pagina<CursoMaestro>> {
    return this.http.get<Pagina<CursoMaestro>>(this.url, { params: { pagina, tamano } });
  }

  curso(idAsignacion: number): Observable<CursoMaestro> {
    return this.http.get<CursoMaestro>(`${this.url}/${idAsignacion}`);
  }

  nomina(
    idAsignacion: number,
    fecha: string,
    pagina: number,
    tamano: number,
  ): Observable<Pagina<AlumnoNomina>> {
    const params = new HttpParams().set('fecha', fecha).set('pagina', pagina).set('tamano', tamano);
    return this.http.get<Pagina<AlumnoNomina>>(`${this.url}/${idAsignacion}/nomina`, { params });
  }

  guardarAsistencia(
    idAsignacion: number,
    fecha: string,
    registros: RegistroAsistencia[],
  ): Observable<{ mensaje: string }> {
    return this.http.post<{ mensaje: string }>(`${this.url}/${idAsignacion}/asistencia`, {
      fecha,
      registros,
    });
  }
  ponderaciones(
    idAsignacion: number,
    pagina: number,
    tamano: number,
  ): Observable<PaginaPonderaciones> {
    return this.http.get<PaginaPonderaciones>(`${this.url}/${idAsignacion}/ponderaciones`, {
      params: { pagina, tamano },
    });
  }

  ponderacion(idAsignacion: number, idPonderacion: number): Observable<Ponderacion> {
    return this.http.get<Ponderacion>(`${this.url}/${idAsignacion}/ponderaciones/${idPonderacion}`);
  }

  crearPonderacion(idAsignacion: number, datos: DatosPonderacion): Observable<Ponderacion> {
    return this.http.post<Ponderacion>(`${this.url}/${idAsignacion}/ponderaciones`, datos);
  }

  editarPonderacion(
    idAsignacion: number,
    idPonderacion: number,
    datos: DatosPonderacion,
  ): Observable<Ponderacion> {
    return this.http.put<Ponderacion>(
      `${this.url}/${idAsignacion}/ponderaciones/${idPonderacion}`,
      datos,
    );
  }

  eliminarPonderacion(
    idAsignacion: number,
    idPonderacion: number,
  ): Observable<{ mensaje: string }> {
    return this.http.delete<{ mensaje: string }>(
      `${this.url}/${idAsignacion}/ponderaciones/${idPonderacion}`,
    );
  }
}
