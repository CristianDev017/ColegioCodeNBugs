import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaestroService } from '../../core/maestro.service';
import { AlumnoNomina, CursoMaestro, RegistroAsistencia } from '../../models/maestro';

@Component({
  selector: 'app-curso-asistencia',
  imports: [RouterLink],
  templateUrl: './curso-asistencia.html'
})
export class CursoAsistencia implements OnInit {
  idAsignacion: number = 0;
  curso?: CursoMaestro;
  noEncontrado: boolean = false;
  alumnos: AlumnoNomina[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 15;
  hoy: string = '';
  fecha: string = '';
  marcas: { [idInscripcion: number]: string } = {};
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(private ruta: ActivatedRoute, private servicio: MaestroService) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.ruta.snapshot.paramMap.get('id'));
    this.hoy = this.fechaIso(new Date());
    this.fecha = this.hoy;
    this.servicio.curso(this.idAsignacion).subscribe({
      next: (curso) => {
        this.curso = curso;
        this.cargar();
      },
      error: () => {
        this.noEncontrado = true;
      }
    });
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get cantidadMarcados(): number {
    return Object.keys(this.marcas).length;
  }

  cargar(): void {
    this.cargando = true;
    this.servicio.nomina(this.idAsignacion, this.fecha, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.alumnos = respuesta.datos;
        this.total = respuesta.total;
        for (const alumno of respuesta.datos) {
          if (alumno.estado && this.marcas[alumno.idInscripcion] === undefined) {
            this.marcas[alumno.idInscripcion] = alumno.estado;
          }
        }
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar la nómina';
        this.cargando = false;
      }
    });
  }

  cambiarFecha(valor: string): void {
    if (!valor) {
      return;
    }
    this.fecha = valor;
    this.marcas = {};
    this.pagina = 1;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.cargar();
  }

  marcar(idInscripcion: number, estado: string): void {
    this.marcas[idInscripcion] = estado;
    this.mensajeExito = '';
  }

  todosPresentes(): void {
    for (const alumno of this.alumnos) {
      this.marcas[alumno.idInscripcion] = 'PRESENTE';
    }
    this.mensajeExito = '';
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
    const registros: RegistroAsistencia[] = Object.keys(this.marcas).map((clave) => ({
      idInscripcion: Number(clave),
      estado: this.marcas[Number(clave)]
    }));
    this.servicio.guardarAsistencia(this.idAsignacion, this.fecha, registros).subscribe({
      next: () => {
        this.mensajeExito = 'Asistencia guardada correctamente';
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo guardar la asistencia';
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

  private fechaIso(fecha: Date): string {
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${fecha.getFullYear()}-${mes}-${dia}`;
  }
}