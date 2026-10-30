import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LibroService } from '../../../core/libro.service';
import { DatosLibro, Libro } from '../../../models/libro';

@Component({
  selector: 'app-libro-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './libro-form.html'
})
export class LibroForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Libro;

  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  form = new FormGroup({
    titulo: new FormControl('', [Validators.required, Validators.maxLength(150)]),
    autor: new FormControl('', [Validators.required, Validators.maxLength(100)]),
    editorial: new FormControl('', [Validators.required, Validators.maxLength(100)]),
    cantidadTotal: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0),
      Validators.max(9999),
      Validators.pattern(/^[0-9]+$/)
    ])
  });

  constructor(private servicio: LibroService) {}

  ngOnInit(): void {
    this.limpiar();
  }

  get prestados(): number {
    return this.registro ? this.registro.cantidadTotal - this.registro.cantidadDisponible : 0;
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
        this.mensajeExito = 'Libro registrado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo registrar el libro';
      }
    });
  }

  actualizar(): void {
    this.servicio.editar(this.registro!.id, this.armarDatos()).subscribe({
      next: (actualizado) => {
        this.registro = actualizado;
        this.mensajeExito = 'Libro actualizado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo actualizar el libro';
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
    this.form.reset({ titulo: '', autor: '', editorial: '', cantidadTotal: null });
  }

  limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({ titulo: r.titulo, autor: r.autor, editorial: r.editorial, cantidadTotal: r.cantidadTotal });
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
      return 'La cantidad no puede ser negativa';
    }
    if (control.errors['max']) {
      return 'La cantidad no puede pasar de 9999';
    }
    if (control.errors['pattern']) {
      return 'Solo números enteros';
    }
    return '';
  }

  private armarDatos(): DatosLibro {
    const v = this.form.value;
    return {
      titulo: (v.titulo ?? '').trim(),
      autor: (v.autor ?? '').trim(),
      editorial: (v.editorial ?? '').trim(),
      cantidadTotal: Number(v.cantidadTotal)
    };
  }
}