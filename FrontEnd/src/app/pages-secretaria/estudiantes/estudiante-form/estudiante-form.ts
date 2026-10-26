import { Component, Input, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EstudianteService } from '../../../core/estudiante.service';
import { DatosEstudiante, Estudiante } from '../../../models/estudiante';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-estudiante-form',
    imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './estudiante-form.html'
})
export class EstudianteForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Estudiante;

  guardado = signal<boolean>(false);
  mensajeExito = '';
  error = '';

  form: FormGroup;

  constructor(private fb: FormBuilder, private servicio: EstudianteService) {
    this.form = this.fb.group({
      nombres: ['', [Validators.required, Validators.maxLength(80)]],
      apellidos: ['', [Validators.required, Validators.maxLength(80)]],
      dpi: ['', [Validators.pattern(/^[0-9]{13}$/)]],
      fechaNacimiento: ['', [Validators.required]],
      genero: ['', [Validators.required]],
      direccion: ['', [Validators.maxLength(200)]],
      alergias: ['', [Validators.maxLength(500)]],
      condiciones: ['', [Validators.maxLength(500)]],
      contactoEmergencia: ['', [Validators.required, Validators.maxLength(100)]],
      telefonoEmergencia: ['', [Validators.required, Validators.pattern(/^[0-9]{8,15}$/)]],
      encargado: this.fb.group({
        nombres: ['', [Validators.required, Validators.maxLength(80)]],
        apellidos: ['', [Validators.required, Validators.maxLength(80)]],
        dpi: ['', [Validators.required, Validators.pattern(/^[0-9]{13}$/)]],
        telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{8,15}$/)]],
        correo: ['', [Validators.email, Validators.maxLength(100)]],
        direccion: ['', [Validators.maxLength(200)]],
        parentesco: ['', [Validators.required, Validators.maxLength(30)]]
      })
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
    this.servicio.crear(this.armarDatos()).subscribe({
      next: (e) => {
        this.limpiar();
        this.mensajeExito = `Estudiante registrado con el carné ${e.carnet}. Su usuario y contraseña inicial es el carné.`;
        this.guardado.set(true);
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo guardar el estudiante')
    });
  }

  private actualizar(): void {
    if (!this.registro) {
      return;
    }
    this.servicio.editar(this.registro.id, this.armarDatos()).subscribe({
      next: () => {
        this.mensajeExito = 'Los datos del estudiante se actualizaron';
        this.guardado.set(true);
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo actualizar el estudiante')
    });
  }

  limpiar(): void {
    if (this.esEdicion && this.registro) {
      this.limpiarEdicion();
    } else {
      this.limpiarNuevo();
    }
    this.guardado.set(false);
    this.error = '';
  }

  private limpiarNuevo(): void {
    this.form.reset({ genero: '' });
  }

  private limpiarEdicion(): void {
    const r = this.registro!;
    const f = r.ficha;
    const e = r.encargado;
    this.form.reset({
      nombres: r.nombres,
      apellidos: r.apellidos,
      dpi: r.dpi ?? '',
      fechaNacimiento: r.fechaNacimiento,
      genero: r.genero,
      direccion: r.direccion ?? '',
      alergias: f?.alergias ?? '',
      condiciones: f?.condiciones ?? '',
      contactoEmergencia: f?.contactoEmergencia ?? '',
      telefonoEmergencia: f?.telefonoEmergencia ?? '',
      encargado: {
        nombres: e?.nombres ?? '',
        apellidos: e?.apellidos ?? '',
        dpi: e?.dpi ?? '',
        telefono: e?.telefono ?? '',
        correo: e?.correo ?? '',
        direccion: e?.direccion ?? '',
        parentesco: e?.parentesco ?? ''
      }
    });
  }

  mensajeError(ruta: string): string {
    const campo = this.form.get(ruta);
    if (!campo || !campo.touched || !campo.errors) {
      return '';
    }
    if (campo.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (campo.errors['email']) {
      return 'El correo no es válido';
    }
    if (campo.errors['maxlength']) {
      return `Máximo ${campo.errors['maxlength'].requiredLength} caracteres`;
    }
    if (campo.errors['pattern']) {
      return 'Solo números y con la cantidad de dígitos indicada';
    }
    return 'Valor no válido';
  }

  private armarDatos(): DatosEstudiante {
    const v = this.form.getRawValue();
    const e = v.encargado;
    return {
      nombres: this.texto(v.nombres),
      apellidos: this.texto(v.apellidos),
      dpi: this.nulo(v.dpi),
      fechaNacimiento: v.fechaNacimiento,
      genero: v.genero,
      direccion: this.nulo(v.direccion),
      alergias: this.nulo(v.alergias),
      condiciones: this.nulo(v.condiciones),
      contactoEmergencia: this.texto(v.contactoEmergencia),
      telefonoEmergencia: this.texto(v.telefonoEmergencia),
      encargado: {
        nombres: this.texto(e.nombres),
        apellidos: this.texto(e.apellidos),
        dpi: this.texto(e.dpi),
        telefono: this.texto(e.telefono),
        correo: this.nulo(e.correo),
        direccion: this.nulo(e.direccion),
        parentesco: this.texto(e.parentesco)
      }
    };
  }

  private texto(valor: string | null): string {
    return (valor ?? '').trim();
  }

  private nulo(valor: string | null): string | null {
    const limpio = this.texto(valor);
    return limpio ? limpio : null;
  }
}