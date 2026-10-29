import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { CicloEscolarService } from '../../../core/ciclo-escolar.service';
import { EstudianteService } from '../../../core/estudiante.service';
import { SolvenciaService } from '../../../core/solvencia.service';
import { CicloEscolar } from '../../../models/ciclo-escolar';
import { Estudiante } from '../../../models/estudiante';
import { Solvencia, VerificacionSolvencia } from '../../../models/solvencia';

@Component({
  selector: 'app-crear-solvencia',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './crear-solvencia.html'
})
export class CrearSolvencia implements OnInit {
  ciclos: CicloEscolar[] = [];
  resultados: Estudiante[] = [];
  seleccionado: Estudiante | null = null;
  verificacion: VerificacionSolvencia | null = null;

  pagina = 1;
  tamano = 8;
  total = 0;

  ultima: Solvencia | null = null;
  mensajeExito = '';
  error = '';
  errorVerificacion = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private solvencias: SolvenciaService,
    private estudiantes: EstudianteService,
    private ciclosServicio: CicloEscolarService
  ) {
    this.form = this.fb.group({
      buscar: [''],
      idCiclo: [null]
    });

    this.form.get('buscar')!.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.pagina = 1;
      this.cargarEstudiantes();
    });
    this.form.get('idCiclo')!.valueChanges.subscribe(() => this.verificar());
  }

  ngOnInit(): void {
    this.ciclosServicio.listar(1, 50).subscribe({
      next: (r) => {
        this.ciclos = r.datos;
        const activo = this.ciclos.find((c) => c.estado.toUpperCase() === 'ACTIVO');
        if (activo) {
          this.form.patchValue({ idCiclo: activo.id });
        }
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar los años lectivos')
    });
    this.cargarEstudiantes();
  }

  get puedeEmitir(): boolean {
    return this.seleccionado !== null && this.form.value.idCiclo !== null && this.verificacion?.solvente === true;
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get totalPendiente(): number {
    if (!this.verificacion) {
      return 0;
    }
    return this.verificacion.pendientes.reduce((suma, p) => suma + p.monto, 0);
  }

  cargarEstudiantes(): void {
    const texto = String(this.form.value.buscar ?? '').trim();
    this.estudiantes.listar(texto, 'ACTIVO', this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.resultados = r.datos;
        this.total = r.total;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cargar la lista de estudiantes')
    });
  }

  irA(p: number): void {
    if (p < 1 || p > this.totalPaginas) {
      return;
    }
    this.pagina = p;
    this.cargarEstudiantes();
  }

  elegir(e: Estudiante): void {
    this.seleccionado = e;
    this.verificar();
  }

  quitarSeleccion(): void {
    this.seleccionado = null;
    this.verificacion = null;
    this.errorVerificacion = '';
  }

  verificar(): void {
    this.verificacion = null;
    this.errorVerificacion = '';
    const idCiclo = this.form.value.idCiclo;
    if (!this.seleccionado || idCiclo === null) {
      return;
    }
    this.solvencias.verificar(this.seleccionado.id, Number(idCiclo)).subscribe({
      next: (v) => (this.verificacion = v),
      error: (e) => (this.errorVerificacion = e?.error?.mensaje ?? 'No se pudo verificar la solvencia')
    });
  }

  emitir(): void {
    this.mensajeExito = '';
    this.error = '';
    this.ultima = null;
    if (!this.seleccionado || !this.puedeEmitir) {
      return;
    }
    const datos = { idEstudiante: this.seleccionado.id, idCiclo: Number(this.form.value.idCiclo) };
    this.solvencias.emitir(datos).subscribe({
      next: (s) => {
        this.ultima = s;
        this.mensajeExito = 'Solvencia emitida correctamente';
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo emitir la solvencia')
    });
  }

  limpiar(): void {
    this.seleccionado = null;
    this.verificacion = null;
    this.ultima = null;
    this.mensajeExito = '';
    this.error = '';
    this.errorVerificacion = '';
    this.form.patchValue({ buscar: '' }, { emitEvent: false });
    this.pagina = 1;
    this.cargarEstudiantes();
  }
}