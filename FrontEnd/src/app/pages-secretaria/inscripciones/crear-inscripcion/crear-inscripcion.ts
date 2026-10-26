import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { EstudianteService } from '../../../core/estudiante.service';
import { InscripcionService } from '../../../core/inscripcion.service';
import { Estudiante } from '../../../models/estudiante';
import { SeccionDisponible } from '../../../models/inscripcion';

@Component({
  selector: 'app-crear-inscripcion',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './crear-inscripcion.html'
})
export class CrearInscripcion implements OnInit {
  secciones: SeccionDisponible[] = [];
  resultados: Estudiante[] = [];
  seleccionado: Estudiante | null = null;

  pagina = 1;
  tamano = 8;
  total = 0;

  mensajeExito = '';
  error = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private inscripciones: InscripcionService,
    private estudiantes: EstudianteService
  ) {
    this.form = this.fb.group({
      buscar: [''],
      idSeccion: [null]
    });
    this.form.get('buscar')!.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.pagina = 1;
      this.cargarEstudiantes();
    });
  }

  ngOnInit(): void {
    this.cargarSecciones();
    this.cargarEstudiantes();
  }

  get puedeGuardar(): boolean {
    return this.seleccionado !== null && this.form.value.idSeccion !== null;
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargarSecciones(): void {
    this.inscripciones.seccionesDisponibles().subscribe({
      next: (lista) => (this.secciones = lista),
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar las secciones')
    });
  }

  cargarEstudiantes(): void {
    const texto = (this.form.value.buscar ?? '').trim();
    this.estudiantes.listar(texto, 'ACTIVO', this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.resultados = r.datos;
        this.total = r.total;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cargar la lista de estudiantes')
    });
  }

  anterior(): void {
    if (this.pagina > 1) {
      this.pagina--;
      this.cargarEstudiantes();
    }
  }

  siguiente(): void {
    if (this.pagina < this.totalPaginas) {
      this.pagina++;
      this.cargarEstudiantes();
    }
  }

  elegir(e: Estudiante): void {
    this.seleccionado = e;
  }

  quitarSeleccion(): void {
    this.seleccionado = null;
  }

  inscribir(): void {
    this.mensajeExito = '';
    this.error = '';
    if (!this.seleccionado || this.form.value.idSeccion === null) {
      return;
    }
    this.inscripciones.inscribir(this.seleccionado.id, this.form.value.idSeccion).subscribe({
      next: (i) => {
        this.mensajeExito = `${i.estudiante} quedó inscrito en ${i.grado}, sección ${i.seccion}`;
        this.seleccionado = null;
        this.form.reset({ buscar: '', idSeccion: null }, { emitEvent: false });
        this.pagina = 1;
        this.cargarSecciones();
        this.cargarEstudiantes();
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo realizar la inscripción')
    });
  }
}