import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CursoService } from '../../core/curso.service';
import { Curso } from '../../models/curso';

@Component({
  selector: 'app-cursos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cursos.html'
})
export class Cursos implements OnInit {
  cursos: Curso[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;

  mostrarForm = false;
  editandoId: number | null = null;
  confirmando: Curso | null = null;

  mensaje = '';
  error = '';

  form: FormGroup;

  constructor(private fb: FormBuilder, private servicio: CursoService) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(255)]]
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    this.servicio.listar(this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.cursos = r.datos;
        this.total = r.total;
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo cargar la lista'))
    });
  }

  irA(p: number): void {
    if (p < 1 || p > this.totalPaginas) {
      return;
    }
    this.pagina = p;
    this.cargar();
  }

  nuevo(): void {
    this.limpiarMensajes();
    this.editandoId = null;
    this.form.reset({ nombre: '', descripcion: '' });
    this.mostrarForm = true;
  }

  editar(c: Curso): void {
    this.limpiarMensajes();
    this.editandoId = c.id;
    this.form.reset({ nombre: c.nombre, descripcion: c.descripcion ?? '' });
    this.mostrarForm = true;
  }

  cancelar(): void {
    this.mostrarForm = false;
    this.editandoId = null;
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.limpiarMensajes();
    const valores = this.form.value;
    const datos = {
      nombre: valores.nombre,
      descripcion: valores.descripcion ? valores.descripcion : null
    };
    const peticion = this.editandoId === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.editandoId, datos);

    peticion.subscribe({
      next: () => {
        this.mensaje = this.editandoId === null ? 'Curso creado' : 'Curso actualizado';
        this.cancelar();
        this.form.reset({ nombre: '', descripcion: '' });
        this.cargar();
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo guardar el curso'))
    });
  }

  pedirDesactivar(c: Curso): void {
    this.limpiarMensajes();
    this.confirmando = c;
  }

  cancelarConfirmacion(): void {
    this.confirmando = null;
  }

  confirmarDesactivar(): void {
    if (!this.confirmando) {
      return;
    }
    const id = this.confirmando.id;
    this.confirmando = null;
    this.servicio.desactivar(id).subscribe({
      next: () => {
        this.mensaje = 'Curso desactivado';
        this.cargar();
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo desactivar el curso'))
    });
  }

  activar(c: Curso): void {
    this.limpiarMensajes();
    this.servicio.activar(c.id).subscribe({
      next: () => {
        this.mensaje = 'Curso activado';
        this.cargar();
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo activar el curso'))
    });
  }

  private limpiarMensajes(): void {
    this.mensaje = '';
    this.error = '';
  }

  private textoError(e: any, porDefecto: string): string {
    return e?.error?.mensaje ?? porDefecto;
  }
}