import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ActividadService } from '../../../core/actividad.service';
import { Actividad, DatosActividad } from '../../../models/actividad';

@Component({
  selector: 'app-actividad-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './actividad-form.html'
})
export class ActividadForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Actividad;

  guardado = false;
  mensajeExito = '';
  error = '';

  form: FormGroup;

  constructor(private fb: FormBuilder, private actividades: ActividadService) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      costo: [null, [Validators.required, Validators.min(0.01), Validators.max(99999999.99)]],
      cupoTotal: [null, [Validators.required, Validators.min(1), Validators.pattern(/^[0-9]+$/)]]
    });
  }

  ngOnInit(): void {
    this.limpiar();
  }

  enviar(): void {
    this.error = '';
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

  private guardarNuevo(): void {
    this.actividades.crear(this.armarDatos()).subscribe({
      next: () => {
        this.limpiar();
        this.mensajeExito = 'Actividad guardada correctamente';
        this.guardado = true;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo guardar la actividad')
    });
  }

  private actualizar(): void {
    if (!this.registro) {
      return;
    }
    this.actividades.editar(this.registro.id, this.armarDatos()).subscribe({
      next: (a) => {
        this.registro = a;
        this.mensajeExito = 'Actividad actualizada correctamente';
        this.guardado = true;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo actualizar la actividad')
    });
  }

  limpiar(): void {
    if (this.esEdicion && this.registro) {
      this.limpiarEdicion();
    } else {
      this.limpiarNuevo();
    }
    this.guardado = false;
    this.error = '';
  }

  private limpiarNuevo(): void {
    this.form.reset({ nombre: '', costo: null, cupoTotal: null });
  }

  private limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({ nombre: r.nombre, costo: r.costo, cupoTotal: r.cupoTotal });
  }

  mensajeError(ruta: string): string {
    const campo = this.form.get(ruta);
    if (!campo || !campo.touched || !campo.errors) {
      return '';
    }
    if (campo.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (campo.errors['maxlength']) {
      return `Máximo ${campo.errors['maxlength'].requiredLength} caracteres`;
    }
    if (campo.errors['min']) {
      return `El valor mínimo es ${campo.errors['min'].min}`;
    }
    if (campo.errors['max']) {
      return `El valor máximo es ${campo.errors['max'].max}`;
    }
    if (campo.errors['pattern']) {
      return 'Solo se permiten números enteros';
    }
    return 'Valor no válido';
  }

  private armarDatos(): DatosActividad {
    const v = this.form.getRawValue();
    return {
      nombre: String(v.nombre ?? '').trim(),
      costo: Number(v.costo),
      cupoTotal: Number(v.cupoTotal)
    };
  }
}