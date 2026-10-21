import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Nivel } from '../models/nivel';
import { API_URL } from './rest-constants';


@Injectable({ providedIn: 'root' })
export class NivelService {
  constructor(private http: HttpClient) {}

listar(): Observable<Nivel[]> {
  return this.http.get<Nivel[]>(`${API_URL}/niveles`);
}
}