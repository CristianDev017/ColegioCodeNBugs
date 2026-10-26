import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ActividadService } from '../../../core/actividad.service';
import { Actividad } from '../../../models/actividad';
import { ActividadForm } from '../actividad-form/actividad-form';

@Component({
  selector: 'app-editar-actividad',
  imports: [ActividadForm, RouterLink],
  templateUrl: './editar-actividad.html'
})
export class EditarActividad implements OnInit {
  id!: number;
  registro?: Actividad;
  existe = false;
  cargado = false;

  constructor(private ruta: ActivatedRoute, private servicio: ActividadService) {}

  ngOnInit(): void {
    this.id = Number(this.ruta.snapshot.params['id']);
    this.servicio.obtener(this.id).subscribe({
      next: (actividad) => {
        this.registro = actividad;
        this.existe = true;
        this.cargado = true;
      },
      error: () => (this.cargado = true)
    });
  }
}