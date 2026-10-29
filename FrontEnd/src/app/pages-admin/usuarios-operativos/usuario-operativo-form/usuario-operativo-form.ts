import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UsuarioOperativoService } from '../../../core/usuario-operativo.service';
import {
  DatosUsuarioOperativo,
  OpcionEmpleado,
  ROLES_OPERATIVOS,
  UsuarioOperativo
} from '../../../models/usuario-operativo';

@Component({
  selector: 'app-usuario-operativo-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './usuario-operativo-form.html'
})
export class UsuarioOperativoForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: UsuarioOperativo;

  roles = ROLES_OPERATIVOS;
  empleados: OpcionEmpleado[] = [];
  empleadosCargados: boolean = false;
  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  textosPattern: { [ruta: string]: string } = {
    username: 'De 4 a 50 caracteres: letras, números, punto, guion o guion bajo',
    correo: 'El correo no tiene un formato válido'
  };

  form = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.pattern(/^[A-Za-z0-9._-]{4,50}$/)]),
    correo: new FormControl('', [
      Validators.required,
      Validators.maxLength(100),
      Validators.pattern(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)
    ]),
    password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]),
    rol: new FormControl<string | null>(null, [Validators.required]),
    idEmpleado: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(private servicio: UsuarioOperativoService) {}

  ngOnInit(): void {
    if (this.esEdicion) {
      this.form.get('username')!.disable();
      this.form.get('rol')!.disable();
      this.form.get('idEmpleado')!.disable();
      this.form.get('password')!.setValidators([Validators.minLength(6), Validators.maxLength(50)]);
      this.form.get('password')!.updateValueAndValidity();
    }
    this.limpiar();
  }

  cargarEmpleados(): void {
    this.form.patchValue({ idEmpleado: null });
    this.empleados = [];
    this.empleadosCargados = false;
    const rol = this.form.value.rol;
    if (!rol) {
      return;
    }
    this.servicio.empleadosDisponibles(rol).subscribe({
      next: (lista) => {
        this.empleados = lista;
        this.empleadosCargados = true;
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo cargar la lista de empleados';
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
        this.mensajeExito = 'Usuario creado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo crear el usuario';
      }
    });
  }

  actualizar(): void {
    this.servicio.editar(this.registro!.id, this.armarDatos()).subscribe({
      next: (actualizado) => {
        this.registro = actualizado;
        this.form.patchValue({ password: '' });
        this.mensajeExito = 'Usuario actualizado correctamente';
      },
      error: (e) => {
        this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo actualizar el usuario';
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
    this.form.reset({ username: '', correo: '', password: '', rol: null, idEmpleado: null });
    this.empleados = [];
    this.empleadosCargados = false;
  }

  limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({
      username: r.username,
      correo: r.correo,
      password: '',
      rol: r.rol,
      idEmpleado: r.idEmpleado ?? null
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
    if (control.errors['minlength'] || control.errors['maxlength']) {
      return ruta === 'password' ? 'La contraseña debe tener de 6 a 50 caracteres' : 'La longitud no es válida';
    }
    if (control.errors['pattern']) {
      return this.textosPattern[ruta] ?? 'El formato no es válido';
    }
    return '';
  }

  private armarDatos(): DatosUsuarioOperativo {
    const v = this.form.getRawValue();
    return {
      username: (v.username ?? '').trim(),
      correo: (v.correo ?? '').trim(),
      password: v.password ?? '',
      rol: v.rol ?? '',
      idEmpleado: Number(v.idEmpleado)
    };
  }
}