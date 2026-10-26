import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { InscripcionService } from '../../../core/inscripcion.service';
import { Inscripcion, SeccionDisponible } from '../../../models/inscripcion';

@Component({
  selector: 'app-lista-inscripciones',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-inscripciones.html'
})
export class ListaInscripciones implements OnInit {
  inscripciones: Inscripcion[] = [];
  secciones: SeccionDisponible[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;
  error = '';

  filtro: FormGroup;

  constructor(public auth: AuthService, private fb: FormBuilder, private servicio: InscripcionService) {
    this.filtro = this.fb.group({
      buscar: [''],
      idSeccion: [0]
    });
  }

  ngOnInit(): void {
    this.servicio.seccionesDisponibles().subscribe({
      next: (lista) => (this.secciones = lista),
      error: () => (this.secciones = [])
    });
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    const buscar = (this.filtro.value.buscar ?? '').trim();
    const idSeccion = Number(this.filtro.value.idSeccion ?? 0);
    this.servicio.listar(buscar, idSeccion, this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.inscripciones = r.datos;
        this.total = r.total;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cargar la lista')
    });
  }

  filtrar(): void {
    this.pagina = 1;
    this.cargar();
  }

  irA(p: number): void {
    if (p < 1 || p > this.totalPaginas) {
      return;
    }
    this.pagina = p;
    this.cargar();
  }
}