
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Usuario, PeticionUsuario, PeticionEditarUsuario } from '../models/usuarios';

@Injectable({
  providedIn: 'root'
})
export class Usuarios {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/usuarios';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar(pagina = 1, tamano = 10) {
    return this.http.get<{
      datos: Usuario[];
      total: number;
      pagina: number;
      tamano: number;
    }>(this.url, {
      headers: this.obtenerHeaders(),
      params: {
        pagina: String(pagina),
        tamano: String(tamano)
      }
    });
  }

  buscarPorId(id: number) {
    return this.http.get<Usuario>(`${this.url}/${id}`, {
      headers: this.obtenerHeaders()
    });
  }

  crear(datos: PeticionUsuario) {
    return this.http.post(this.url, datos, {
      headers: this.obtenerHeaders()
    });
  }

  editar(id: number, datos: PeticionEditarUsuario) {
    return this.http.put(`${this.url}/${id}`, datos, {
      headers: this.obtenerHeaders()
    });
  }

  cambiarEstado(id: number, estado: string) {
    return this.http.put(`${this.url}/${id}/estado`, { estado }, {
      headers: this.obtenerHeaders()
    });
  }
}
