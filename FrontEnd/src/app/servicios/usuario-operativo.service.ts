import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pagina } from '../models/pagina';
import { DatosUsuarioOperativo, OpcionEmpleado, UsuarioOperativo } from '../models/usuario-operativo';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class UsuarioOperativoService {
  private url = `${API_URL}/usuarios-operativos`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, rol: string, estado: string, pagina: number, tamano: number): Observable<Pagina<UsuarioOperativo>> {
    return this.http.get<Pagina<UsuarioOperativo>>(this.url, { params: { buscar, rol, estado, pagina, tamano } });
  }

  obtener(id: number): Observable<UsuarioOperativo> {
    return this.http.get<UsuarioOperativo>(`${this.url}/${id}`);
  }

  empleadosDisponibles(rol: string): Observable<OpcionEmpleado[]> {
    return this.http.get<OpcionEmpleado[]>(`${this.url}/empleados`, { params: { rol } });
  }

  crear(datos: DatosUsuarioOperativo): Observable<UsuarioOperativo> {
    return this.http.post<UsuarioOperativo>(this.url, datos);
  }

  editar(id: number, datos: DatosUsuarioOperativo): Observable<UsuarioOperativo> {
    return this.http.put<UsuarioOperativo>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<UsuarioOperativo> {
    return this.http.put<UsuarioOperativo>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<UsuarioOperativo> {
    return this.http.put<UsuarioOperativo>(`${this.url}/${id}/desactivar`, {});
  }
}