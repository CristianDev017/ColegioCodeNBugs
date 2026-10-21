import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DatosGrado, Grado } from '../models/grado';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';


@Injectable({ providedIn: 'root' })
export class GradoService {
private url = `${API_URL}/grados`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, idNivel: number, pagina: number, tamano: number): Observable<Pagina<Grado>> {
    return this.http.get<Pagina<Grado>>(this.url, { params: { buscar, idNivel, pagina, tamano } });
  }

  crear(datos: DatosGrado): Observable<Grado> {
    return this.http.post<Grado>(this.url, datos);
  }

  editar(id: number, datos: DatosGrado): Observable<Grado> {
    return this.http.put<Grado>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Grado> {
    return this.http.put<Grado>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Grado> {
    return this.http.put<Grado>(`${this.url}/${id}/desactivar`, {});
  }
}