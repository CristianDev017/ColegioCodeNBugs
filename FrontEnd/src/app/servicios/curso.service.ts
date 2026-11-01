import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './rest-constants';
import { Curso } from '../models/curso';
import { Pagina } from '../models/pagina';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private url = `${API_URL}/cursos`;

  constructor(private http: HttpClient) {}

  listar(pagina: number, tamano: number): Observable<Pagina<Curso>> {
    return this.http.get<Pagina<Curso>>(`${this.url}?pagina=${pagina}&tamano=${tamano}`);
  }

  listarActivos(): Observable<Curso[]> {
    return this.http.get<Curso[]>(`${this.url}/activos`);
  }

  crear(datos: { nombre: string; descripcion: string | null }): Observable<Curso> {
    return this.http.post<Curso>(this.url, datos);
  }

  editar(id: number, datos: { nombre: string; descripcion: string | null }): Observable<Curso> {
    return this.http.put<Curso>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Curso> {
    return this.http.put<Curso>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Curso> {
    return this.http.put<Curso>(`${this.url}/${id}/desactivar`, {});
  }
}