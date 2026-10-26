import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EstudianteService } from '../../../core/estudiante.service';
import { Estudiante } from '../../../models/estudiante';

@Component({
  selector: 'app-lista-estudiantes',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-estudiantes.html'
})
export class ListaEstudiantes implements OnInit {
  estudiantes: Estudiante[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;

  cambiando: Estudiante | null = null;
  nuevoEstado = '';

  mensaje = '';
  error = '';

  filtro: FormGroup;

  constructor(private fb: FormBuilder, private servicio: EstudianteService) {
    this.filtro = this.fb.group({
      buscar: [''],
      estado: ['']
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    const buscar = (this.filtro.value.buscar ?? '').trim();
    const estado = this.filtro.value.estado ?? '';
    this.servicio.listar(buscar, estado, this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.estudiantes = r.datos;
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

  claseEstado(estado: string): string {
    if (estado === 'ACTIVO') {
      return 'bg-success';
    }
    if (estado === 'GRADUADO') {
      return 'bg-info';
    }
    return 'bg-secondary';
  }

  pedirCambio(e: Estudiante): void {
    this.mensaje = '';
    this.error = '';
    this.cambiando = e;
    this.nuevoEstado = e.estado;
  }

  elegirEstado(valor: string): void {
    this.nuevoEstado = valor;
  }

  cancelarCambio(): void {
    this.cambiando = null;
  }

  confirmarCambio(): void {
    if (!this.cambiando) {
      return;
    }
    const id = this.cambiando.id;
    this.cambiando = null;
    this.servicio.cambiarEstado(id, this.nuevoEstado).subscribe({
      next: () => {
        this.mensaje = 'Estado del estudiante actualizado';
        this.cargar();
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cambiar el estado')
    });
  }
}