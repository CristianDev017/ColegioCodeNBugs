import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private url = 'http://localhost:8080/ColegioBackend_war_exploded/api/auth/login';

  constructor(private http: HttpClient) {}

  login(usuario: string, password: string) {
    return this.http.post<any>(this.url, { usuario, password });
  }
}