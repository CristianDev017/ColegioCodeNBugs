import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EmpleadoService } from '../../../core/empleado.service';
import { DatosEmpleado, Empleado, PUESTOS } from '../../../models/empleado';

@Component({
  selector: 'app-empleado-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './empleado-form.html'
})
export class EmpleadoForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Empleado;

  puestos = PUESTOS;
  mensajeExito: string = '';
  mensajeErrorServidor: string = '';
  hoy: string = this.fechaIso(new Date());
  hace18Anios: string = this.fechaIso(new Date(new Date().getFullYear() - 18, new Date().getMonth(), new Date().getDate()));

  textosPattern: { [ruta: string]: string } = {
    dpi: 'El DPI debe tener exactamente 13 números',
    telefono: 'El teléfono debe tener entre 8 y 15 números',
    salario: 'Solo números con hasta 2 decimales'
  };

  form = new FormGroup({
    nombres: new FormControl('', [Validators.required, Validators.maxLength(80)]),
    apellidos: new FormControl('', [Validators.required, Validators.maxLength(80)]),
    dpi: new FormControl('', [Validators.required, Validators.pattern(/^[0-9]{13}$/)]),
    fechaNacimiento: new FormControl('', [Validators.required]),
    telefono: new FormControl('', [Validators.pattern(/^[0-9]{8,15}$/)]),
    direccion: new FormControl('', [Validators.maxLength(200)]),
    puesto: new FormControl<string | null>(null, [Validators.required]),
    fechaContratacion: new FormControl('', [Validators.required]),
    salario: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0),
      Validators.max(99999999.99),
      Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)
    ])
  });

  constructor(private servicio: EmpleadoService) {}

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
    this.servicio.crear(this.armarDatos()).subscribe({
      next: () => {
        this.limpiarNuevo();
        this.mensajeExito = 'Empleado registrado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo registrar el empleado';
      }
    });
  }

  actualizar(): void {
    this.servicio.editar(this.registro!.id, this.armarDatos()).subscribe({
      next: (actualizado) => {
        this.registro = actualizado;
        this.mensajeExito = 'Empleado actualizado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo actualizar el empleado';
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
    this.form.reset({
      nombres: '',
      apellidos: '',
      dpi: '',
      fechaNacimiento: '',
      telefono: '',
      direccion: '',
      puesto: null,
      fechaContratacion: '',
      salario: null
    });
  }

  limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({
      nombres: r.nombres,
      apellidos: r.apellidos,
      dpi: r.dpi,
      fechaNacimiento: r.fechaNacimiento,
      telefono: r.telefono ?? '',
      direccion: r.direccion ?? '',
      puesto: r.puesto,
      fechaContratacion: r.fechaContratacion,
      salario: r.salario
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
    if (control.errors['maxlength']) {
      return `Máximo ${control.errors['maxlength'].requiredLength} caracteres`;
    }
    if (control.errors['pattern']) {
      return this.textosPattern[ruta] ?? 'El formato no es válido';
    }
    if (control.errors['min']) {
      return 'El salario no puede ser negativo';
    }
    if (control.errors['max']) {
      return 'El salario es demasiado grande';
    }
    return '';
  }

  private armarDatos(): DatosEmpleado {
    const v = this.form.value;
    return {
      nombres: this.texto(v.nombres),
      apellidos: this.texto(v.apellidos),
      dpi: this.texto(v.dpi),
      fechaNacimiento: this.texto(v.fechaNacimiento),
      telefono: this.nulo(v.telefono),
      direccion: this.nulo(v.direccion),
      puesto: this.texto(v.puesto),
      fechaContratacion: this.texto(v.fechaContratacion),
      salario: Number(v.salario)
    };
  }

  private texto(valor: string | null | undefined): string {
    return (valor ?? '').trim();
  }

  private nulo(valor: string | null | undefined): string | null {
    const limpio = (valor ?? '').trim();
    return limpio === '' ? null : limpio;
  }

  private fechaIso(fecha: Date): string {
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${fecha.getFullYear()}-${mes}-${dia}`;
  }
}