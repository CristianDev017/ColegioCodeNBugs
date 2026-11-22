
import { Component, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.html'
})
export class App implements OnDestroy {

  sesionActiva = false;
  rol = '';

  private suscripcion: Subscription;

  constructor(
    private router: Router,
    private detector: ChangeDetectorRef
  ) {
    this.actualizarSesion();

    this.suscripcion = this.router.events
      .pipe(filter(evento => evento instanceof NavigationEnd))
      .subscribe(() => {
        this.actualizarSesion();
        this.detector.detectChanges();
      });
  }

  actualizarSesion() {
    const token = sessionStorage.getItem('token');

    this.sesionActiva = !!token && !this.router.url.startsWith('/login');
    this.rol = sessionStorage.getItem('rol') || '';
  }

  cerrarSesion() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('rol');

    this.sesionActiva = false;
    this.rol = '';

    this.router.navigate(['/login']);
    this.detector.detectChanges();
  }

  ngOnDestroy() {
    this.suscripcion.unsubscribe();
  }
}
