
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Ciclos } from '../../services/ciclos';
import { Grados } from '../../services/grados';
import { CicloEscolar, PeticionCiclo } from '../../models/ciclo-escolar';
import { Grado } from '../../models/grado';

@Component({
  selector: 'app-ciclos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ciclos.html'
})
export class CiclosPagina implements OnInit {

  ciclos: CicloEscolar[] = [];
  grados: Grado[] = [];
  gradosAsignados: number[] = [];

  anio: number | null = null;
  fechaInicio = '';
  fechaFin = '';

  cicloSeleccionado: CicloEscolar | null = null;
  gradoSeleccionado = 0;

  mensaje = '';
  error = '';
  guardando = false;
  cambiandoEstado = false;
  cargando = false;
  administrando = false;

  constructor(
    private ciclosService: Ciclos,
    private gradosService: Grados,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
    this.cargarCatalogo();
  }

  listar() {
    this.cargando = true;
    this.detector.detectChanges();

    this.ciclosService.listar().subscribe({
      next: (datos) => {
        this.ciclos = datos;
        this.cargando = false;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.cargando = false;
        this.error = e.error?.mensaje || 'No se pudieron cargar los años lectivos';
        this.detector.detectChanges();
      }
    });
  }

  cargarCatalogo() {
    this.gradosService.listar().subscribe({
      next: (datos) => {
        this.grados = datos;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo cargar el catalogo de grados';
        this.detector.detectChanges();
      }
    });
  }

  guardar() {
    if (this.guardando || this.cambiandoEstado || this.administrando) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    if (!this.anio || !this.fechaInicio || !this.fechaFin) {
      this.error = 'Completa todos los campos';
      this.detector.detectChanges();
      return;
    }

    if (this.anio < 2000 || this.anio > 2100) {
      this.error = 'Ingresa un año entre 2000 y 2100';
      this.detector.detectChanges();
      return;
    }

    if (this.fechaFin <= this.fechaInicio) {
      this.error = 'La fecha final debe ser posterior a la inicial';
      this.detector.detectChanges();
      return;
    }

    const datos: PeticionCiclo = {
      anio: this.anio,
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin
    };

    this.guardando = true;
    this.detector.detectChanges();

    this.ciclosService.crear(datos).subscribe({
      next: () => {
        this.limpiar();
        this.guardando = false;
        this.mensaje = 'Año lectivo creado. Ahora puedes asignarle grados';
        this.detector.detectChanges();
        this.listar();
      },
      error: (e) => {
        this.guardando = false;
        this.error = e.error?.mensaje || 'No se pudo crear el año lectivo';
        this.detector.detectChanges();
      }
    });
  }

  abrirGrados(ciclo: CicloEscolar) {
    if (this.administrando || this.guardando || this.cambiandoEstado) {
      return;
    }

    this.cicloSeleccionado = ciclo;
    this.gradoSeleccionado = 0;
    this.gradosAsignados = [];
    this.mensaje = '';
    this.error = '';
    this.administrando = true;
    this.detector.detectChanges();

    this.ciclosService.listarGrados(ciclo.idCiclo).subscribe({
      next: (datos) => {
        this.gradosAsignados = datos;
        this.administrando = false;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudieron consultar los grados del año';
        this.administrando = false;
        this.detector.detectChanges();
      }
    });
  }

  gradosDisponibles() {
    return this.grados.filter(grado =>
      grado.activo && !this.gradosAsignados.includes(grado.idGrado)
    );
  }

  gradosDelAnio() {
    return this.grados.filter(grado =>
      this.gradosAsignados.includes(grado.idGrado)
    );
  }

