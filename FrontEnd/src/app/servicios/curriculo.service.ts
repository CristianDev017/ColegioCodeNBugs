import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './rest-constants';
import { Curriculo } from '../models/curriculo';

@Injectable({ providedIn: 'root' })
export class CurriculoService {
  private url = `${API_URL}/curriculos`;

  constructor(private http: HttpClient) {}

  consultar(idCiclo: number, idGrado: number): Observable<Curriculo> {
    return this.http.get<Curriculo>(this.url, { params: { ciclo: idCiclo, grado: idGrado } });
  }

  agregarCurso(idCiclo: number, idGrado: number, idCurso: number): Observable<Curriculo> {
    return this.http.post<Curriculo>(`${this.url}/cursos`, { idCiclo, idGrado, idCurso });
  }

  quitarCurso(idCurriculo: number, idCurso: number): Observable<Curriculo> {
    return this.http.delete<Curriculo>(`${this.url}/${idCurriculo}/cursos/${idCurso}`);
  }
}