import { DecimalPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaestroService } from '../../core/maestro.service';
import { AlumnoNota, CursoMaestro, Ponderacion, RegistroNota } from '../../models/maestro';

@Component({
  selector: 'app-curso-notas',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './curso-notas.html'
})
export class CursoNotas implements OnInit {
  idAsignacion: number = 0;
  curso?: CursoMaestro;
  noEncontrado: boolean = false;
  actividades: Ponderacion[] = [];
  idPonderacion: number | null = null;
  alumnos: AlumnoNota[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 15;
  valores: { [idInscripcion: number]: number } = {};
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(private ruta: ActivatedRoute, private servicio: MaestroService) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.curso(this.idAsignacion).subscribe({
      next: (curso) => (this.curso = curso),
      error: () => (this.noEncontrado = true)
    });
    this.servicio.ponderaciones(this.idAsignacion, 1, 50).subscribe({
      next: (respuesta) => (this.actividades = respuesta.datos),
      error: (e) => (this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar las actividades')
    });
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get notaMaxima(): number {
    return this.actividades.find((a) => a.id === this.idPonderacion)?.notaMaxima ?? 0;
  }

  get cantidadConNota(): number {
    return Object.keys(this.valores).length;
  }

  get hayInvalidas(): boolean {
    return Object.keys(this.valores).some((clave) => this.esInvalida(Number(clave)));
  }

  elegirActividad(valor: string): void {
    this.idPonderacion = valor ? Number(valor) : null;
    this.valores = {};
    this.alumnos = [];
    this.total = 0;
    this.pagina = 1;
    this.mensajeExito = '';
    this.mensajeError = '';
    if (this.idPonderacion) {
      this.cargar();
    }
  }

  cargar(): void {
    this.cargando = true;
    this.servicio.notas(this.idAsignacion, this.idPonderacion!, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.alumnos = respuesta.datos;
        this.total = respuesta.total;
        for (const alumno of respuesta.datos) {
          if (alumno.valor != null && this.valores[alumno.idInscripcion] === undefined) {
            this.valores[alumno.idInscripcion] = alumno.valor;
          }
        }
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar la lista de notas';
        this.cargando = false;
      }
    });
  }

  cambiarValor(idInscripcion: number, texto: string): void {
    this.mensajeExito = '';
    if (texto === '') {
      delete this.valores[idInscripcion];
    } else {
      this.valores[idInscripcion] = Number(texto);
    }
  }

  esInvalida(idInscripcion: number): boolean {
    const valor = this.valores[idInscripcion];
    if (valor === undefined) {
      return false;
    }
    return valor < 0 || valor > this.notaMaxima || Math.round(valor * 100) / 100 !== valor;
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
    const notas: RegistroNota[] = Object.keys(this.valores).map((clave) => ({
      idInscripcion: Number(clave),
      valor: this.valores[Number(clave)]
    }));
    this.servicio.guardarNotas(this.idAsignacion, this.idPonderacion!, notas).subscribe({
      next: () => {
        this.mensajeExito = 'Notas guardadas correctamente';
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron guardar las notas';
      }
    });
  }

  anterior(): void {
    if (this.pagina > 1) {
      this.pagina--;
      this.cargar();
    }
  }

  siguiente(): void {
    if (this.pagina < this.totalPaginas) {
      this.pagina++;
      this.cargar();
    }
  }
}