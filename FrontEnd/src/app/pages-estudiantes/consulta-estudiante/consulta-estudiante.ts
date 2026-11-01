import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { API_URL } from '../../servicios/rest-constants';

interface PerfilEstudiante {
  idEstudiante: number;
  carnet: string;
  nombres: string;
  apellidos: string;
  correo: string;
  fechaNacimiento: string;
  genero: string;
  direccion: string;
  estado: string;
}

interface NotaEstudiante {
  anio: number;
  curso: string;
  actividad: string;
  nota: number;
  notaMaxima: number;
}

interface PagoEstudiante {
  idPago: number;
  anio: number;
  concepto: string;
  detalle: string;
  fechaPago: string;
  monto: number;
  comprobante: string | null;
}

interface ConsultaEstudianteRespuesta {
  perfil: PerfilEstudiante;
  notas: NotaEstudiante[];
  pagos: PagoEstudiante[];
  totalPagado: number;
  saldoPendiente: number;
}

@Component({
  selector: 'app-consulta-estudiante',
  imports: [RouterLink],
  templateUrl: './consulta-estudiante.html'
})
export class ConsultaEstudiante implements OnInit {

  perfil: PerfilEstudiante | null = null;

  notas: NotaEstudiante[] = [];
  pagos: PagoEstudiante[] = [];

  totalPagado = 0;
  saldoPendiente = 0;

  anioSeleccionado = 0;
  anios: number[] = [];

  cargando = false;
  mensajeError = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.http.get<ConsultaEstudianteRespuesta>(
      `${API_URL}/estudiante/consulta`
    ).subscribe({
      next: (respuesta) => {
        this.perfil = respuesta.perfil;
        this.notas = respuesta.notas;
        this.pagos = respuesta.pagos;
        this.totalPagado = respuesta.totalPagado;
        this.saldoPendiente = respuesta.saldoPendiente;

        this.anios = [...new Set(this.notas.map(n => n.anio))]
          .sort((a, b) => b - a);

        this.cargando = false;
      },
      error: (error) => {
        this.mensajeError =
          error?.error?.mensaje ?? 'No se pudieron cargar los datos';

        this.cargando = false;
      }
    });
  }

  get notasFiltradas(): NotaEstudiante[] {
    if (this.anioSeleccionado === 0) {
      return this.notas;
    }

    return this.notas.filter(
      nota => nota.anio === this.anioSeleccionado
    );
  }

  cambiarAnio(evento: Event): void {
    const select = evento.target as HTMLSelectElement;
    this.anioSeleccionado = Number(select.value);
  }
}