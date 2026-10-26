import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { DatosPago, EstadoCuenta, Pago } from '../models/pago';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class PagoService {
  private url = `${API_URL}/pagos`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, idCiclo: number, pagina: number, tamano: number): Observable<Pagina<Pago>> {
    return this.http.get<Pagina<Pago>>(this.url, { params: { buscar, idCiclo, pagina, tamano } });
  }

  obtener(id: number): Observable<Pago> {
    return this.http.get<Pago>(`${this.url}/${id}`);
  }

  estadoCuenta(idEstudiante: number, idCiclo: number): Observable<EstadoCuenta> {
    return this.http.get<EstadoCuenta>(`${this.url}/estado-cuenta`, { params: { idEstudiante, idCiclo } });
  }

  registrar(datos: DatosPago): Observable<Pago> {
    return this.http.post<Pago>(this.url, datos);
  }
}