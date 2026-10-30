import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { FilaMora, FilaSolvencia, OpcionReporte } from '../models/reportes';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class ReportesService {
  constructor(private http: HttpClient) {}

  prestamosMora(buscar: string, tipo: string, pagina: number, tamano: number): Observable<Pagina<FilaMora>> {
    return this.http.get<Pagina<FilaMora>>(`${API_URL}/reportes/prestamos-mora`, {
      params: { buscar, tipo, pagina, tamano }
    });
  }

  pdfPrestamosMora(buscar: string, tipo: string): Observable<Blob> {
    return this.http.get(`${API_URL}/reportes/prestamos-mora/pdf`, {
      params: { buscar, tipo },
      responseType: 'blob'
    });
  }

  ciclos(): Observable<OpcionReporte[]> {
    return this.http.get<OpcionReporte[]>(`${API_URL}/reportes/opciones/ciclos`);
  }

  grados(): Observable<OpcionReporte[]> {
    return this.http.get<OpcionReporte[]>(`${API_URL}/reportes/opciones/grados`);
  }

  secciones(idGrado: number): Observable<OpcionReporte[]> {
    return this.http.get<OpcionReporte[]>(`${API_URL}/reportes/opciones/secciones`, { params: { idGrado } });
  }

  estudiantes(): Observable<OpcionReporte[]> {
    return this.http.get<OpcionReporte[]>(`${API_URL}/reportes/opciones/estudiantes`);
  }

  solventes(filtros: any, pagina: number, tamano: number): Observable<Pagina<FilaSolvencia>> {
    return this.http.get<Pagina<FilaSolvencia>>(`${API_URL}/reportes/solventes`, {
      params: { ...filtros, pagina, tamano }
    });
  }

  pdfSolventes(filtros: any): Observable<Blob> {
    return this.http.get(`${API_URL}/reportes/solventes/pdf`, { params: filtros, responseType: 'blob' });
  }
}