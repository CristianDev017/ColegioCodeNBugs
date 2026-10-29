import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AsignacionService } from '../../../core/asignacion.service';
import { Asignacion, CursoSeccion, DatosAsignacion, OpcionAsignacion } from '../../../models/asignacion';

@Component({
  selector: 'app-asignacion-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './asignacion-form.html'
})
export class AsignacionForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Asignacion;

  maestros: OpcionAsignacion[] = [];
  secciones: OpcionAsignacion[] = [];
  cursosLibres: CursoSeccion[] = [];
  seccionConCursos: boolean = false;
  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  form = new FormGroup({
    idEmpleado: new FormControl<number | null>(null, [Validators.required]),
    idSeccion: new FormControl<number | null>(null, [Validators.required]),
    idCurriculoCurso: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(private servicio: AsignacionService) {}

  ngOnInit(): void {
    this.servicio.maestros().subscribe({
      next: (lista) => (this.maestros = lista),
      error: () => (this.mensajeErrorServidor = 'No se pudo cargar la lista de maestros')
    });
    if (this.esEdicion) {
      this.form.get('idSeccion')!.disable();
      this.form.get('idCurriculoCurso')!.disable();
    } else {
      this.servicio.secciones().subscribe({
        next: (lista) => (this.secciones = lista),
        error: () => (this.mensajeErrorServidor = 'No se pudo cargar la lista de secciones')
      });
    }
    this.limpiar();
  }

  cargarCursos(): void {
    this.form.patchValue({ idCurriculoCurso: null });
    this.cursosLibres = [];
    this.seccionConCursos = false;
    const idSeccion = this.form.value.idSeccion;
    if (!idSeccion) {
      return;
    }
    this.servicio.cursos(idSeccion).subscribe({
      next: (lista) => {
        this.seccionConCursos = lista.length > 0;
        this.cursosLibres = lista.filter((c) => c.idAsignacion == null);
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudieron cargar los cursos de la sección';
      }
    });
  }

  enviar(): void {
    this.mensajeExito = '';
    this.mensajeErrorServidor = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.esEdicion) {
      this.actualizar();
    } else {
      this.guardarNuevo();
    }
  }

  guardarNuevo(): void {
    this.servicio.crear(this.armarDatos()).subscribe({
      next: () => {
        this.limpiarNuevo();
        this.mensajeExito = 'Maestro asignado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo asignar el maestro';
      }
    });
  }

  actualizar(): void {
    this.servicio.cambiarMaestro(this.registro!.id, this.armarDatos()).subscribe({
      next: (actualizada) => {
        this.registro = actualizada;
        this.mensajeExito = 'Maestro cambiado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo cambiar el maestro';
      }
    });
  }

  limpiar(): void {
    if (this.esEdicion && this.registro) {
      this.limpiarEdicion();
    } else {
      this.limpiarNuevo();
    }
  }

  limpiarNuevo(): void {
    this.form.reset({ idEmpleado: null, idSeccion: null, idCurriculoCurso: null });
    this.cursosLibres = [];
    this.seccionConCursos = false;
  }

  limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({ idEmpleado: r.idEmpleado, idSeccion: r.idSeccion, idCurriculoCurso: r.idCurriculoCurso });
  }

  mensajeError(ruta: string): string {
    const control = this.form.get(ruta);
    if (!control || !control.touched || !control.errors) {
      return '';
    }
    if (control.errors['required']) {
      return 'Este campo es obligatorio';
    }
    return '';
  }

  private armarDatos(): DatosAsignacion {
    const v = this.form.getRawValue();
    return {
      idEmpleado: Number(v.idEmpleado),
      idCurriculoCurso: Number(v.idCurriculoCurso),
      idSeccion: Number(v.idSeccion)
    };
  }
}