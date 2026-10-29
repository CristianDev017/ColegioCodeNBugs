import { Component, OnDestroy, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { EmpleadoService } from '../../../core/empleado.service';
import { Empleado, PUESTOS } from '../../../models/empleado';

@Component({
  selector: 'app-lista-empleados',
  imports: [ReactiveFormsModule, RouterLink, DecimalPipe],
  templateUrl: './lista-empleados.html'
})
export class ListaEmpleados implements OnInit, OnDestroy {
  puestos = PUESTOS;
  empleados: Empleado[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  empleadoAConfirmar?: Empleado;

  filtros = new FormGroup({
    buscar: new FormControl(''),
    puesto: new FormControl(''),
    estado: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: EmpleadoService) {}

  ngOnInit(): void {
    this.cargar();
    this.suscripcion = this.filtros.valueChanges.pipe(debounceTime(300)).subscribe(() => {
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
    const f = this.filtros.value;
    this.cargando = true;
    this.servicio.listar((f.buscar ?? '').trim(), f.puesto ?? '', f.estado ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.empleados = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar el listado de empleados';
        this.cargando = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ buscar: '', puesto: '', estado: '' });
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

  pedirConfirmacion(empleado: Empleado): void {
    this.empleadoAConfirmar = empleado;
  }

  cancelar(): void {
    this.empleadoAConfirmar = undefined;
  }

  confirmar(): void {
    const empleado = this.empleadoAConfirmar!;
    const activar = empleado.estado === 'INACTIVO';
    const peticion = activar ? this.servicio.activar(empleado.id) : this.servicio.desactivar(empleado.id);
    this.mensajeExito = '';
    this.mensajeError = '';
    peticion.subscribe({
      next: () => {
        this.empleadoAConfirmar = undefined;
        this.mensajeExito = activar ? 'Empleado activado correctamente' : 'Empleado desactivado correctamente';
        this.cargar();
      },
      error: (e) => {
        this.empleadoAConfirmar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cambiar el estado del empleado';
      }
    });
  }
}