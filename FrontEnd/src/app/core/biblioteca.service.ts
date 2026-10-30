import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ConfigBiblioteca, DatosPrestamo, Multa, OpcionBiblioteca, Prestamo, ResultadoDevolucion } from '../models/biblioteca';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class BibliotecaService {
  constructor(private http: HttpClient) {}

  config(): Observable<ConfigBiblioteca> {
    return this.http.get<ConfigBiblioteca>(`${API_URL}/biblioteca-config`);
  }

  guardarConfig(datos: ConfigBiblioteca): Observable<ConfigBiblioteca> {
    return this.http.put<ConfigBiblioteca>(`${API_URL}/biblioteca-config`, datos);
  }

  prestamos(buscar: string, estado: string, pagina: number, tamano: number): Observable<Pagina<Prestamo>> {
    return this.http.get<Pagina<Prestamo>>(`${API_URL}/prestamos`, { params: { buscar, estado, pagina, tamano } });
  }

  opcionesLibros(buscar: string): Observable<OpcionBiblioteca[]> {
    return this.http.get<OpcionBiblioteca[]>(`${API_URL}/prestamos/opciones/libros`, { params: { buscar } });
  }

  opcionesSolicitantes(tipo: string, buscar: string): Observable<OpcionBiblioteca[]> {
    return this.http.get<OpcionBiblioteca[]>(`${API_URL}/prestamos/opciones/solicitantes`, { params: { tipo, buscar } });
  }

  crearPrestamo(datos: DatosPrestamo): Observable<Prestamo> {
    return this.http.post<Prestamo>(`${API_URL}/prestamos`, datos);
  }

  devolver(id: number, observacion: string): Observable<ResultadoDevolucion> {
    return this.http.put<ResultadoDevolucion>(`${API_URL}/prestamos/${id}/devolver`, { observacion });
  }

  multas(buscar: string, estado: string, pagina: number, tamano: number): Observable<Pagina<Multa>> {
    return this.http.get<Pagina<Multa>>(`${API_URL}/multas`, { params: { buscar, estado, pagina, tamano } });
  }

  pagarMulta(id: number): Observable<Multa> {
    return this.http.put<Multa>(`${API_URL}/multas/${id}/pagar`, {});
  }
}
