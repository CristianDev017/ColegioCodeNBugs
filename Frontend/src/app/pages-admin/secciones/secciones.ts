
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Secciones } from '../../services/secciones';

import {
  Seccion,
  PeticionSeccion,
  OpcionSeccion
} from '../../models/seccion';

@Component({
  selector: 'app-secciones',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './secciones.html'
})
export class SeccionesPagina implements OnInit {

  secciones: Seccion[] = [];
  opciones: OpcionSeccion[] = [];

  pagina = 1;
  tamano = 10;
  total = 0;

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  idEditar = 0;
  idCiclo = 0;
  idCicloGrado = 0;
  nombre = '';
  cupo = 30;

  mensaje = '';
  error = '';
  guardando = false;
  cambiandoEstado = false;
  cargando = false;

  constructor(
    private seccionesService: Secciones,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
    this.cargarOpciones();
  }

  listar() {
    this.cargando = true;
    this.detector.detectChanges();

    this.seccionesService.listar(this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.secciones = respuesta.datos;
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
          'No se pudieron cargar las secciones';
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

  cargarOpciones() {
    this.seccionesService.listarOpciones().subscribe({
      next: (datos) => {
        this.opciones = datos;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje ||
          'No se pudieron cargar los grados habilitados';
        this.detector.detectChanges();
      }
    });
  }

  ciclosDisponibles(): number[] {
    return [...new Set(this.opciones.map(opcion => opcion.anio))]
      .sort((a, b) => b - a);
  }

  gradosDisponibles(): OpcionSeccion[] {
    return this.opciones.filter(opcion =>
      opcion.anio === this.idCiclo
    );
  }

  cambiarCiclo() {
    this.idCicloGrado = 0;
    this.cupo = 30;
  }

  cambiarGrado() {
    const opcion = this.opciones.find(
      o => o.idCicloGrado === this.idCicloGrado
    );

    if (opcion && this.idEditar === 0) {
      this.cupo = opcion.cupoDefault;
    }

    this.detector.detectChanges();
  }

  guardar() {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    const datos: PeticionSeccion = {
      idCicloGrado: this.idCicloGrado,
      nombre: this.nombre.trim().toUpperCase(),
      cupo: this.cupo
    };

    if (!this.idCiclo || !datos.idCicloGrado ||
        !datos.nombre || datos.cupo <= 0) {
      this.error = 'Completa todos los campos correctamente';
      this.detector.detectChanges();
      return;
    }

    if (datos.nombre.length > 10) {
      this.error = 'La seccion no puede superar 10 caracteres';
      this.detector.detectChanges();
      return;
    }

    this.guardando = true;
    this.detector.detectChanges();

    if (this.idEditar === 0) {

      this.seccionesService.crear(datos).subscribe({
        next: () => {
          this.limpiar();
          this.guardando = false;
          this.mensaje = 'Seccion creada correctamente';
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.guardando = false;
          this.error = e.error?.mensaje || 'No se pudo crear la seccion';
          this.detector.detectChanges();
        }
      });

    } else {

      this.seccionesService.editar(this.idEditar, datos).subscribe({
        next: () => {
          this.limpiar();
          this.guardando = false;
          this.mensaje = 'Seccion actualizada correctamente';
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.guardando = false;
          this.error = e.error?.mensaje || 'No se pudo actualizar la seccion';
          this.detector.detectChanges();
        }
      });
    }
  }

  seleccionar(seccion: Seccion) {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    if (seccion.estadoCiclo === 'CERRADO') {
      this.error = 'No puedes editar secciones de ciclos cerrados';
      this.detector.detectChanges();
      return;
    }

    const disponible = this.opciones.some(
      opcion => opcion.idCicloGrado === seccion.idCicloGrado
    );

    if (!disponible) {
      this.error = 'El grado de esta seccion ya no esta habilitado';
      this.detector.detectChanges();
      return;
    }

    this.idEditar = seccion.idSeccion;
    this.idCiclo = seccion.anio;
    this.idCicloGrado = seccion.idCicloGrado;
    this.nombre = seccion.nombre;
    this.cupo = seccion.cupo;

    this.error = '';
    this.mensaje = '';
    this.detector.detectChanges();
  }

  cambiarEstado(seccion: Seccion) {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    if (seccion.estadoCiclo === 'CERRADO') {
      this.error = 'No puedes modificar secciones de ciclos cerrados';
      this.detector.detectChanges();
      return;
    }

    const accion = seccion.activo ? 'desactivar' : 'activar';

    if (!confirm('¿Quieres ' + accion + ' la seccion ' + seccion.nombre + '?')) {
      return;
    }

    this.error = '';
    this.mensaje = '';
    this.cambiandoEstado = true;
    this.detector.detectChanges();

    this.seccionesService.cambiarEstado(
      seccion.idSeccion, !seccion.activo
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
    this.idCiclo = 0;
    this.idCicloGrado = 0;
    this.nombre = '';
    this.cupo = 30;
    this.detector.detectChanges();
  }
}
