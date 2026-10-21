import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CarreraService } from '../../core/carrera.service';
import { GradoService } from '../../core/grado.service';
import { NivelService } from '../../core/nivel.service';
import { Carrera } from '../../models/carrera';
import { DatosGrado, Grado } from '../../models/grado';
import { Nivel } from '../../models/nivel';

@Component({
  selector: 'app-grados',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './grados.html'
})
export class Grados implements OnInit {
  grados: Grado[] = [];
  niveles: Nivel[] = [];
  carreras: Carrera[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;

  buscar = '';
  idNivel = 0;

  mensaje = '';
  error = '';
  errorFormulario = '';

  mostrarFormulario = false;
  editandoId: number | null = null;
  formulario: FormGroup;

  gradoPendiente: Grado | null = null;

  constructor(
    private fb: FormBuilder,
    private servicio: GradoService,
    private nivelServicio: NivelService,
    private carreraServicio: CarreraService
  ) {
    this.formulario = this.fb.group({
      idNivel: [null, Validators.required],
      idCarrera: [null],
      nombre: ['', [Validators.required, Validators.maxLength(60)]],
      cupoDefault: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.nivelServicio.listar().subscribe({
      next: (niveles) => (this.niveles = niveles),
      error: () => (this.error = 'No se pudieron cargar los niveles')
    });
    this.carreraServicio.listarActivas().subscribe({
      next: (carreras) => (this.carreras = carreras),
      error: () => (this.error = 'No se pudieron cargar las carreras')
    });
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get esDiversificado(): boolean {
    const id = Number(this.formulario.get('idNivel')?.value);
    const nivel = this.niveles.find((n) => n.id === id);
    return nivel?.nombre === 'Diversificado';
  }

  cargar(): void {
    this.servicio.listar(this.buscar, this.idNivel, this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.grados = respuesta.datos;
        this.total = respuesta.total;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo cargar la lista de grados';
      }
    });
  }

  buscarGrados(texto: string): void {
    this.buscar = texto.trim();
    this.pagina = 1;
    this.cargar();
  }

  cambiarNivel(valor: string): void {
    this.idNivel = Number(valor);
    this.pagina = 1;
    this.cargar();
  }

  irAPagina(numero: number): void {
    if (numero < 1 || numero > this.totalPaginas) {
      return;
    }
    this.pagina = numero;
    this.cargar();
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario.reset();
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  abrirEditar(grado: Grado): void {
    this.editandoId = grado.id;
    this.formulario.setValue({
      idNivel: grado.idNivel,
      idCarrera: grado.idCarrera,
      nombre: grado.nombre,
      cupoDefault: grado.cupoDefault
    });
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    this.mostrarFormulario = false;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.value;
    if (this.esDiversificado && !valores.idCarrera) {
      this.errorFormulario = 'Seleccione la carrera del grado';
      return;
    }

    const datos: DatosGrado = {
      idNivel: Number(valores.idNivel),
      idCarrera: this.esDiversificado ? Number(valores.idCarrera) : null,
      nombre: valores.nombre,
      cupoDefault: Number(valores.cupoDefault)
    };

    const peticion = this.editandoId === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.editandoId, datos);

    peticion.subscribe({
      next: () => {
        this.mensaje = this.editandoId === null ? 'Grado creado' : 'Grado actualizado';
        this.error = '';
        this.formulario.reset();
        this.mostrarFormulario = false;
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje || 'No se pudo guardar el grado';
      }
    });
  }

  activar(grado: Grado): void {
    this.servicio.activar(grado.id).subscribe({
      next: () => {
        this.mensaje = 'Grado activado';
        this.error = '';
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo activar el grado';
        this.mensaje = '';
      }
    });
  }

  pedirDesactivar(grado: Grado): void {
    this.gradoPendiente = grado;
  }

  cancelarDesactivar(): void {
    this.gradoPendiente = null;
  }

  confirmarDesactivar(): void {
    if (!this.gradoPendiente) {
      return;
    }
    this.servicio.desactivar(this.gradoPendiente.id).subscribe({
      next: () => {
        this.mensaje = 'Grado desactivado';
        this.error = '';
        this.gradoPendiente = null;
        this.cargar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo desactivar el grado';
        this.mensaje = '';
        this.gradoPendiente = null;
      }
    });
  }
}