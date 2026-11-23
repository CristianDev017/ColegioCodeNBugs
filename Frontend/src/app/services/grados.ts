
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

  // Catalogo completo utilizado por Ciclos escolares.
  listar() {
    return this.http.get<Grado[]>(`${this.url}/catalogo`, {
      headers: this.obtenerHeaders()
    });
  }

  // Consulta paginada para la pantalla de Grados.
  listarPaginado(pagina = 1, tamano = 10) {
    return this.http.get<{
      datos: Grado[];
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
