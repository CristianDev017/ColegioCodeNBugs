import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CicloEscolarService } from '../../../core/ciclo-escolar.service';
import { GradoService } from '../../../core/grado.service';
import { TarifaService } from '../../../core/tarifa.service';
import { CicloEscolar } from '../../../models/ciclo-escolar';
import { Grado } from '../../../models/grado';
import { DatosTarifa, Tarifa } from '../../../models/tarifa';

@Component({
  selector: 'app-tarifa-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './tarifa-form.html'
})
export class TarifaForm implements OnInit {
  @Input() esEdicion: boolean = false;
  @Input() registro?: Tarifa;

  ciclos: CicloEscolar[] = [];
  grados: Grado[] = [];

  guardado = false;
  mensajeExito = '';
  error = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private tarifas: TarifaService,
    private ciclosServicio: CicloEscolarService,
    private gradosServicio: GradoService
  ) {
    this.form = this.fb.group({
      idCiclo: [null, [Validators.required]],
      idGrado: [null, [Validators.required]],
      montoInscripcion: [null, [Validators.required, Validators.min(0), Validators.max(99999999.99)]],
      montoMensualidad: [null, [Validators.required, Validators.min(0), Validators.max(99999999.99)]],
      diaVencimiento: [null, [Validators.required, Validators.min(1), Validators.max(31)]]
    });
  }

  ngOnInit(): void {
    if (this.esEdicion) {
      this.form.get('idCiclo')?.disable();
      this.form.get('idGrado')?.disable();
    } else {
      this.cargarOpciones();
    }
    this.limpiar();
  }

  get ciclosDisponibles(): CicloEscolar[] {
    return this.ciclos.filter((c) => c.estado.toUpperCase() !== 'CERRADO');
  }

  private cargarOpciones(): void {
    this.ciclosServicio.listar(1, 50).subscribe({
      next: (r) => (this.ciclos = r.datos),
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar los años lectivos')
    });
    this.gradosServicio.listar('', 0, 1, 50).subscribe({
      next: (r) => (this.grados = r.datos.filter((g) => g.activo)),
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar los grados')
    });
  }

  nombreGrado(g: Grado): string {
    return g.carrera ? `${g.nivel} - ${g.nombre} (${g.carrera})` : `${g.nivel} - ${g.nombre}`;
  }

  enviar(): void {
    this.error = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.esEdicion) {
      this.actualizar();
    } else {
      this.guardarNuevo();
    }
  }

  private guardarNuevo(): void {
    this.tarifas.crear(this.armarDatos()).subscribe({
      next: () => {
        this.limpiar();
        this.mensajeExito = 'Tarifa guardada correctamente';
        this.guardado = true;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo guardar la tarifa')
    });
  }

  private actualizar(): void {
    if (!this.registro) {
      return;
    }
    this.tarifas.editar(this.registro.id, this.armarDatos()).subscribe({
      next: () => {
        this.mensajeExito = 'Tarifa actualizada correctamente';
        this.guardado = true;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo actualizar la tarifa')
    });
  }

  limpiar(): void {
    if (this.esEdicion && this.registro) {
      this.limpiarEdicion();
    } else {
      this.limpiarNuevo();
    }
    this.guardado = false;
    this.error = '';
  }

  private limpiarNuevo(): void {
    this.form.reset({ idCiclo: null, idGrado: null });
  }

  private limpiarEdicion(): void {
    const r = this.registro!;
    this.form.reset({
      idCiclo: r.idCiclo,
      idGrado: r.idGrado,
      montoInscripcion: r.montoInscripcion,
      montoMensualidad: r.montoMensualidad,
      diaVencimiento: r.diaVencimiento
    });
  }

  mensajeError(ruta: string): string {
    const campo = this.form.get(ruta);
    if (!campo || !campo.touched || !campo.errors) {
      return '';
    }
    if (campo.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (campo.errors['min']) {
      return `El valor mínimo es ${campo.errors['min'].min}`;
    }
    if (campo.errors['max']) {
      return `El valor máximo es ${campo.errors['max'].max}`;
    }
    return 'Valor no válido';
  }

  private armarDatos(): DatosTarifa {
    const v = this.form.getRawValue();
    return {
      idGrado: Number(v.idGrado),
      idCiclo: Number(v.idCiclo),
      montoInscripcion: Number(v.montoInscripcion),
      montoMensualidad: Number(v.montoMensualidad),
      diaVencimiento: Number(v.diaVencimiento)
    };
  }
}