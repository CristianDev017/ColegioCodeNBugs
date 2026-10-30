import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BibliotecaService } from '../../../core/biblioteca.service';
import { ConfigBiblioteca, OpcionBiblioteca } from '../../../models/biblioteca';

@Component({
  selector: 'app-crear-prestamo',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './crear-prestamo.html'
})
export class CrearPrestamo implements OnInit {
  libros: OpcionBiblioteca[] = [];
  solicitantes: OpcionBiblioteca[] = [];
  config?: ConfigBiblioteca;
  mensajeExito: string = '';
  mensajeErrorServidor: string = '';

  form = new FormGroup({
    tipoSolicitante: new FormControl('ESTUDIANTE', [Validators.required]),
    idSolicitante: new FormControl<number | null>(null, [Validators.required]),
    idLibro: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(private servicio: BibliotecaService) {}

  ngOnInit(): void {
    this.servicio.config().subscribe({ next: (c) => (this.config = c) });
    this.cargarLibros();
    this.cargarSolicitantes();

    this.form.controls.tipoSolicitante.valueChanges.subscribe(() => {
      this.form.patchValue({ idSolicitante: null });
      this.cargarSolicitantes();
    });
  }

  cargarLibros(): void {
    this.servicio.opcionesLibros('').subscribe({ next: (lista) => (this.libros = lista) });
  }

  cargarSolicitantes(): void {
    const tipo = this.form.value.tipoSolicitante ?? 'ESTUDIANTE';
    this.servicio.opcionesSolicitantes(tipo, '').subscribe({ next: (lista) => (this.solicitantes = lista) });
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeErrorServidor = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.value;
    const tipo = v.tipoSolicitante === 'ESTUDIANTE' ? 'ESTUDIANTE' : 'EMPLEADO';
    this.servicio
      .crearPrestamo({
        idLibro: Number(v.idLibro),
        tipoSolicitante: tipo,
        idSolicitante: Number(v.idSolicitante)
      })
      .subscribe({
        next: (p) => {
          this.limpiar();
          this.mensajeExito = `Préstamo registrado. Fecha esperada de devolución: ${p.fechaEsperada}`;
        },
        error: (e) => {
          this.mensajeErrorServidor = e?.error?.mensaje ?? 'No se pudo registrar el préstamo';
        }
      });
  }

  limpiar(): void {
    this.form.reset({ tipoSolicitante: 'ESTUDIANTE', idSolicitante: null, idLibro: null });
    this.cargarLibros();
    this.cargarSolicitantes();
  }
}