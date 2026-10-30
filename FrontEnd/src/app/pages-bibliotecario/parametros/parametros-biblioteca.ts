import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BibliotecaService } from '../../core/biblioteca.service';

@Component({
  selector: 'app-parametros-biblioteca',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './parametros-biblioteca.html'
})
export class ParametrosBiblioteca implements OnInit {
  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  form = new FormGroup({
    diasPrestamo: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(365),
      Validators.pattern(/^[0-9]+$/)
    ]),
    multaPorDia: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0),
      Validators.max(999999.99),
      Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)
    ])
  });

  constructor(private servicio: BibliotecaService) {}

  ngOnInit(): void {
    this.servicio.config().subscribe({
      next: (c) => this.form.reset({ diasPrestamo: c.diasPrestamo, multaPorDia: c.multaPorDia }),
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo cargar la configuración';
      }
    });
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeErrorServidor = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.value;
    this.servicio.guardarConfig({ diasPrestamo: Number(v.diasPrestamo), multaPorDia: Number(v.multaPorDia) }).subscribe({
      next: () => {
        this.mensajeExito = 'Parámetros guardados correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudieron guardar los parámetros';
      }
    });
  }

  mensajeError(ruta: string): string {
    const control = this.form.get(ruta);
    if (!control || !control.touched || !control.errors) {
      return '';
    }
    if (control.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (control.errors['min']) {
      return ruta === 'diasPrestamo' ? 'Mínimo 1 día' : 'No puede ser negativa';
    }
    if (control.errors['max']) {
      return ruta === 'diasPrestamo' ? 'Máximo 365 días' : 'El valor es demasiado grande';
    }
    if (control.errors['pattern']) {
      return ruta === 'diasPrestamo' ? 'Solo números enteros' : 'Máximo 2 decimales';
    }
    return '';
  }
}
