import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
})
export class Login {
  usuario = '';
  password = '';
  error = '';
  ingresando = false;

  constructor(
    private auth: Auth,
    private router: Router,
    private detector: ChangeDetectorRef,
  ) {}

  iniciarSesion() {
    if (this.ingresando) {
      return;
    }

    this.error = '';

    if (!this.usuario.trim() || !this.password) {
      this.error = 'Ingresa tu usuario y contraseña';
      this.detector.detectChanges();
      return;
    }

    this.ingresando = true;
    this.detector.detectChanges();

    this.auth.login(this.usuario.trim(), this.password).subscribe({
      next: (res) => {
        this.ingresando = false;

        sessionStorage.setItem('token', res.token);
        sessionStorage.setItem('rol', res.rol);

        if (res.rol === 'SUPER_ADMIN') {
          this.router.navigate(['/superadmin']);
        } else if (res.rol === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.error = 'Este rol todavía no tiene una pantalla disponible';
        }

        this.detector.detectChanges();
      },
      error: (e) => {
        this.ingresando = false;

        if (e.status === 401) {
          this.error = 'Correo o contraseña incorrectos';
        } else {
          this.error = 'No se pudo iniciar sesión. Inténtalo nuevamente';
        }

        this.detector.detectChanges();
      },
    });
  }
}
