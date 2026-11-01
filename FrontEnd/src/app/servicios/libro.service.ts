import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DatosLibro, Libro } from '../models/libro';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class LibroService {
  private url = `${API_URL}/libros`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, estado: string, pagina: number, tamano: number): Observable<Pagina<Libro>> {
    return this.http.get<Pagina<Libro>>(this.url, { params: { buscar, estado, pagina, tamano } });
  }

  obtener(id: number): Observable<Libro> {
    return this.http.get<Libro>(`${this.url}/${id}`);
  }

  crear(datos: DatosLibro): Observable<Libro> {
    return this.http.post<Libro>(this.url, datos);
  }

  editar(id: number, datos: DatosLibro): Observable<Libro> {
    return this.http.put<Libro>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Libro> {
    return this.http.put<Libro>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Libro> {
    return this.http.put<Libro>(`${this.url}/${id}/desactivar`, {});
  }
}