import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PonderacionForm } from '../ponderacion-form/ponderacion-form';

@Component({
  selector: 'app-crear-ponderacion',
  imports: [PonderacionForm],
  templateUrl: './crear-ponderacion.html'
})
export class CrearPonderacion implements OnInit {
  idAsignacion: number = 0;

  constructor(private ruta: ActivatedRoute) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.ruta.snapshot.paramMap.get('id'));
  }
}