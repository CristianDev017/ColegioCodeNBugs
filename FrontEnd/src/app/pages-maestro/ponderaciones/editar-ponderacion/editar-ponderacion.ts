import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaestroService } from '../../../core/maestro.service';
import { Ponderacion } from '../../../models/maestro';
import { PonderacionForm } from '../ponderacion-form/ponderacion-form';

@Component({
  selector: 'app-editar-ponderacion',
  imports: [PonderacionForm, RouterLink],
  templateUrl: './editar-ponderacion.html'
})
export class EditarPonderacion implements OnInit {
  idAsignacion: number = 0;
  registro?: Ponderacion;
  cargando: boolean = true;
  noEncontrado: boolean = false;

  constructor(private ruta: ActivatedRoute, private servicio: MaestroService) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.ruta.snapshot.paramMap.get('id'));
    const idPonderacion = Number(this.ruta.snapshot.paramMap.get('idPonderacion'));
    this.servicio.ponderacion(this.idAsignacion, idPonderacion).subscribe({
      next: (ponderacion) => {
        this.registro = ponderacion;
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.cargando = false;
      }
    });
  }
}