  agregarGrado() {
    if (!this.cicloSeleccionado || this.administrando) {
      return;
    }

    if (this.cicloSeleccionado.estado !== 'PLANIFICADO') {
      return;
    }

    if (this.gradoSeleccionado === 0) {
      this.error = 'Selecciona un grado';
      this.detector.detectChanges();
      return;
    }

    const idCiclo = this.cicloSeleccionado.idCiclo;

    this.mensaje = '';
    this.error = '';
    this.administrando = true;
    this.detector.detectChanges();

    this.ciclosService.agregarGrado(idCiclo, this.gradoSeleccionado).subscribe({
      next: () => {
        this.gradoSeleccionado = 0;
        this.mensaje = 'Grado agregado al año lectivo';
        this.actualizarGrados(idCiclo);
        this.listar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo agregar el grado';
        this.administrando = false;
        this.detector.detectChanges();
      }
    });
  }

  quitarGrado(grado: Grado) {
    if (!this.cicloSeleccionado || this.administrando) {
      return;
    }

    if (this.cicloSeleccionado.estado !== 'PLANIFICADO') {
      return;
    }

    if (!confirm('¿Quitar ' + grado.nombre + ' del año ' +
      this.cicloSeleccionado.anio + '?')) {
      return;
    }

    const idCiclo = this.cicloSeleccionado.idCiclo;

    this.mensaje = '';
    this.error = '';
    this.administrando = true;
    this.detector.detectChanges();

    this.ciclosService.quitarGrado(idCiclo, grado.idGrado).subscribe({
      next: () => {
        this.mensaje = 'Grado quitado del año lectivo';
        this.actualizarGrados(idCiclo);
        this.listar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo quitar el grado';
        this.administrando = false;
        this.detector.detectChanges();
      }
    });
  }

  actualizarGrados(idCiclo: number) {
    this.ciclosService.listarGrados(idCiclo).subscribe({
      next: (datos) => {
        this.gradosAsignados = datos;
        this.administrando = false;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudieron actualizar los grados';
        this.administrando = false;
        this.detector.detectChanges();
      }
    });
  }

  cerrarAdministracion() {
    if (this.administrando) {
      return;
    }

    this.cicloSeleccionado = null;
    this.gradoSeleccionado = 0;
    this.gradosAsignados = [];
    this.detector.detectChanges();
  }

  activar(ciclo: CicloEscolar) {
    if (this.cambiandoEstado || this.guardando || this.administrando) {
      return;
    }

    if (ciclo.estado !== 'PLANIFICADO') {
      return;
    }

    if (this.ciclos.some(c => c.estado === 'ACTIVO')) {
      this.error = 'Ya existe un año lectivo activo. Debes cerrarlo primero';
      this.mensaje = '';
      this.detector.detectChanges();
      return;
    }

    if (ciclo.cantidadGrados === 0) {
      this.error = 'Primero debes asignar grados a este año lectivo';
      this.mensaje = '';
      this.detector.detectChanges();
      return;
    }

    if (!confirm('¿Quieres activar el año lectivo ' + ciclo.anio + '?')) {
      return;
    }

    this.actualizarEstado(ciclo.idCiclo, 'ACTIVO');
  }

  cerrar(ciclo: CicloEscolar) {
    if (this.cambiandoEstado || this.guardando || this.administrando) {
      return;
    }

    if (ciclo.estado !== 'ACTIVO') {
      return;
    }

    if (!confirm('¿Seguro que deseas cerrar el año lectivo ' +
      ciclo.anio + '? No se podrá volver a activar.')) {
      return;
    }

    this.actualizarEstado(ciclo.idCiclo, 'CERRADO');
  }

  private actualizarEstado(id: number, estado: string) {
    this.error = '';
    this.mensaje = '';
    this.cambiandoEstado = true;
    this.detector.detectChanges();

    this.ciclosService.cambiarEstado(id, estado).subscribe({
      next: () => {
        this.cambiandoEstado = false;
        this.mensaje = estado === 'ACTIVO'
          ? 'Año lectivo activado correctamente'
          : 'Año lectivo cerrado correctamente';

        if (this.cicloSeleccionado?.idCiclo === id) {
          this.cerrarAdministracion();
        }

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
    this.anio = null;
    this.fechaInicio = '';
    this.fechaFin = '';
    this.detector.detectChanges();
  }
}
