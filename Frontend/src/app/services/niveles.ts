import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Nivel } from '../models/nivel';

@Injectable({
  providedIn: 'root'
})
export class Niveles {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/niveles';

  constructor(private http: HttpClient) {}

  listar() {
    const token = sessionStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + (token || '')
    });

    return this.http.get<Nivel[]>(this.url, { headers });
  }
}
