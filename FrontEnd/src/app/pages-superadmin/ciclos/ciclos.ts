import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CicloEscolarService } from '../../core/ciclo-escolar.service';
import { CicloEscolar } from '../../models/ciclo-escolar';

@Component({
  selector: 'app-ciclos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './ciclos.html',
})
export class Ciclos implements OnInit {
  ciclos: CicloEscolar[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;

  mensaje = '';
  error = '';
  errorFormulario = '';

  mostrarFormulario = false;
  editandoId: number | null = null;
  formulario: FormGroup;

  accionPendiente: string | null = null;
  cicloPendiente: CicloEscolar | null = null;

  constructor(private fb: FormBuilder, private servicio: CicloEscolarService) {
    this.formulario = this.fb.group({
      anio: [null, [Validators.required, Validators.min(2000), Validators.max(2100)]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    this.servicio.listar(this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.ciclos = respuesta.datos;
        this.total = respuesta.total;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo cargar la lista de años lectivos';
      }
    });
  }

  irAPagina(numero: number): void {
    if (numero < 1 || numero > this.totalPaginas) {
      return;
    }
    this.pagina = numero;
    this.cargar();
  }

  claseEstado(estado: string): string {
    if (estado === 'ACTIVO') {
      return 'bg-success';
    }
    if (estado === 'CERRADO') {
      return 'bg-secondary';
    }
    return 'bg-warning text-dark';
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario.reset();
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  abrirEditar(ciclo: CicloEscolar): void {
    this.editandoId = ciclo.id;
    this.formulario.setValue({
      anio: ciclo.anio,
      fechaInicio: ciclo.fechaInicio,
      fechaFin: ciclo.fechaFin
    });
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    this.mostrarFormulario = false;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const datos = this.formulario.value;
    if (datos.fechaFin <= datos.fechaInicio) {
      this.errorFormulario = 'La fecha de fin debe ser posterior a la de inicio';
      return;
    }

    const peticion = this.editandoId === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.editandoId, datos);

    peticion.subscribe({
      next: () => {
        this.mensaje = this.editandoId === null ? 'Año lectivo creado' : 'Año lectivo actualizado';
        this.formulario.reset();
        this.mostrarFormulario = false;
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje || 'No se pudo guardar el año lectivo';
      }
    });
  }

  pedirConfirmacion(accion: string, ciclo: CicloEscolar): void {
    this.accionPendiente = accion;
    this.cicloPendiente = ciclo;
  }

  cancelarConfirmacion(): void {
    this.accionPendiente = null;
    this.cicloPendiente = null;
  }

  confirmar(): void {
    if (!this.cicloPendiente || !this.accionPendiente) {
      return;
    }
    const id = this.cicloPendiente.id;
    const accion = this.accionPendiente;
    const peticion = accion === 'activar' ? this.servicio.activar(id) : this.servicio.cerrar(id);

    peticion.subscribe({
      next: () => {
        this.mensaje = accion === 'activar' ? 'Año lectivo activado' : 'Año lectivo cerrado';
        this.error = '';
        this.cancelarConfirmacion();
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo completar la acción';
        this.mensaje = '';
        this.cancelarConfirmacion();
      }
    });
  }
}