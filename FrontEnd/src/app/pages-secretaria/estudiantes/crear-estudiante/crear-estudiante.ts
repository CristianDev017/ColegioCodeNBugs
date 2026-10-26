import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EstudianteForm } from '../estudiante-form/estudiante-form';

@Component({
  selector: 'app-crear-estudiante',
  imports: [EstudianteForm],
  templateUrl: './crear-estudiante.html'
})
export class CrearEstudiante {}