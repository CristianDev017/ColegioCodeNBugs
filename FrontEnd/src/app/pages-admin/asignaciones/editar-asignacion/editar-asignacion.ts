import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AsignacionService } from '../../../core/asignacion.service';
import { Asignacion } from '../../../models/asignacion';
import { AsignacionForm } from '../asignacion-form/asignacion-form';

@Component({
  selector: 'app-editar-asignacion',
  imports: [AsignacionForm, RouterLink],
  templateUrl: './editar-asignacion.html'
})
export class EditarAsignacion implements OnInit {
  registro?: Asignacion;
  cargando: boolean = true;
  noEncontrado: boolean = false;

  constructor(private ruta: ActivatedRoute, private servicio: AsignacionService) {}

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.obtener(id).subscribe({
      next: (asignacion) => {
        this.registro = asignacion;
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.cargando = false;
      }
    });
  }
}