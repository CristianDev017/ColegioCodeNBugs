import { DecimalPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaestroService } from '../../../core/maestro.service';
import { CursoMaestro, Ponderacion } from '../../../models/maestro';

@Component({
  selector: 'app-lista-ponderaciones',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './lista-ponderaciones.html'
})
export class ListaPonderaciones implements OnInit {
  idAsignacion: number = 0;
  curso?: CursoMaestro;
  ponderaciones: Ponderacion[] = [];
  suma: number = 0;
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  ponderacionAEliminar?: Ponderacion;

  constructor(private ruta: ActivatedRoute, private servicio: MaestroService) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.curso(this.idAsignacion).subscribe({
      next: (curso) => (this.curso = curso),
      error: () => (this.mensajeError = 'No se encontró el curso o no está asignado a ti')
    });
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    this.cargando = true;
    this.servicio.ponderaciones(this.idAsignacion, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.ponderaciones = respuesta.datos;
        this.total = respuesta.total;
        this.suma = respuesta.suma;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar las actividades';
        this.cargando = false;
      }
    });
  }

  anterior(): void {
    if (this.pagina > 1) {
      this.pagina--;
      this.cargar();
    }
  }

  siguiente(): void {
    if (this.pagina < this.totalPaginas) {
      this.pagina++;
      this.cargar();
    }
  }

  pedirConfirmacion(ponderacion: Ponderacion): void {
    this.ponderacionAEliminar = ponderacion;
  }

  cancelar(): void {
    this.ponderacionAEliminar = undefined;
  }

  confirmar(): void {
    const ponderacion = this.ponderacionAEliminar!;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.servicio.eliminarPonderacion(this.idAsignacion, ponderacion.id).subscribe({
      next: () => {
        this.ponderacionAEliminar = undefined;
        this.mensajeExito = 'Actividad eliminada correctamente';
        if (this.ponderaciones.length === 1 && this.pagina > 1) {
          this.pagina--;
        }
        this.cargar();
      },
      error: (e) => {
        this.ponderacionAEliminar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo eliminar la actividad';
      }
    });
  }
}