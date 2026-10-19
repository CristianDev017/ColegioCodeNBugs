import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  formulario: FormGroup;
  cargando = false;
  error = '';

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.formulario = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (this.auth.estaLogueado()) {
      this.router.navigate(['/inicio']);
    }
  }

  ingresar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.cargando = true;
    this.error = '';

    const { username, password } = this.formulario.value;
    this.auth.login(username, password).subscribe({
      next: () => this.router.navigate(['/inicio']),
      error: (e) => {
        this.error = e.error?.mensaje ?? 'No se pudo conectar con el servidor';
        this.cargando = false;
      }
    });
  }
}