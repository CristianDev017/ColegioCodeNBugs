
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);

  const token = sessionStorage.getItem('token');
  const rol = sessionStorage.getItem('rol');

  if (!token || !rol) {
    return router.createUrlTree(['/login']);
  }

  const rolesPermitidos: string[] = route.data['roles'] || [];

  if (rol === 'SUPER_ADMIN') {
    return true;
  }

  if (rolesPermitidos.length === 0) {
    return true;
  }

  if (rolesPermitidos.includes(rol)) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
