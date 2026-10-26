import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DatosEstudiante, Estudiante } from '../models/estudiante';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class EstudianteService {
  private url = `${API_URL}/estudiantes`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, estado: string, pagina: number, tamano: number): Observable<Pagina<Estudiante>> {
    return this.http.get<Pagina<Estudiante>>(this.url, { params: { buscar, estado, pagina, tamano } });
  }

  obtener(id: number): Observable<Estudiante> {
    return this.http.get<Estudiante>(`${this.url}/${id}`);
  }

  crear(datos: DatosEstudiante): Observable<Estudiante> {
    return this.http.post<Estudiante>(this.url, datos);
  }

  editar(id: number, datos: DatosEstudiante): Observable<Estudiante> {
    return this.http.put<Estudiante>(`${this.url}/${id}`, datos);
  }

  cambiarEstado(id: number, estado: string): Observable<Estudiante> {
    return this.http.put<Estudiante>(`${this.url}/${id}/estado`, { estado });
  }
}