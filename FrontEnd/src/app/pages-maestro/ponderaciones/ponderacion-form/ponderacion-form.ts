import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MaestroService } from '../../../core/maestro.service';
import { DatosPonderacion, Ponderacion } from '../../../models/maestro';

@Component({
  selector: 'app-ponderacion-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './ponderacion-form.html'
})
export class PonderacionForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() idAsignacion: number = 0;
  @Input() registro?: Ponderacion;

  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  textosPattern: { [ruta: string]: string } = {
    porcentaje: 'Solo números con hasta 2 decimales',
    notaMaxima: 'Solo números con hasta 2 decimales'
  };

  form = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.maxLength(60)]),
    porcentaje: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0.01),
      Validators.max(100),
      Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)
    ]),
    notaMaxima: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0.01),
      Validators.max(999.99),
      Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)
    ])
  });

  constructor(private servicio: MaestroService) {}

  ngOnInit(): void {
    this.limpiar();
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
    this.servicio.crearPonderacion(this.idAsignacion, this.armarDatos()).subscribe({
      next: () => {
        this.limpiarNuevo();
        this.mensajeExito = 'Actividad creada correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo crear la actividad';
      }
    });
  }

  actualizar(): void {
    this.servicio.editarPonderacion(this.idAsignacion, this.registro!.id, this.armarDatos()).subscribe({
      next: (actualizada) => {
        this.registro = actualizada;
        this.mensajeExito = 'Actividad actualizada correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo actualizar la actividad';
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
    this.form.reset({ nombre: '', porcentaje: null, notaMaxima: null });
  }

  limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({ nombre: r.nombre, porcentaje: r.porcentaje, notaMaxima: r.notaMaxima });
  }

  mensajeError(ruta: string): string {
    const control = this.form.get(ruta);
    if (!control || !control.touched || !control.errors) {
      return '';
    }
    if (control.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (control.errors['maxlength']) {
      return `Máximo ${control.errors['maxlength'].requiredLength} caracteres`;
    }
    if (control.errors['min']) {
      return 'Debe ser mayor que 0';
    }
    if (control.errors['max']) {
      return `No puede pasar de ${control.errors['max'].max}`;
    }
    if (control.errors['pattern']) {
      return this.textosPattern[ruta] ?? 'El formato no es válido';
    }
    return '';
  }

  private armarDatos(): DatosPonderacion {
    const v = this.form.value;
    return {
      nombre: (v.nombre ?? '').trim(),
      porcentaje: Number(v.porcentaje),
      notaMaxima: Number(v.notaMaxima)
    };
  }
}