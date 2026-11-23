
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  CicloEscolar,
  PeticionCiclo
} from '../models/ciclo-escolar';

@Injectable({
  providedIn: 'root'
})
export class Ciclos {

  private url =
    'http://localhost:8080/ColegioBackend_war_exploded/api/ciclos';

  constructor(private http: HttpClient) {}

  private obtenerHeaders() {
    const token = sessionStorage.getItem('token');

    return new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });
  }

  listar(pagina = 1, tamano = 10) {
    return this.http.get<{
      datos: CicloEscolar[];
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
    return this.http.get<CicloEscolar>(
      `${this.url}/${id}`,
      { headers: this.obtenerHeaders() }
    );
  }

  crear(datos: PeticionCiclo) {
    return this.http.post(
      this.url,
      datos,
      { headers: this.obtenerHeaders() }
    );
  }

  cambiarEstado(id: number, estado: string) {
    return this.http.put(
      `${this.url}/${id}/estado`,
      { estado },
      { headers: this.obtenerHeaders() }
    );
  }

  listarGrados(idCiclo: number) {
    return this.http.get<number[]>(
      `${this.url}/${idCiclo}/grados`,
      { headers: this.obtenerHeaders() }
    );
  }

  agregarGrado(idCiclo: number, idGrado: number) {
    return this.http.post(
      `${this.url}/${idCiclo}/grados`,
      { idGrado },
      { headers: this.obtenerHeaders() }
    );
  }

  quitarGrado(idCiclo: number, idGrado: number) {
    return this.http.delete(
      `${this.url}/${idCiclo}/grados/${idGrado}`,
      { headers: this.obtenerHeaders() }
    );
  }
}
