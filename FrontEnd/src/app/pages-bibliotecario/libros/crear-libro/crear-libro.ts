import { Component } from '@angular/core';
import { LibroForm } from '../libro-form/libro-form';

@Component({
  selector: 'app-crear-libro',
  imports: [LibroForm],
  templateUrl: './crear-libro.html'
})
export class CrearLibro {}