
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Carrera, PeticionCarrera } from '../models/carrera';

@Injectable({
  providedIn: 'root'
})
export class Carreras {
  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/carreras';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar() {
    return this.http.get<Carrera[]>(this.url, {
      headers: this.obtenerHeaders()
    });
  }

  buscarPorId(idCarrera: number) {
    return this.http.get<Carrera>(`${this.url}/${idCarrera}`, {
      headers: this.obtenerHeaders()
    });
  }

  crear(datos: PeticionCarrera) {
    return this.http.post(this.url, datos, {
      headers: this.obtenerHeaders()
    });
  }

  editar(idCarrera: number, datos: PeticionCarrera) {
    return this.http.put(`${this.url}/${idCarrera}`, datos, {
      headers: this.obtenerHeaders()
    });
  }

  cambiarEstado(idCarrera: number, activo: boolean) {
    return this.http.put(`${this.url}/${idCarrera}/estado`, { activo }, {
      headers: this.obtenerHeaders()
    });
  }
}
