import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CarreraService } from '../../core/carrera.service';
import { Carrera } from '../../models/carrera';

@Component({
  selector: 'app-carreras',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './carreras.html'
})
export class Carreras implements OnInit {
  carreras: Carrera[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;

  mensaje = '';
  error = '';
  errorFormulario = '';

  mostrarFormulario = false;
  editandoId: number | null = null;
  formulario: FormGroup;

  carreraPendiente: Carrera | null = null;

  constructor(private fb: FormBuilder, private servicio: CarreraService) {
    this.formulario = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', Validators.maxLength(255)]
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
        this.carreras = respuesta.datos;
        this.total = respuesta.total;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo cargar la lista de carreras';
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

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario.reset({ nombre: '', descripcion: '' });
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  abrirEditar(carrera: Carrera): void {
    this.editandoId = carrera.id;
    this.formulario.setValue({
      nombre: carrera.nombre,
      descripcion: carrera.descripcion || ''
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
    const valores = this.formulario.value;
    const datos = { nombre: valores.nombre, descripcion: valores.descripcion || null };

    const peticion = this.editandoId === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.editandoId, datos);

    peticion.subscribe({
      next: () => {
        this.mensaje = this.editandoId === null ? 'Carrera creada' : 'Carrera actualizada';
        this.error = '';
        this.formulario.reset({ nombre: '', descripcion: '' });
        this.mostrarFormulario = false;
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje || 'No se pudo guardar la carrera';
      }
    });
  }

  activar(carrera: Carrera): void {
    this.servicio.activar(carrera.id).subscribe({
      next: () => {
        this.mensaje = 'Carrera activada';
        this.error = '';
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo activar la carrera';
        this.mensaje = '';
      }
    });
  }

  pedirDesactivar(carrera: Carrera): void {
    this.carreraPendiente = carrera;
  }

  cancelarDesactivar(): void {
    this.carreraPendiente = null;
  }

  confirmarDesactivar(): void {
    if (!this.carreraPendiente) {
      return;
    }
    this.servicio.desactivar(this.carreraPendiente.id).subscribe({
      next: () => {
        this.mensaje = 'Carrera desactivada';
        this.error = '';
        this.carreraPendiente = null;
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo desactivar la carrera';
        this.mensaje = '';
        this.carreraPendiente = null;
      }
    });
  }
}