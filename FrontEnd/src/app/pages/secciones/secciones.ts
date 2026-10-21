import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GradoService } from '../../core/grado.service';
import { SeccionService } from '../../core/seccion.service';
import { Grado } from '../../models/grado';
import { DatosSeccion, Seccion } from '../../models/seccion';

@Component({
  selector: 'app-secciones',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './secciones.html'
})
export class Secciones implements OnInit {
  secciones: Seccion[] = [];
  grados: Grado[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;

  idGrado = 0;

  mensaje = '';
  error = '';
  errorFormulario = '';

  mostrarFormulario = false;
  editandoId: number | null = null;
  formulario: FormGroup;

  seccionPendiente: Seccion | null = null;

  constructor(
    private fb: FormBuilder,
    private servicio: SeccionService,
    private gradoServicio: GradoService
  ) {
    this.formulario = this.fb.group({
      idGrado: [null, Validators.required],
      nombre: ['', [Validators.required, Validators.maxLength(10)]],
      cupo: [null, Validators.min(1)]
    });
  }

  ngOnInit(): void {
    this.gradoServicio.listar('', 0, 1, 50).subscribe({
      next: (respuesta) => (this.grados = respuesta.datos),
      error: () => (this.error = 'No se pudieron cargar los grados')
    });
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get gradosActivos(): Grado[] {
    return this.grados.filter((g) => g.activo);
  }

  cargar(): void {
    this.servicio.listar(this.idGrado, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.secciones = respuesta.datos;
        this.total = respuesta.total;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo cargar la lista de secciones';
      }
    });
  }

  cambiarGrado(valor: string): void {
    this.idGrado = Number(valor);
    this.pagina = 1;
    this.cargar();
  }

  irAPagina(numero: number): void {
    if (numero < 1 || numero > this.totalPaginas) {
      return;
    }
    this.pagina = numero;
    this.cargar();
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario.reset();
    this.formulario.get('idGrado')?.enable();
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  abrirEditar(seccion: Seccion): void {
    this.editandoId = seccion.id;
    this.formulario.setValue({
      idGrado: seccion.idGrado,
      nombre: seccion.nombre,
      cupo: seccion.cupo
    });
    this.formulario.get('idGrado')?.disable();
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
    const valores = this.formulario.getRawValue();
    const datos: DatosSeccion = {
      idGrado: Number(valores.idGrado),
      nombre: valores.nombre,
      cupo: valores.cupo ? Number(valores.cupo) : null
    };

    const peticion = this.editandoId === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.editandoId, datos);

    peticion.subscribe({
      next: () => {
        this.mensaje = this.editandoId === null ? 'Sección creada' : 'Sección actualizada';
        this.error = '';
        this.formulario.reset();
        this.mostrarFormulario = false;
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje || 'No se pudo guardar la sección';
      }
    });
  }

  activar(seccion: Seccion): void {
    this.servicio.activar(seccion.id).subscribe({
      next: () => {
        this.mensaje = 'Sección activada';
        this.error = '';
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo activar la sección';
        this.mensaje = '';
      }
    });
  }

  pedirDesactivar(seccion: Seccion): void {
    this.seccionPendiente = seccion;
  }

  cancelarDesactivar(): void {
    this.seccionPendiente = null;
  }

  confirmarDesactivar(): void {
    if (!this.seccionPendiente) {
      return;
    }
    this.servicio.desactivar(this.seccionPendiente.id).subscribe({
      next: () => {
        this.mensaje = 'Sección desactivada';
        this.error = '';
        this.seccionPendiente = null;
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo desactivar la sección';
        this.mensaje = '';
        this.seccionPendiente = null;
      }
    });
  }
}