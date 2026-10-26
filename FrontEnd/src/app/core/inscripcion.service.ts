import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Inscripcion, SeccionDisponible } from '../models/inscripcion';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class InscripcionService {
  private url = `${API_URL}/inscripciones`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, idSeccion: number, pagina: number, tamano: number): Observable<Pagina<Inscripcion>> {
    return this.http.get<Pagina<Inscripcion>>(this.url, { params: { buscar, idSeccion, pagina, tamano } });
  }

  seccionesDisponibles(): Observable<SeccionDisponible[]> {
    return this.http.get<SeccionDisponible[]>(`${this.url}/secciones`);
  }

  inscribir(idEstudiante: number, idSeccion: number): Observable<Inscripcion> {
    return this.http.post<Inscripcion>(this.url, { idEstudiante, idSeccion });
  }
}