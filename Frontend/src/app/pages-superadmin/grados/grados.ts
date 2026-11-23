
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Grados } from '../../services/grados';
import { Niveles } from '../../services/niveles';
import { Carreras } from '../../services/carreras';

import { Grado } from '../../models/grado';
import { Nivel } from '../../models/nivel';
import { Carrera } from '../../models/carrera';

@Component({
  selector: 'app-grados',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './grados.html'
})
export class GradosPagina implements OnInit {

  grados: Grado[] = [];
  niveles: Nivel[] = [];
  carreras: Carrera[] = [];

  pagina = 1;
  tamano = 10;
  total = 0;
  cargando = false;

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  idEditar = 0;
  idNivel = 0;
  idCarrera: number | null = null;
  nombre = '';
  cupoDefault = 30;

  mensaje = '';
  error = '';
  guardando = false;
  cambiandoEstado = false;

  constructor(
    private gradosService: Grados,
    private nivelesService: Niveles,
    private carrerasService: Carreras,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
    this.cargarNiveles();
    this.cargarCarreras();
  }

  listar() {
    this.cargando = true;
    this.detector.detectChanges();

    this.gradosService.listarPaginado(
      this.pagina, this.tamano
    ).subscribe({
      next: (respuesta) => {
        this.grados = respuesta.datos;
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
          'No se pudieron cargar los grados';
        this.detector.detectChanges();
      }
    });
  }

  cambiarPagina(nuevaPagina: number) {
    if (this.cargando ||
        nuevaPagina < 1 ||
        nuevaPagina > this.totalPaginas ||
        nuevaPagina === this.pagina) {
      return;
    }

    this.pagina = nuevaPagina;
    this.listar();
  }

  cargarNiveles() {
    this.nivelesService.listar().subscribe({
      next: (datos) => {
        this.niveles = datos;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje ||
          'No se pudieron cargar los niveles';
        this.detector.detectChanges();
      }
    });
  }

  cargarCarreras() {
    this.carrerasService.listar().subscribe({
      next: (datos) => {
        this.carreras = datos;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje ||
          'No se pudieron cargar las carreras';
        this.detector.detectChanges();
      }
    });
  }

  esDiversificado() {
    const nivel = this.niveles.find(
      n => n.idNivel === this.idNivel
    );

    return nivel?.nombre.toLowerCase() === 'diversificado';
  }

  carrerasDisponibles() {
    return this.carreras.filter(c =>
      c.activo || c.idCarrera === this.idCarrera
    );
  }

  cambiarNivel() {
    this.idCarrera = null;
    this.detector.detectChanges();
  }

  guardar() {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    if (!this.nombre.trim() ||
        this.idNivel === 0 ||
        this.cupoDefault <= 0) {

      this.error = 'Completa los campos obligatorios';
      this.detector.detectChanges();
      return;
    }

    if (this.esDiversificado() && this.idCarrera === null) {
      this.error = 'Selecciona una carrera para Diversificado';
      this.detector.detectChanges();
      return;
    }

    const datos = {
      idNivel: this.idNivel,
      idCarrera: this.esDiversificado() ? this.idCarrera : null,
      nombre: this.nombre.trim(),
      cupoDefault: this.cupoDefault
    };

    this.guardando = true;
    this.detector.detectChanges();

    if (this.idEditar === 0) {
      this.gradosService.crear(datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Grado creado correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje || 'No se pudo crear el grado';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    } else {
      this.gradosService.editar(this.idEditar, datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Grado actualizado correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje || 'No se pudo actualizar el grado';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    }
  }

  seleccionar(grado: Grado) {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    this.idEditar = grado.idGrado;
    this.idNivel = grado.idNivel;
    this.idCarrera = grado.idCarrera;
    this.nombre = grado.nombre;
    this.cupoDefault = grado.cupoDefault;

    this.mensaje = '';
    this.error = '';
    this.detector.detectChanges();
  }

  cambiarEstado(grado: Grado) {
    if (this.cambiandoEstado || this.guardando) {
      return;
    }

    const accion = grado.activo ? 'desactivar' : 'activar';

    if (!confirm('¿Quieres ' + accion + ' el grado ' + grado.nombre + '?')) {
      return;
    }

    this.mensaje = '';
    this.error = '';
    this.cambiandoEstado = true;
    this.detector.detectChanges();

    this.gradosService.cambiarEstado(
      grado.idGrado, !grado.activo
    ).subscribe({
      next: () => {
        this.cambiandoEstado = false;
        this.mensaje = 'Estado actualizado correctamente';
        this.detector.detectChanges();
        this.listar();
      },
      error: (e) => {
        this.cambiandoEstado = false;
        this.error = e.error?.mensaje ||
          'No se pudo cambiar el estado';
        this.detector.detectChanges();
      }
    });
  }

  limpiar() {
    this.idEditar = 0;
    this.idNivel = 0;
    this.idCarrera = null;
    this.nombre = '';
    this.cupoDefault = 30;
    this.detector.detectChanges();
  }
}
