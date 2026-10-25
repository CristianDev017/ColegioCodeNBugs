import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CicloEscolarService } from '../../core/ciclo-escolar.service';
import { GradoService } from '../../core/grado.service';
import { CursoService } from '../../core/curso.service';
import { CurriculoService } from '../../core/curriculo.service';
import { CicloEscolar } from '../../models/ciclo-escolar';
import { Grado } from '../../models/grado';
import { Curso } from '../../models/curso';
import { Curriculo } from '../../models/curriculo';

@Component({
  selector: 'app-curriculo',
  imports: [ReactiveFormsModule],
  templateUrl: './curriculo.html'
})
export class GestionCurriculo implements OnInit {
  ciclos: CicloEscolar[] = [];
  grados: Grado[] = [];
  cursosActivos: Curso[] = [];
  curriculo: Curriculo | null = null;
  confirmando: Curso | null = null;

  mensaje = '';
  error = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private ciclosServicio: CicloEscolarService,
    private gradosServicio: GradoService,
    private cursosServicio: CursoService,
    private servicio: CurriculoService
  ) {
    this.form = this.fb.group({
      ciclo: [null],
      grado: [null],
      curso: [null]
    });
  }

  ngOnInit(): void {
    this.ciclosServicio.listar(1, 50).subscribe({
      next: (r) => (this.ciclos = r.datos),
      error: (e) => (this.error = this.textoError(e, 'No se pudieron cargar los años lectivos'))
    });
    this.gradosServicio.listar('', 0, 1, 50).subscribe({
      next: (r) => (this.grados = r.datos.filter((g) => g.activo)),
      error: (e) => (this.error = this.textoError(e, 'No se pudieron cargar los grados'))
    });
    this.cursosServicio.listarActivos().subscribe({
      next: (c) => (this.cursosActivos = c),
      error: (e) => (this.error = this.textoError(e, 'No se pudieron cargar los cursos'))
    });
  }

  get cerrado(): boolean {
    const ciclo = this.ciclos.find((c) => c.id === this.form.value.ciclo);
    return ciclo ? ciclo.estado.toUpperCase() === 'CERRADO' : false;
  }

  get cursosDisponibles(): Curso[] {
    const usados = this.curriculo ? this.curriculo.cursos.map((c) => c.id) : [];
    return this.cursosActivos.filter((c) => !usados.includes(c.id));
  }

  nombreGrado(g: Grado): string {
    return g.carrera ? `${g.nivel} - ${g.nombre} (${g.carrera})` : `${g.nivel} - ${g.nombre}`;
  }

  consultar(): void {
    this.limpiarMensajes();
    this.curriculo = null;
    const ciclo = this.form.value.ciclo;
    const grado = this.form.value.grado;
    if (!ciclo || !grado) {
      return;
    }
    this.servicio.consultar(ciclo, grado).subscribe({
      next: (c) => (this.curriculo = c),
      error: (e) => (this.error = this.textoError(e, 'No se pudo cargar el currículo'))
    });
  }

  agregar(): void {
    this.limpiarMensajes();
    const { ciclo, grado, curso } = this.form.value;
    if (!ciclo || !grado || !curso) {
      this.error = 'Elige el curso que quieres agregar';
      return;
    }
    this.servicio.agregarCurso(ciclo, grado, curso).subscribe({
      next: (c) => {
        this.curriculo = c;
        this.form.patchValue({ curso: null });
        this.mensaje = 'Curso agregado al currículo';
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo agregar el curso'))
    });
  }

  pedirQuitar(c: Curso): void {
    this.limpiarMensajes();
    this.confirmando = c;
  }

  cancelarQuitar(): void {
    this.confirmando = null;
  }

  confirmarQuitar(): void {
    if (!this.confirmando || !this.curriculo) {
      return;
    }
    const idCurso = this.confirmando.id;
    this.confirmando = null;
    this.servicio.quitarCurso(this.curriculo.id, idCurso).subscribe({
      next: (c) => {
        this.curriculo = c;
        this.mensaje = 'Curso quitado del currículo';
      },
      error: (e) => (this.error = this.textoError(e, 'No se pudo quitar el curso'))
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