import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GradoService } from '../../core/grado.service';
import { Grado } from '../../models/grado';

@Component({
  selector: 'app-grados-defecto',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './grados-defecto.html'
})
export class GradosDefecto implements OnInit {
  grados: Grado[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;
  buscar = '';

  mensaje = '';
  error = '';
  errorFormulario = '';

  gradoEditando: Grado | null = null;
  formulario: FormGroup;

  constructor(private fb: FormBuilder, private servicio: GradoService) {
    this.formulario = this.fb.group({
      cupoDefault: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    this.servicio.listar(this.buscar, 0, this.pagina, this.tamano).subscribe({
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

  irAPagina(numero: number): void {
    if (numero < 1 || numero > this.totalPaginas) {
      return;
    }
    this.pagina = numero;
    this.cargar();
  }

  abrirEditar(grado: Grado): void {
    this.gradoEditando = grado;
    this.formulario.setValue({ cupoDefault: grado.cupoDefault });
    this.errorFormulario = '';
  }

  cerrarFormulario(): void {
    this.gradoEditando = null;
  }

  guardar(): void {
    if (!this.gradoEditando) {
      return;
    }
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const cupo = Number(this.formulario.value.cupoDefault);
    this.servicio.cambiarCupo(this.gradoEditando.id, cupo).subscribe({
      next: () => {
        this.mensaje = 'Cupo por defecto actualizado';
        this.error = '';
        this.gradoEditando = null;
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje || 'No se pudo actualizar el cupo';
      }
    });
  }
}
