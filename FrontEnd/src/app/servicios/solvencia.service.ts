import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { DatosSolvencia, Solvencia, VerificacionSolvencia } from '../models/solvencia';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class SolvenciaService {
  private url = `${API_URL}/solvencias`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, idCiclo: number, pagina: number, tamano: number): Observable<Pagina<Solvencia>> {
    return this.http.get<Pagina<Solvencia>>(this.url, { params: { buscar, idCiclo, pagina, tamano } });
  }

  obtener(id: number): Observable<Solvencia> {
    return this.http.get<Solvencia>(`${this.url}/${id}`);
  }

  verificar(idEstudiante: number, idCiclo: number): Observable<VerificacionSolvencia> {
    return this.http.get<VerificacionSolvencia>(`${this.url}/verificar`, { params: { idEstudiante, idCiclo } });
  }

  emitir(datos: DatosSolvencia): Observable<Solvencia> {
    return this.http.post<Solvencia>(this.url, datos);
  }
}