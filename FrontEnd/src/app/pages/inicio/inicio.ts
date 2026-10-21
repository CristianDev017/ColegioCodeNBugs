import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-inicio',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './inicio.html',
})
export class Inicio {

  constructor(public auth: AuthService) {}

  cerrarSesion(): void {
    this.auth.logout();
  }
}