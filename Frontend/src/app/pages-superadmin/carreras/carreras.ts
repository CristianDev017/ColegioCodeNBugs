
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Carreras } from '../../services/carreras';
import { Carrera } from '../../models/carrera';

@Component({
  selector: 'app-carreras',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './carreras.html'
})
export class CarrerasPagina implements OnInit {

  carreras: Carrera[] = [];

  pagina = 1;
  tamano = 10;
  total = 0;
  cargando = false;

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  nombre = '';
  descripcion = '';
  idEditar = 0;
  mensaje = '';
  error = '';
  guardando = false;

  constructor(
    private carrerasService: Carreras,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
  }

  listar() {
    this.cargando = true;
    this.detector.detectChanges();

    this.carrerasService.listarPaginado(
      this.pagina, this.tamano
    ).subscribe({
      next: (respuesta) => {
        this.carreras = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;

        if (this.pagina > this.totalPaginas) {
          this.pagina = this.totalPaginas;
          this.listar();
          return;
        }

        this.detector.detectChanges();
      },
      error: (e) => {
        this.cargando = false;
        this.error = e.error?.mensaje ||
          'No se pudieron cargar las carreras';
        this.detector.detectChanges();
      }
    });
  }

  cambiarPagina(nuevaPagina: number) {
    if (
      this.cargando ||
      nuevaPagina < 1 ||
      nuevaPagina > this.totalPaginas ||
      nuevaPagina === this.pagina
    ) {
      return;
    }

    this.pagina = nuevaPagina;
    this.listar();
  }

  guardar() {
    if (this.guardando) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    const datos = {
      nombre: this.nombre.trim(),
      descripcion: this.descripcion.trim()
    };

    if (!datos.nombre) {
      this.error = 'El nombre de la carrera es obligatorio';
      this.detector.detectChanges();
      return;
    }

    this.guardando = true;
    this.detector.detectChanges();

    if (this.idEditar === 0) {
      this.carrerasService.crear(datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Carrera creada correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje ||
            'No se pudo crear la carrera';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    } else {
      this.carrerasService.editar(this.idEditar, datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Carrera actualizada correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje ||
            'No se pudo actualizar la carrera';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    }
  }

  seleccionar(carrera: Carrera) {
    if (this.guardando) {
      return;
    }

    this.idEditar = carrera.idCarrera;
    this.nombre = carrera.nombre;
    this.descripcion = carrera.descripcion || '';
    this.mensaje = '';
    this.error = '';

    this.detector.detectChanges();
  }

  cambiarEstado(carrera: Carrera) {
    if (this.guardando) {
      return;
    }

    const accion = carrera.activo ? 'desactivar' : 'activar';

    if (!confirm('¿Quieres ' + accion + ' esta carrera?')) {
      return;
    }

    this.mensaje = '';
    this.error = '';
    this.guardando = true;
    this.detector.detectChanges();

    this.carrerasService.cambiarEstado(
      carrera.idCarrera, !carrera.activo
    ).subscribe({
      next: () => {
        this.guardando = false;
        this.mensaje = 'Estado actualizado correctamente';
        this.detector.detectChanges();
        this.listar();
      },
      error: (e) => {
        this.guardando = false;
        this.error = e.error?.mensaje ||
          'No se pudo cambiar el estado';
        this.detector.detectChanges();
      }
    });
  }

  limpiar() {
    this.idEditar = 0;
    this.nombre = '';
    this.descripcion = '';
    this.detector.detectChanges();
  }
}
