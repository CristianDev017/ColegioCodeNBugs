
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import {
  Seccion,
  PeticionSeccion,
  OpcionSeccion
} from '../models/seccion';

@Injectable({
  providedIn: 'root'
})
export class Secciones {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/secciones';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar(pagina = 1, tamano = 10) {
    return this.http.get<{
      datos: Seccion[];
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

  listarCatalogo() {
    return this.http.get<Seccion[]>(
      `${this.url}/catalogo`,
      { headers: this.obtenerHeaders() }
    );
  }

  listarOpciones() {
    return this.http.get<OpcionSeccion[]>(
      `${this.url}/opciones`,
      { headers: this.obtenerHeaders() }
    );
  }

  crear(datos: PeticionSeccion) {
    return this.http.post(this.url, datos, {
      headers: this.obtenerHeaders()
    });
  }

  editar(id: number, datos: PeticionSeccion) {
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
