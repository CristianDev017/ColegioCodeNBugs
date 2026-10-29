import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DatosEmpleado, Empleado } from '../models/empleado';
import { Pagina } from '../models/pagina';
import { API_URL } from './rest-constants';

@Injectable({ providedIn: 'root' })
export class EmpleadoService {
  private url = `${API_URL}/empleados`;

  constructor(private http: HttpClient) {}

  listar(buscar: string, puesto: string, estado: string, pagina: number, tamano: number): Observable<Pagina<Empleado>> {
    return this.http.get<Pagina<Empleado>>(this.url, { params: { buscar, puesto, estado, pagina, tamano } });
  }

  obtener(id: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.url}/${id}`);
  }

  crear(datos: DatosEmpleado): Observable<Empleado> {
    return this.http.post<Empleado>(this.url, datos);
  }

  editar(id: number, datos: DatosEmpleado): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.url}/${id}`, datos);
  }

  activar(id: number): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.url}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.url}/${id}/desactivar`, {});
  }
}