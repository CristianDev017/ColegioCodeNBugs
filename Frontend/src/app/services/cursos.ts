
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Curso, PeticionCurso } from '../models/curso';

@Injectable({
  providedIn: 'root'
})
export class Cursos {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/cursos';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar() {
    return this.http.get<Curso[]>(this.url, {
      headers: this.obtenerHeaders()
    });
  }

  buscarPorId(id: number) {
    return this.http.get<Curso>(`${this.url}/${id}`, {
      headers: this.obtenerHeaders()
    });
  }

  crear(datos: PeticionCurso) {
    return this.http.post(this.url, datos, {
      headers: this.obtenerHeaders()
    });
  }

  editar(id: number, datos: PeticionCurso) {
    return this.http.put(`${this.url}/${id}`, datos, {
      headers: this.obtenerHeaders()
    });
  }

  cambiarEstado(id: number, activo: boolean) {
    return this.http.put(`${this.url}/${id}/estado`, { activo }, {
      headers: this.obtenerHeaders()
    });
  }
}
