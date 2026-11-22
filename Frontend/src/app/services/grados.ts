import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Grado, PeticionGrado } from '../models/grado';

@Injectable({
  providedIn: 'root'
})
export class Grados {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/grados';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar() {
    return this.http.get<Grado[]>(this.url, {
      headers: this.obtenerHeaders()
    });
  }

  crear(datos: PeticionGrado) {
    return this.http.post(this.url, datos, {
      headers: this.obtenerHeaders()
    });
  }

  editar(id: number, datos: PeticionGrado) {
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
