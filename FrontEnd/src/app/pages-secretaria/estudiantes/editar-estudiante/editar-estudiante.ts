import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EstudianteService } from '../../../core/estudiante.service';
import { Estudiante } from '../../../models/estudiante';
import { EstudianteForm } from '../estudiante-form/estudiante-form';

@Component({
  selector: 'app-editar-estudiante',
  imports: [EstudianteForm, RouterLink],
  templateUrl: './editar-estudiante.html'
})
export class EditarEstudiante implements OnInit {
  id!: number;
  registro = signal<Estudiante | undefined>(undefined);
  existe = signal<boolean>(false);
  cargado = signal<boolean>(false);

  constructor(private ruta: ActivatedRoute, private servicio: EstudianteService) {}

  ngOnInit(): void {
    this.id = Number(this.ruta.snapshot.params['id']);
    this.servicio.obtener(this.id).subscribe({
      next: (estudiante) => {
        this.registro.set(estudiante);
        this.existe.set(true);
        this.cargado.set(true);
      },
      error: () => this.cargado.set(true)
    });
  }
}