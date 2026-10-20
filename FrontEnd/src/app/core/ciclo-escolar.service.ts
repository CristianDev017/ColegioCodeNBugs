import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CicloEscolar, DatosCiclo } from '../models/ciclo-escolar';
import { Pagina } from '../models/pagina';

@Injectable({ providedIn: 'root' })
export class CicloEscolarService {
  private url = '/api/ciclos';

  constructor(private http: HttpClient) {}

  listar(pagina: number, tamano: number): Observable<Pagina<CicloEscolar>> {
    return this.http.get<Pagina<CicloEscolar>>(this.url, { params: { pagina, tamano } });
  }

  crear(datos: DatosCiclo): Observable<CicloEscolar> {
    return this.http.post<CicloEscolar>(this.url, datos);
  }

  editar(id: number, datos: DatosCiclo): Observable<CicloEscolar> {
    return this.http.put<CicloEscolar>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<CicloEscolar> {
    return this.http.put<CicloEscolar>(`${this.url}/${id}/activar`, {});
  }

  cerrar(id: number): Observable<CicloEscolar> {
    return this.http.put<CicloEscolar>(`${this.url}/${id}/cerrar`, {});
  }
}