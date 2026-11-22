
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Cursos } from '../../services/cursos';
import { Curso, PeticionCurso } from '../../models/curso';

@Component({
  selector: 'app-cursos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cursos.html'
})
export class CursosPagina implements OnInit {

  cursos: Curso[] = [];

  idEditar = 0;
  nombre = '';
  descripcion = '';

  mensaje = '';
  error = '';
  guardando = false;
  cambiandoEstado = false;
  cargando = false;

  constructor(
    private cursosService: Cursos,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
  }

  listar() {
    this.cargando = true;
    this.detector.detectChanges();

    this.cursosService.listar().subscribe({
      next: (datos) => {
        this.cursos = datos;
        this.cargando = false;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.cargando = false;
        this.error = e.error?.mensaje || 'No se pudieron cargar los cursos';
        this.detector.detectChanges();
      }
    });
  }

  guardar() {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    const datos: PeticionCurso = {
      nombre: this.nombre.trim(),
      descripcion: this.descripcion.trim()
    };

    if (!datos.nombre) {
      this.error = 'El nombre del curso es obligatorio';
      this.detector.detectChanges();
      return;
    }

    if (datos.nombre.length > 100) {
      this.error = 'El nombre no puede superar 100 caracteres';
      this.detector.detectChanges();
      return;
    }

    this.guardando = true;
    this.detector.detectChanges();

    if (this.idEditar === 0) {
      this.cursosService.crear(datos).subscribe({
        next: () => {
          this.limpiar();
          this.guardando = false;
          this.mensaje = 'Curso creado correctamente';
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.guardando = false;
          this.error = e.error?.mensaje || 'No se pudo crear el curso';
          this.detector.detectChanges();
        }
      });
    } else {
      this.cursosService.editar(this.idEditar, datos).subscribe({
        next: () => {
          this.limpiar();
          this.guardando = false;
          this.mensaje = 'Curso actualizado correctamente';
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.guardando = false;
          this.error = e.error?.mensaje || 'No se pudo actualizar el curso';
          this.detector.detectChanges();
        }
      });
    }
  }

  seleccionar(curso: Curso) {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    this.idEditar = curso.idCurso;
    this.nombre = curso.nombre;
    this.descripcion = curso.descripcion || '';

    this.mensaje = '';
    this.error = '';
    this.detector.detectChanges();
  }

  cambiarEstado(curso: Curso) {
    if (this.guardando || this.cambiandoEstado) {
      return;
    }

    const accion = curso.activo ? 'desactivar' : 'activar';

    if (!confirm('¿Quieres ' + accion + ' el curso ' + curso.nombre + '?')) {
      return;
    }

    this.mensaje = '';
    this.error = '';
    this.cambiandoEstado = true;
    this.detector.detectChanges();

    this.cursosService.cambiarEstado(curso.idCurso, !curso.activo).subscribe({
      next: () => {
        this.cambiandoEstado = false;
        this.mensaje = 'Estado actualizado correctamente';
        this.detector.detectChanges();
        this.listar();
      },
      error: (e) => {
        this.cambiandoEstado = false;
        this.error = e.error?.mensaje || 'No se pudo cambiar el estado';
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
