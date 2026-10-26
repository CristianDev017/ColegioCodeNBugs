import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { DatosTarifa, Tarifa } from '../models/tarifa';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class TarifaService {
  private url = `${API_URL}/tarifas`;

  constructor(private http: HttpClient) {}

  listar(idCiclo: number, pagina: number, tamano: number): Observable<Pagina<Tarifa>> {
    return this.http.get<Pagina<Tarifa>>(this.url, { params: { idCiclo, pagina, tamano } });
  }

  obtener(id: number): Observable<Tarifa> {
    return this.http.get<Tarifa>(`${this.url}/${id}`);
  }

  crear(datos: DatosTarifa): Observable<Tarifa> {
    return this.http.post<Tarifa>(this.url, datos);
  }

  editar(id: number, datos: DatosTarifa): Observable<Tarifa> {
    return this.http.put<Tarifa>(`${this.url}/${id}`, datos);
  }
}