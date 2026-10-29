import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { AsignacionService } from '../../../core/asignacion.service';
import { Asignacion, OpcionAsignacion } from '../../../models/asignacion';

@Component({
  selector: 'app-lista-asignaciones',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-asignaciones.html'
})
export class ListaAsignaciones implements OnInit, OnDestroy {
  asignaciones: Asignacion[] = [];
  maestros: OpcionAsignacion[] = [];
  secciones: OpcionAsignacion[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  asignacionAQuitar?: Asignacion;

  filtros = new FormGroup({
    idEmpleado: new FormControl<number>(0, { nonNullable: true }),
    idSeccion: new FormControl<number>(0, { nonNullable: true })
  });

  private suscripcion?: Subscription;

  constructor(private servicio: AsignacionService) {}

  ngOnInit(): void {
    this.servicio.maestros().subscribe((lista) => (this.maestros = lista));
    this.servicio.secciones().subscribe((lista) => (this.secciones = lista));
    this.cargar();
    this.suscripcion = this.filtros.valueChanges.subscribe(() => {
      this.pagina = 1;
      this.cargar();
    });
  }

  ngOnDestroy(): void {
    this.suscripcion?.unsubscribe();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    const f = this.filtros.getRawValue();
    this.cargando = true;
    this.servicio.listar(f.idEmpleado, f.idSeccion, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.asignaciones = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar el listado de asignaciones';
        this.cargando = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ idEmpleado: 0, idSeccion: 0 });
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

  pedirConfirmacion(asignacion: Asignacion): void {
    this.asignacionAQuitar = asignacion;
  }

  cancelar(): void {
    this.asignacionAQuitar = undefined;
  }

  confirmar(): void {
    const asignacion = this.asignacionAQuitar!;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.servicio.quitar(asignacion.id).subscribe({
      next: () => {
        this.asignacionAQuitar = undefined;
        this.mensajeExito = 'Asignación eliminada correctamente';
        if (this.asignaciones.length === 1 && this.pagina > 1) {
          this.pagina--;
        }
        this.cargar();
      },
      error: (e) => {
        this.asignacionAQuitar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo quitar la asignación';
      }
    });
  }
}