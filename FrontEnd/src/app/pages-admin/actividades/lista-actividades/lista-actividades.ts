import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ActividadService } from '../../../core/actividad.service';
import { Actividad } from '../../../models/actividad';

@Component({
  selector: 'app-lista-actividades',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './lista-actividades.html'
})
export class ListaActividades implements OnInit {
  actividades: Actividad[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;
  error = '';

  filtro: FormGroup;

  constructor(private fb: FormBuilder, private servicio: ActividadService) {
    this.filtro = this.fb.group({
      buscar: ['']
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    const buscar = String(this.filtro.value.buscar ?? '').trim();
    this.servicio.listar(buscar, this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.actividades = r.datos;
        this.total = r.total;
        this.error = '';
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

  cambiarEstado(a: Actividad): void {
    const peticion = a.activo ? this.servicio.desactivar(a.id) : this.servicio.activar(a.id);
    peticion.subscribe({
      next: () => this.cargar(),
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cambiar el estado')
    });
  }
}